# IT-63 · Descarga e instalación de actualizaciones desde la app

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-62 |
| Estado | ⏳ Pendiente de prueba manual |

## 1. Qué hacer
Que la actualización se baje e instale desde la propia app, como en Google Play, en vez de abrir el
navegador para descargar la APK.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Confirmas que hago exactamente esto?

> Respuesta del usuario (2026-11-26): "¿es posible que desde la app se descargue automáticamente la update como Google Play? Si se puede, hazlo funcionar ya".

## 3. Salida esperada
- Al abrir la app con una versión nueva publicada, el aviso *Update available* empieza a descargar la APK solo
  si estás en una red sin coste por datos (Wi-Fi); con datos móviles el botón dice **Download**. Se ve el porcentaje.
- Al terminar, el botón pasa a **Install**. La descarga sigue aunque se cierre el aviso y se reutiliza en el
  siguiente arranque.
- **Primera vez:** Android pide permitir *Instalar apps desconocidas* para Spotui (ajuste que abre la app; al
  volver se pulsa Install otra vez) y luego confirmar la instalación.
- **Siguientes veces (Android 12+):** como Spotui ya es quien instaló la versión actual, la actualización se
  instala sin diálogo del sistema. La app se cierra al reemplazarse.
- Si la release no tiene APK adjunta, el botón **Update** abre la página como antes.

## 4. Cambios

| Fichero | Cambio |
|---|---|
| `app/src/main/java/com/music/spotui/data/update/ApkUpdater.kt` (nuevo) | Descarga a `cacheDir/updates` con progreso en un ámbito propio, valida que es el mismo paquete con `versionCode` mayor, instala con `PackageInstaller` (`USER_ACTION_NOT_REQUIRED` en Android 12+, `setRequestUpdateOwnership` en 14+); `UpdateInstallReceiver` lanza la confirmación del sistema o avisa del fallo |
| `app/src/main/java/com/music/spotui/ui/components/UpdatePrompt.kt` | Botón Download / Downloading… / Install, barra de progreso, descarga automática en Wi-Fi |
| `app/src/main/java/com/music/spotui/data/update/UpdateChecker.kt` | Si ya estás al día, borra las APK descargadas |
| `app/src/main/AndroidManifest.xml` | `REQUEST_INSTALL_PACKAGES`, `ENFORCE_UPDATE_OWNERSHIP` y el receptor |

## 5. Fuera de alcance
- Instalar sin abrir la app (comprobación periódica con WorkManager en segundo plano): posible, pero cerraría la
  app mientras suena música → sugerencia.
- Volver a abrir la app sola tras actualizarse (Android bloquea abrir actividades desde segundo plano).

## 6. Sugerencias (nuevas iteraciones candidatas)
- Comprobación y descarga en segundo plano (WorkManager, solo Wi-Fi + cargando) con notificación «Actualización lista».
- Al pedir propiedad de actualización (Android 14+), instalar una APK a mano desde el navegador muestra un aviso
  extra del sistema; documentarlo en el README si molesta.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
