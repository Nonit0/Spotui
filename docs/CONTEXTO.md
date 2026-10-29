# Contexto de Spotui

> Documento de referencia para cualquier persona o agente que vaya a trabajar en el repo.
> Repo de trabajo: **`Nonit0/Spotui`**, todavía sin releases propias.
> Base de código: **`H4zh4n/Spotui`** 1.5.0 (commit `9180164`, `versionCode 202608150`), rama
> `update-h4zh4n` (antes `base-h4zh4n`, ver IT-18), que desde IT-50 es también `main` en GitHub. Versión propia:
> 1.6.0 (IT-49). La base anterior (`Spotui/Spotui` 1.6) está archivada en la rama
> `archivo/original-1.6`. Estado descrito: 28-10-2026.

## 1. Qué es

Spotui es un **clon de Spotify para Android** hecho con **Kotlin + Jetpack Compose**.
Se conecta a la cuenta real de Spotify del usuario para obtener **metadatos** (playlists,
álbumes, artistas, letras, recomendaciones, biblioteca, likes), pero **el audio no sale de
Spotify**: se resuelve en tiempo de reproducción desde otras fuentes (Deezer, SpotiFLAC, Amazon,
Qobuz, TIDAL, SoundCloud, YouTube Music o archivos locales), en un orden que el usuario puede
cambiar.

## 2. Stack técnico

| Pieza | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.4.0, JVM 17 |
| UI | Jetpack Compose (BOM 2026.06.01), Material 3, Navigation Compose |
| DI | Hilt 2.60.1 (KSP 2.3.10) |
| Reproducción | Media3 / ExoPlayer 1.10.1 (+ DASH, UI, Session) |
| Red | OkHttp 5.4.0, kotlinx.serialization, coroutines 1.11.0 |
| Imágenes | Glide Compose, Palette |
| Persistencia | **Solo `SharedPreferences`** (21 ficheros en `data/preferences/`), sin Room ni DataStore |
| Build | Gradle 9.6.1 (KTS + `gradle/libs.versions.toml`), AGP 9.4.1 (IT-19), `compileSdk/targetSdk 37`, `minSdk 26`, `isMinifyEnabled = false` |

## 3. Módulos

```
:app        → la aplicación (com.music.spotui) + utilidades YouTube portadas (com.metrolist.music.utils)
:spotify    → cliente web de Spotify (Spotify.kt, SpotifyAuth, SpotifyMapper, SpotifyCanvas, SpotiFlac)
:innertube  → cliente InnerTube de YouTube/YouTube Music
```

Paquetes principales de `:app`:

| Paquete | Qué hay |
|---|---|
| `di/` | `SongPlayer.kt` (motor de reproducción, ~2400 líneas), `TrackMatcher.kt` (puntuación de candidatos de YouTube), `SpotifyWebPlayer.kt` (desactivado) |
| `providers/` | Un proveedor de audio por fichero: Amazon, Deezer, Qobuz, SoundCloud, TIDAL + búsqueda y *override* manual de coincidencias |
| `lossless/`, `deezer/` | SpotiFLAC (FLAC comunitario) y descifrado/sesión de Deezer |
| `audio/` | Crossfade, `ResilientPlaybackDataSource`, `ProviderHealthChecker`, parser de bitrate FLAC |
| `lyrics/` | Letras de KuGou y LRCLIB (además de las de Spotify) |
| `data/api/` | `Api.kt` (capa sobre `:spotify`), `SpotifySession` (cookie `sp_dc`) |
| `data/update/` | `UpdateChecker` (único) |
| `data/recommendation/` | `SpotifyRecommendationEngine` (radio/autoplay) |
| `ui/` | Pantallas Compose, navegación, `PlaybackService` |

## 4. Cómo funciona

### 4.1 Arranque (`ui/navigation/MyNavHost.kt`)
1. Si no hay cookie `sp_dc` de Spotify → pantalla **Login** (`SpotifyLoginScreen`, WebView).
2. Si no → **Home**. Se restaura la última pista y la cola en el mini‑player (pausada).

No hay pantalla de elegir fuente: las fuentes se ordenan y activan en **Ajustes**. Deezer tiene su
propia introducción y login (`DeezerIntroScreen`, `DeezerLoginScreen`) y SpotiFLAC una pantalla de
verificación (`SpotiflacVerifyScreen`).

### 4.2 Capa de datos
`ViewModel` → `AppRepository` → `Api` (`data/api/Api.kt`) → módulo `:spotify`.
`Api` devuelve `Flow<Response<...>>` y cachea el feed de Home en memoria del proceso.
Cada canción lleva en `SongsModel.url` una **clave de reproducción** (`spotify:track:<id>` +
"título artista"), no una URL de stream.

### 4.3 Reproducción (`di/SongPlayer.kt`, `object` singleton)
`resolveStreamUrl()` (l.774) prueba, en este orden:

1. Resolución que ya esté en curso para la misma canción (p. ej. un prefetch) → la reutiliza.
2. Caché en memoria de la sesión y **caché persistente en disco** (las URLs de YouTube guardan su
   caducidad).
3. **Alternativa** fijada por el usuario (archivo local o enlace de YouTube).
4. **Descarga offline**, si la pista está descargada.
5. **Proveedores en el orden de Ajustes** (`getAudioProviderOrder`, `SettingsPref.kt:151`). Por
   defecto: Deezer → SpotiFLAC → Amazon → Qobuz → TIDAL → SoundCloud → YouTube Music. Cada uno se
   puede desactivar (`getDisabledAudioProviders`).

YouTube compara candidatos por título, artista, duración y explicit (bloque de puntuación en
`di/TrackMatcher.kt`, sacado de `SongPlayer` en IT-27). Extras: prefetch de las siguientes de la cola, caché de medios de
ExoPlayer, crossfade (`audio/`), badge de fuente y calidad, calidad distinta por Wi‑Fi y datos.

