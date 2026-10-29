# Cómo sacar una release

La APK la compila y firma GitHub Actions (`.github/workflows/release.yml`, IT-53) al subir un tag `v*`.
Crea la release como **borrador** con la APK adjunta; tú la revisas y la publicas.

## Una sola vez: secrets del repo
GitHub → `Nonit0/Spotui` → Settings → Secrets and variables → Actions → **New repository secret**:

| Secret | Valor |
|---|---|
| `KEYSTORE_BASE64` | El `.jks` en base64: `base64 -w0 ~/.android/spotui-release.jks` (copiar la salida entera) |
| `KEYSTORE_PASSWORD` | `storePassword` de tu `keystore.properties` |
| `KEY_ALIAS` | `keyAlias` (`spotui`) |
| `KEY_PASSWORD` | `keyPassword` |

Las contraseñas no deben contener `\` (rompería el `keystore.properties` que genera el workflow).

Para probar los secrets sin crear release: pestaña **Actions** → *Release* → **Run workflow**. Deja la APK como
*artifact* de la ejecución.

## Cada release
1. **Versión** en `app/build.gradle.kts`: `versionName` (p. ej. `1.6.1`) y `versionCode` (`AAAAMMDD` + dígito,
   siempre mayor que el anterior). Opcional: notas en `CHANGELOG.md`.
2. Commit y push a `main` y `update-h4zh4n` (tienen que seguir iguales).
3. Tag y push: `git tag v1.6.1 && git push origin v1.6.1`. El tag **tiene que coincidir** con `versionName`
   (`v` + versión) o el workflow falla.
4. Esperar al workflow (pestaña **Actions**). Comprueba solo:
   - que el tag coincide con `versionName`;
   - que existen los 4 secrets (sin ellos no firma con la clave debug: falla);
   - que la APK está firmada con el certificado `CN=Nonit0` (SHA-256 `a20dca38…faa6`).
5. GitHub → Releases → el **borrador** `Spotui v1.6.1`: revisar notas, confirmar que lleva
   `Spotui_v1.6.1.apk`, dejar marcado **Set as the latest release** y **Publish release**.

`UpdateChecker` mira `releases/latest` y coge el primer *asset* que acaba en `.apk`: sin APK adjunta o sin
*Latest*, la app no ofrece la actualización.

## Si Actions falla: a mano
1. AS → *Build* → *Generate Signed App Bundle / APK* (o `./gradlew :app:assembleRelease` con
   `keystore.properties`). Sale en `app/build/outputs/apk/release/Spotui_v<versión>.apk`.
2. Comprobar firma y versión:
   `$ANDROID_HOME/build-tools/<versión>/apksigner verify --print-certs <apk>` → `CN=Nonit0`.
3. Crear la release del tag en la web, **adjuntar el `.apk`**, marcar *Latest* y publicar.
