package com.music.spotui.data.update

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * In-app updates (IT-63): downloads the release APK into the cache and installs it
 * through [PackageInstaller].
 *
 * Android never lets a sideloaded app update itself fully silently the first time:
 * the user confirms once (and allows "install unknown apps"). From then on Spotui is
 * the *installer of record*, so on Android 12+ `USER_ACTION_NOT_REQUIRED` lets the
 * following updates install without the system dialog.
 */
object ApkUpdater {

    private const val TAG = "ApkUpdater"
    private const val ACTION_INSTALL_STATUS = "com.music.spotui.UPDATE_INSTALL_STATUS"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /** Download progress 0..1 while downloading, null otherwise. */
    private val _progress = MutableStateFlow<Float?>(null)
    val progress: StateFlow<Float?> = _progress.asStateFlow()

    /** The APK ready to install, once downloaded. */
    private val _ready = MutableStateFlow<File?>(null)
    val ready: StateFlow<File?> = _ready.asStateFlow()

    // Outlives the dialog: closing it doesn't cancel a running download.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    /** Starts (or reuses) the download in the background; observe [progress] / [ready]. */
    fun startDownload(context: Context, info: UpdateChecker.UpdateInfo) {
        if (job?.isActive == true || !isDirectApk(info)) return
        val app = context.applicationContext
        job = scope.launch {
            _progress.value = 0f
            val file = download(app, info) { _progress.value = it }
            _progress.value = null
            _ready.value = file
            if (file == null) {
                launch(Dispatchers.Main) {
                    Toast.makeText(app, "Update download failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dir(context: Context) = File(context.cacheDir, "updates").apply { mkdirs() }

    private fun apkFile(context: Context, version: String) = File(dir(context), "Spotui_v$version.apk")

    /** Only a real `.apk` asset can be installed; otherwise the release page is opened. */
    fun isDirectApk(info: UpdateChecker.UpdateInfo) = info.downloadUrl.endsWith(".apk", ignoreCase = true)

    /** True when the network won't cost the user data (Wi-Fi, ethernet…). */
    fun isUnmetered(context: Context): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return false
        return !cm.isActiveNetworkMetered
    }

    /** The already-downloaded and validated APK for [version], if any. */
    fun downloaded(context: Context, version: String): File? =
        apkFile(context, version).takeIf { it.exists() && validate(context, it) }

    /** Deletes downloaded APKs (call once the installed version is up to date). */
    fun clean(context: Context) {
        dir(context).listFiles()?.forEach { it.delete() }
    }

    /** Downloads the APK (reusing a previous complete download). Returns null on failure. */
    private suspend fun download(
        context: Context,
        info: UpdateChecker.UpdateInfo,
        onProgress: (Float) -> Unit,
    ): File? = withContext(Dispatchers.IO) {
        downloaded(context, info.version)?.let { return@withContext it }
        dir(context).listFiles()?.forEach { it.delete() } // older versions / partial files
        val target = apkFile(context, info.version)
        val part = File(target.path + ".part")
        runCatching {
            client.newCall(Request.Builder().url(info.downloadUrl).build()).execute().use { resp ->
                if (!resp.isSuccessful) error("HTTP ${resp.code}")
                val body = resp.body
                val total = body.contentLength()
                body.byteStream().use { input ->
                    part.outputStream().use { out ->
                        val buf = ByteArray(64 * 1024)
                        var done = 0L
                        while (true) {
                            val n = input.read(buf)
                            if (n < 0) break
                            out.write(buf, 0, n)
                            done += n
                            if (total > 0) onProgress(done.toFloat() / total)
                        }
                    }
                }
            }
            if (!part.renameTo(target)) error("rename failed")
            if (!validate(context, target)) {
                target.delete()
                error("downloaded file is not a newer Spotui APK")
            }
            target
        }.onFailure {
            part.delete()
            Log.w(TAG, "update download failed: ${it.message}")
        }.getOrNull()
    }

    /** Same package and a higher versionCode than the installed one. */
    private fun validate(context: Context, file: File): Boolean {
        val pm = context.packageManager
        val archive: PackageInfo = pm.getPackageArchiveInfo(file.path, 0) ?: return false
        if (archive.packageName != context.packageName) return false
        val installed = pm.getPackageInfo(context.packageName, 0)
        return versionCode(archive) > versionCode(installed)
    }

    @Suppress("DEPRECATION")
    private fun versionCode(info: PackageInfo): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else info.versionCode.toLong()

    /**
     * Installs [file]. If the user hasn't allowed Spotui to install apps yet, opens that
     * setting instead and returns false (the caller lets them tap Install again).
     */
    fun install(context: Context, file: File): Boolean {
        val pm = context.packageManager
        if (!pm.canRequestPackageInstalls()) {
            runCatching {
                context.startActivity(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            Toast.makeText(context, "Allow Spotui to install updates, then tap Install again", Toast.LENGTH_LONG).show()
            return false
        }
        return runCatching {
            val installer = pm.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
                setAppPackageName(context.packageName)
                setSize(file.length())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    setRequestUpdateOwnership(true)
                }
            }
            val sessionId = installer.createSession(params)
            installer.openSession(sessionId).use { session ->
                file.inputStream().use { input ->
                    session.openWrite("spotui.apk", 0, file.length()).use { out ->
                        input.copyTo(out)
                        session.fsync(out)
                    }
                }
                val intent = Intent(context, UpdateInstallReceiver::class.java).setAction(ACTION_INSTALL_STATUS)
                val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
                val pending = PendingIntent.getBroadcast(context, sessionId, intent, flags)
                session.commit(pending.intentSender)
            }
            true
        }.onFailure {
            Log.w(TAG, "install failed: ${it.message}")
            Toast.makeText(context, "Update install failed: ${it.message}", Toast.LENGTH_LONG).show()
        }.getOrDefault(false)
    }
}

/** Receives the PackageInstaller result: shows the system confirmation when needed. */
class UpdateInstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                @Suppress("DEPRECATION")
                val confirm: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_INTENT)
                }
                confirm?.let {
                    runCatching { context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                }
            }
            PackageInstaller.STATUS_SUCCESS -> Unit // the app is replaced (and this process killed)
            else -> {
                val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                Log.w("ApkUpdater", "install status $status: $msg")
                if (status != PackageInstaller.STATUS_FAILURE_ABORTED) {
                    Toast.makeText(context, "Update not installed: ${msg ?: status}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
