# IT-47 · Firmar la release con una clave propia (S12)

| Campo | Valor |
|---|---|
| Tipo | Build |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-29) · pendiente de que el usuario cree la clave y pruebe |

## 1. Qué hacer
S12: la release se firmaba con `app/debug.keystore` (clave de H4zh4n, alias `hazhan`, contraseña `android`),
que está en git: cualquiera puede firmar una APK que el móvil aceptaría como actualización. Firmar la release
con una clave propia que no esté en git.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Clave propia fuera de git? → **Sí** (primera vez que firma; vale lo recomendable).
- [x] ¿Hay APKs release publicadas con la clave actual? → **No**, nadie tiene la app: cambiar de clave no rompe
  a ningún usuario. El móvil del usuario tendrá que desinstalar/reinstalar una vez.

## 3. Salida esperada
- Con `keystore.properties` en la raíz: `assembleRelease` firma con la clave propia.
- Sin él (clon nuevo): firma con la clave de debug compartida, como antes, y Gradle avisa con un *warning*.
- Debug no cambia (`sharedDebug`).

### Pasos del usuario (una vez)
1. Crear la clave (fuera del repo; `keytool` pide las contraseñas y los datos del certificado):
   `keytool -genkeypair -v -keystore ~/.android/spotui-release.jks -alias spotui -keyalg RSA -keysize 4096 -validity 10000`
2. `cp keystore.properties.example keystore.properties` y rellenarlo (ruta absoluta al `.jks`, contraseñas).
3. **Copia de seguridad** del `.jks` y de las contraseñas (USB / gestor de contraseñas). Perderlos = no poder
   actualizar nunca más la app instalada.
4. Comprobar la firma de una APK: `apksigner verify --print-certs app-release.apk` (en
   `$ANDROID_HOME/build-tools/<versión>/`).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/build.gradle.kts` | `signingConfigs.release` leído de `keystore.properties` si existe; `release` usa `findByName("release") ?: sharedDebug` + warning |
| `keystore.properties.example` | Plantilla (sin secretos) |
| `.gitignore` | `keystore.properties`, `*.jks` |

## 5. Fuera de alcance
- Sacar `app/debug.keystore` de git: rompería el build de debug de un clon nuevo → sugerencia S22.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S22: dejar de versionar `app/debug.keystore` (que cada clon use la debug key por defecto de Android).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí; el usuario crea la clave y genera la release | `4dcff1b` |
