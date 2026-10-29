# IT-54 · Minificar la release con R8 (S4)

| Campo | Valor |
|---|---|
| Tipo | Build |
| Depende de | IT-47 |
| Estado | ✅ Hecha (2026-10-29) · probada por el usuario |

## 1. Qué hacer
S4: la release iba sin minificar (`isMinifyEnabled = false`), 90,6 MB. Activar R8 con las reglas necesarias.

Reparto de la APK 1.6.0 (sin comprimir, ~132 MB): `.dex` 64 MB, librerías nativas 64 MB (4 ABIs; casi todo
`libtranslate_jni.so` de ML Kit, 16 MB por ABI), recursos 3 MB. R8 solo actúa sobre el `.dex` y los recursos.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Qué activar? → **R8 sin ofuscar + recortar recursos** (`-dontobfuscate`: menos roturas por reflexión y
  trazas legibles).
- [x] ¿Quién compila? → **Claude en terminal**, con límites (`systemd-run` 4,5 GB / 200 % CPU, `-Xmx3g`,
  1 worker).

## 3. Salida esperada
APK release más pequeña, firmada con la clave propia, que funciona igual que la 1.6.0.

### Pasos del usuario
Instalar `app/build/outputs/apk/release/Spotui_v1.6.0.apk` encima de la actual y probar sobre todo lo que usa
reflexión o JS: reproducir desde YouTube y desde cada proveedor (Deezer, Qobuz, Amazon, SoundCloud, SpotiFLAC),
descargas, login de Spotify, crear playlist, traducción de letras, Canvas, notificación/pantalla de bloqueo y
aviso de actualización.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/build.gradle.kts` | `release`: `isMinifyEnabled = true`, `isShrinkResources = true` |
| `app/proguard-rules.pro` | `-dontobfuscate`, líneas en trazas; `keep` de Rhino (`org.mozilla.javascript`) y de los patrones *timeago* de NewPipeExtractor; `dontwarn` de `java.beans`, `javax.script`, `jdk.dynalink` (APIs de JVM de escritorio que referencia Rhino) |

Los puentes `@JavascriptInterface` (cipher, PoToken, EJS, SpotiFLAC) ya los conserva
`proguard-android-optimize.txt`; kotlinx.serialization, ktor, OkHttp y Hilt traen sus propias reglas.

## 5. Fuera de alcance
- Quitar las ABIs `x86`/`x86_64` (solo emuladores, ~35 MB sin comprimir) → S26.
- Ofuscar.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S26: `abiFilters` solo `arm64-v8a` + `armeabi-v7a` en la release (o *splits* por ABI).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | 3 compilaciones: 1) R8 falla por clases de JVM de escritorio (Rhino) → `dontwarn`; 2) R8 OK, `produceReleaseComposeMapping` falla en `--offline` (falta `compose-group-mapping:2.4.0`); 3) sin `--offline`: OK. APK **90,6 → 73,3 MB**; `.dex` 63,8 → 9,1 MB; firma `CN=Nonit0`; Rhino, *timeago* y `CipherWebView` presentes en el `.dex` | `e4bbb19` |
| 2026-10-29 | ✅ Probada | APK minificada instalada encima de la 1.6.0: funciona todo | — |