### 4.4 Cuentas y credenciales
| Cuenta | Dónde se guarda |
|---|---|
| Spotify (`sp_dc`) | `spotify_session.xml` (`data/api/SpotifySession.kt`) |
| Deezer (ARL) | `Deezer.xml` |
| SpotiFLAC | `SpotiflacSession.xml` |

Sin cifrar. Desde IT-29 **no entran en el backup de Google** (ver D6). YouTube se usa siempre de forma anónima
(`MyApplication.kt:52`, `YouTube.cookie = null`): no hay login de YouTube. Las funciones `*YoutubeCookie` que
había en `SettingsPref.kt` no se usaban y se borraron en IT-44 (S15).

### 4.5 Servicio y sistema
`PlaybackService` (`MediaLibraryService` de Media3) da notificación, pantalla de bloqueo y
**Android Auto**. `SpotifyWebPlayer` (reproductor web de Spotify en WebView) existe pero está
**desactivado** (`webPlayerEnabled = false`, `SongPlayer.kt:187`).

### 4.6 Funcionalidades visibles
Home, Buscar, Biblioteca, Álbum, Artista (y lanzamientos), Categorías, Playlist
(crear/añadir/quitar, sincronizado), Podcasts (Show), Cola, Me gusta, Historial, Descargas
(incl. FLAC), Archivos locales, Letras sincronizadas con traducción, Canvas, Ajustes (calidad,
crossfade, orden de fuentes, URL del repo de actualizaciones) y aviso de actualización desde
GitHub.

### 4.7 Actualizaciones
Un solo `UpdateChecker` (`data/update/`) que consulta `api.github.com/repos/<repo>/releases/latest`.
El repo es configurable en Ajustes; por defecto `https://github.com/Nonit0/Spotui`
(`DEFAULT_UPDATE_REPO_URL`, `SettingsPref.kt:36`) (cambiado en IT-22).

## 5. Estado actual (diagnóstico)

**Fortalezas**
- Fuentes de audio modulares (`providers/`) con orden y activación configurables por el usuario.
- Cachés de streams en memoria y en disco, y prefetch de la cola.
- Comentarios de código abundantes que explican el *porqué*.
- Dependencias recientes (2026).

**Deuda / riesgos detectados**
| # | Hallazgo | Dónde | Iteración |
|---|---|---|---|
| D1 | `SongPlayer.kt` es un *god object* (~2400 líneas: resolución, crossfade, cachés) | `di/SongPlayer.kt` | IT-27 sacó la puntuación a `TrackMatcher` ✅; más trozos solo si hace falta |
| D2 | `runBlocking` al generar el PoToken dentro de un `CompletableFuture`: puede bloquear hilos del pool hasta 5 s y la cancelación no llega (IT-26 cerrada sin cambios) | `metrolist/music/utils/potoken/PoTokenGenerator.kt:40`, `metrolist/music/utils/YTPlayerUtils.kt:126-150` | S13 |
| D3 | ✅ Resuelta: el typo `LibraryScreeen.kt` ya se llama `LibraryScreen.kt` | `ui/screens/` | IT-24 |
| D4 | Tests de plantilla sin valor; `ProviderStreamTest` usa red. Hay 46 `@Test` en total (`:app` 26 unitarios —10 de `TrackMatcherTest`, IT-28— + 1 instrumentado, `:spotify` 19, `:innertube` 0) | `app/src/test`, `app/src/androidTest` | IT-21, IT-25 |
| D5 | ✅ Resuelta: aviso de actualización (IT-22) y firma de Ajustes `Maintained with ♥ by Nonit0` (IT-23) apuntan a `Nonit0/Spotui` | `SettingsPref.kt:36`, `SettingsScreen.kt:834-839` | IT-22, IT-23 |
| D6 | ✅ Resuelta: credenciales de Spotify, Deezer y SpotiFLAC excluidas del backup (IT-29). No hay cookie de YouTube (IT-30) | `res/xml/` | IT-29, IT-30 |
| D7 | Reproductor web de Spotify desactivado pero presente | `di/SpotifyWebPlayer.kt`, `ui/notification/WebMediaPlayer.kt` | S3 |
| D8 | Release sin minificar (`isMinifyEnabled = false`) → APK de ~100 MB | `app/build.gradle.kts` | S4 |
| D9 | ✅ Resuelta (IT-47): la release se firma con la clave propia de `keystore.properties` (fuera de git); si falta, con la clave debug local de Android y Gradle avisa. Debug usa la clave debug local; `app/debug.keystore` ya no está en git (IT-52) | `app/build.gradle.kts`, `keystore.properties.example` | S12, S22, IT-47, IT-52 |

Estas deudas alimentan el plan de iteraciones en [`iteraciones/README.md`](iteraciones/README.md).

## 6. Compilar y probar

**Lo hace el usuario en Android Studio** (fase 2). Claude no lanza Gradle sin permiso explícito,
porque una compilación completa satura el PC.

- APK: `app/build/outputs/apk/debug/Spotui_v1.5.0.apk` (con sufijo `.debug` en el `applicationId`).
- Tests: `:app` y `:innertube` → `testDebugUnitTest`; `:spotify` es un módulo JVM → `:spotify:test`.
- Si alguna vez se compila desde terminal: JDK 17 (`JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`,
  `ANDROID_HOME=$HOME/Android/Sdk`) y con límites (`--no-daemon --max-workers=4`, `-Xmx2g`).
- Releases: las compila y firma GitHub Actions al subir un tag `v*` (borrador con la APK). Pasos y secrets en
  [`RELEASE.md`](RELEASE.md) (IT-53).
