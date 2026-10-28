# Traceroute de iteraciones

Cada iteración es **un solo cambio atómico** con su propio fichero `IT-XX-*.md`, que funciona
como traza (traceroute) de lo que se pidió, se preguntó, se hizo y salió.

Contexto de la app: [`../CONTEXTO.md`](../CONTEXTO.md). Plantilla: [`PLANTILLA.md`](PLANTILLA.md).

## Reglas (obligatorias)

1. **No salir de esta carpeta** (`Spotui/`). Nada se lee ni escribe fuera del repo.
2. **Preguntar siempre al usuario** antes de tocar nada: se presenta el *qué hacer* y se espera
   un "sí". Si la respuesta es "no" o "cambia X", se actualiza el fichero y se vuelve a preguntar.
3. **Un cambio por iteración, sin subcambios.** Si al hacerla aparece otro cambio necesario,
   **no se hace**: se apunta en *Sugerencias* como nueva iteración candidata y se cierra la actual.
   Esto evita la iteración infinita.
4. **Si el cambio no cabe en una iteración, se parte** en varias antes de empezar, nunca durante.
5. **Criterio de parada**: la iteración termina cuando se obtiene la *Salida esperada* o cuando
   falla una vez. Un fallo no se arregla dentro de la misma iteración: se registra y se propone
   una nueva.
6. Al terminar, se rellena el *Registro* (estado, fecha, resultado real, commit si lo hay).

## Estados

`⏳ Pendiente` · `⏸️ Aplazada` · `❓ Esperando usuario` · `🔧 En curso` · `✅ Hecha` · `❌ Fallida` · `🚫 Descartada`

## Índice

### Fase 1 · base `Spotui/Spotui` 1.6 (cerrada, archivada en la rama `archivo/original-1.6`)

Se conserva como historial. Las que siguen teniendo sentido se rehacen en la fase 2 (ver la
correspondencia más abajo).

| ID | Título | Tipo | Depende de | Estado |
|---|---|---|---|---|
| [IT-01](IT-01-baseline-build-tests.md) | Comprobar que compila y pasan los tests | Verificación | — | ✅ |
| [IT-02](IT-02-decidir-update-checker.md) | Decidir qué `UpdateChecker` se queda | Decisión | IT-01 | ✅ |
| [IT-09](IT-09-update-checker-releases-latest.md) | Apuntar `data/update` a `Nonit0/Spotui` `releases/latest` | Código | IT-02 + 1ª release en tu fork | 🚫 (no hace falta en H4zh4n) |
| [IT-03](IT-03-migrar-badge-home.md) | Migrar el badge de Home a `data/update` | Código | IT-09 | 🚫 (no hace falta en H4zh4n) |
| [IT-10](IT-10-adaptar-update-checker-test.md) | Adaptar `UpdateCheckerTest` a `data/update` | Código | IT-03 | 🚫 (no hace falta en H4zh4n) |
| [IT-04](IT-04-borrar-update-checker-sobrante.md) | Borrar el `UpdateChecker` que sobra | Código | IT-10 | 🚫 (no hace falta en H4zh4n) |
| [IT-05](IT-05-renombrar-library-screen.md) | Renombrar `LibraryScreeen.kt` → `LibraryScreen.kt` | Código | IT-01 | ✅ |
| [IT-06](IT-06-borrar-tests-plantilla.md) | Borrar los tests de plantilla | Código | IT-01 | ✅ |
| [IT-07](IT-07-actualizar-readme.md) | Actualizar `README.md` con las funciones reales | Docs | — | ✅ |
| [IT-08](IT-08-analizar-runblocking-potoken.md) | Analizar el `runBlocking` del PoToken (sin cambiar código) | Análisis | — | ✅ |
| [IT-11](IT-11-potoken-suspend.md) | Quitar `runBlocking` de `PoTokenGenerator` (hacerlo `suspend`) | Código | IT-08 | ✅ (falta prueba manual) |
| [IT-12](IT-12-tests-modulo-spotify.md) | Ejecutar los tests de `:spotify` | Verificación | IT-01 | ✅ |
| [IT-13](IT-13-badge-home-a-nonit0.md) | Apuntar el aviso de Home a `Nonit0/Spotui` | Código | — | ✅ |
| [IT-14](IT-14-extraer-track-matcher.md) | Sacar la comparación de canciones de `SongPlayer` a `TrackMatcher.kt` | Código | — | ✅ (falta prueba manual) · rehacer → IT-27 |
| [IT-15](IT-15-tests-track-matcher.md) | Tests unitarios de `TrackMatcher` | Código | IT-14 | 🚫 (cambio de base → IT-28) |
| [IT-16](IT-16-credenciales-fuera-del-backup.md) | Sacar la cookie de YouTube y Soulseek del backup de Google | Código | — | 🚫 (cambio de base → IT-29/IT-30) |

### Fase 2 · base H4zh4n (1.5.0), rama `base-h4zh4n`

Reglas extra de esta fase:
- **Compilar y ejecutar tests lo hace el usuario en Android Studio.** Claude no lanza Gradle sin
  permiso explícito (satura el PC).
- El fichero `IT-XX-*.md` de cada iteración se crea cuando se propone, no antes, para que el
  índice se pueda reordenar sin dejar ficheros huérfanos.

| ID | Título | Qué se hace (resumen) | Tipo | Depende de | Estado |
|---|---|---|---|---|---|
| [IT-18](IT-18-base-h4zh4n-compila.md) | Traer H4zh4n a `base-h4zh4n` y comprobar que compila | — | Verificación | — | ✅ |
| [IT-19](IT-19-agp-9-4-1.md) | Subir el AGP de 9.2.1 a 9.4.1 | — | Código | IT-18 | ✅ |
| [IT-20](IT-20-contexto-base-h4zh4n.md) | Actualizar `CONTEXTO.md` a la base H4zh4n | Reescribir el contexto: módulos, fuentes de audio (lista de prioridad), login de YouTube por WebView, `UpdateChecker` único con URL configurable, tests que existen | Docs | IT-18 | ✅ |
| [IT-21](IT-21-tests-unitarios.md) | Pasar los tests unitarios | Lanzados de uno en uno con topes de RAM/CPU: 45/45 en verde (`:innertube` no tiene tests). `ProviderStreamTest` pasa pero solo YouTube resuelve (→ S16) | Verificación | IT-18 | ✅ |
| [IT-22](IT-22-actualizaciones-a-nonit0.md) | Apuntar las actualizaciones a `Nonit0/Spotui` | `DEFAULT_UPDATE_REPO_URL` (`SettingsPref.kt:36`) → `https://github.com/Nonit0/Spotui`. Sustituye a IT-09/IT-13. Sin avisos hasta la 1ª release del fork | Código | — | ✅ |
| [IT-23](IT-23-firma-ajustes-nonit0.md) | Firma de Ajustes a `Nonit0` | `SettingsScreen.kt:834-839`: `Maintained with ♥ by Hazhan Salih` → `by Nonit0`, enlace a tu fork. Créditos a H4zh4n van al README (S10) | Código | — | ✅ |
| [IT-24](IT-24-renombrar-library-screen.md) | Renombrar `LibraryScreeen.kt` → `LibraryScreen.kt` | Igual que IT-05: renombrar el fichero, sin tocar código | Código | — | ✅ |
| [IT-25](IT-25-borrar-tests-plantilla.md) | Borrar los tests de plantilla | Igual que IT-06: `ExampleUnitTest.kt` y `ExampleInstrumentedTest.kt` borrados, `androidTest/` se mantiene con `.gitkeep` | Código | IT-21 | ✅ |
| [IT-26](IT-26-potoken-runblocking.md) | Quitar `runBlocking` de `PoTokenGenerator` | Cerrada sin cambios: en H4zh4n el `runBlocking` ya tiene tiempo límite y corre en un `CompletableFuture`; hacerlo `suspend` obliga a tocar `YTPlayerUtils`. Riesgo de bloqueo de hilos → S13 | Análisis | — | 🚫 |
| [IT-27](IT-27-extraer-track-matcher.md) | Sacar la comparación de canciones de `SongPlayer` a `TrackMatcher.kt` | Igual que IT-14; movido sin cambios (l.1625-1838 + `featSearchPattern`). `SongPlayer.kt` 2612 → 2397 líneas | Código | — | ✅ |
| [IT-28](IT-28-tests-track-matcher.md) | Tests unitarios de `TrackMatcher` | 10 tests en `di/TrackMatcherTest.kt` (incluye `explicit`). Ejecutados en IT-21: 10/10 en verde | Código | IT-27 | ✅ |
| [IT-29](IT-29-credenciales-fuera-del-backup.md) | Sacar las credenciales del backup de Google | `spotify_session`, `Deezer` y `SpotiflacSession` excluidos en `backup_rules.xml` y `data_extraction_rules.xml` (cloud + device-transfer) | Código | — | ✅ |
| [IT-30](IT-30-cookie-youtube-backup.md) | Mover la cookie de YouTube a su propio fichero y excluirlo del backup | Cerrada sin cambios: las funciones de la cookie no se usan, no hay login de YouTube (`YouTube.cookie = null`). Funciones sin uso → S15 | Análisis | IT-29 | 🚫 |
| [IT-31](IT-31-commits-rmno21.md) | Revisar los 4 commits de RMNO21 contra H4zh4n | 3 aplican limpio; `bfe4bdc` no por `UpdateChecker` (OkHttp en H4zh4n). Bug real: seek en Deezer (`f1631c8`). Qué commits se traen: decisión del usuario | Análisis | — | ✅ |
| [IT-32](IT-32-decidir-soulseek.md) | Decidir si se trae Soulseek | No se trae: P2P (IP visible), cuenta automática, ~30 MB/canción y H4zh4n no tiene el *upgrade*. Queda como S18 | Decisión | IT-31 | 🚫 |
| [IT-33](IT-33-deezer-seek-canvas.md) | Traer `f1631c8`: salto al final en Deezer + lector de Canvas | Cherry-pick. Arregla el `IllegalArgumentException` al saltar al último trozo en Deezer | Código | IT-31 | ✅ |
| [IT-34](IT-34-concurrencia-rmno21.md) | Traer `0035218`: concurrencia | Cherry-pick. `SpotifySync` set concurrente, `NewPipeExtractor.init()` sincronizado, cachés de `SpotifyMapper` | Código | IT-31 | ✅ |
| [IT-35](IT-35-cerrar-conexiones-http.md) | Traer `bfe4bdc` sin `UpdateChecker`: cerrar conexiones HTTP | `DeezerSession`, `LosslessSource`, `SpotiflacGated` hacen `disconnect()` en `finally` | Código | IT-31 | ✅ |
| [IT-36](IT-36-playlist-canciones-repetidas.md) | La app se cierra al abrir una playlist con canciones repetidas | Clave de fila `id_posición` en `PlaylistScreen` (Spotify permite repetir canciones) | Código | — | ✅ (pendiente de prueba manual) |

#### Correspondencia fase 1 → fase 2

| Fase 1 | En la fase 2 | Motivo |
|---|---|---|
| IT-01, IT-12 (compilar y tests) | IT-18 (hecha) + IT-21 | Ahora compila y prueba el usuario en Android Studio |
| IT-02, IT-03, IT-04, IT-09, IT-10 (`UpdateChecker` duplicado) | 🚫 Ya no hacen falta | H4zh4n solo tiene un `UpdateChecker` (`data/update`) con URL configurable |
| IT-13 (aviso apunta a tu fork) | IT-22 (+ IT-23 para el enlace de Ajustes) | — |
| IT-05, IT-06, IT-07 | IT-24, IT-25 y "Actualizar README" (en el backlog, después de IT-20) | — |
| IT-08, IT-11 (PoToken) | IT-26 | El análisis de IT-08 vale como base, pero hay que comprobar las llamadas |
| IT-14, IT-15 (`TrackMatcher`) | IT-27, IT-28 | — |
| IT-16 (backup) | IT-29, IT-30 | En H4zh4n el problema es mayor y se parte en dos |
| IT-17 (elegir fuente automáticamente) | 🚫 Ya no hace falta | H4zh4n ya tiene una lista de prioridad de fuentes |

## Backlog de sugerencias (aún no son iteraciones)

Se convierten en iteración solo si el usuario lo pide. Los IDs no se renumeran: el orden lo marca *Depende de*.

| ID | Sugerencia | Estado |
|---|---|---|
| S1 | Trocear `SongPlayer.kt` | Primer trozo → IT-14. Más trozos solo si hace falta |
| S2 | Tests del matching | → IT-15 |
| S3 | Quitar `SpotifyWebPlayer` (desactivado) | Pendiente: preguntar si se piensa recuperar |
| S4 | Activar `isMinifyEnabled` | Pendiente: necesita reglas ProGuard |
| S5 | Subir dependencias / AGP (quita avisos de D8) | Pendiente: una dependencia por iteración |
| S6 | Cifrar credenciales | 🚫 Descartada en favor de IT-16 (ver su §5) |
| S7 | Caché de 6 h / `force` en `data/update` | Pendiente, ligada a IT-09 |
| S9 | Aviso de Home apunta a `Spotui/Spotui` | ✅ Resuelta en IT-13 (fase 1); en la fase 2 → IT-22 |
| S10 | Actualizar `README.md` para tu fork (funciones, enlaces, **créditos a H4zh4n: confirmado por el usuario en IT-23**) | Fase 2, después de IT-20 |
| S11 | `ProviderStreamTest` usa red: separarlo o marcarlo para que no corra por defecto | Pendiente. IT-21: tarda ~2 min y no tiene asserts (siempre pasa) |
| S12 | La release se firma con `app/debug.keystore`, que está en git (contraseña `android`): ¿clave propia fuera de git? | Fase 2, hallazgo de IT-20 |
| S13 | **Posible bloqueo de hilos al generar el PoToken** (hallazgo de IT-26): `PoTokenGenerator.kt:40` usa `runBlocking` dentro de un `CompletableFuture.supplyAsync` (`YTPlayerUtils.kt:126`), que ocupa un hilo del `ForkJoinPool` común hasta 5 s; `potFuture.get`/`sigFuture.get` (`YTPlayerUtils.kt:143,150`) bloquean el hilo de la corrutina; y la cancelación (saltar canción) no llega al futuro. Arreglo: pasar todo a corrutinas (`suspend` + `async` + `withTimeoutOrNull`), 2 ficheros, probar bien la reproducción | Pendiente: a tratar en el futuro (decisión del usuario en IT-26) |
| S14 | Errata en el marcador `clean` (`TrackMatcher.kt:110`): `пensored` lleva una `п` cirílica, así que nunca casa con "censored" (hallazgo de IT-27) | Pendiente |
| S15 | `getYoutubeCookie`, `setYoutubeCookie`, `isYoutubeLoggedIn` y `KEY_YT_COOKIE` (`SettingsPref.kt:76-85`) no se usan en ningún sitio: borrarlos (hallazgo de IT-30) | Pendiente |
| S16 | En `ProviderStreamTest` (2026-10-28) solo YouTube Music resuelve (10/10). Amazon falla al instante con `toString(...) must not be null` (¿bug o contexto falso del test?); Qobuz da timeout; TIDAL, Deezer Mirror, SoundCloud y SpotiFLAC no encuentran ninguna canción; Deezer Direct necesita ARL. Investigar cuáles están caídos de verdad (hallazgo de IT-21) | Pendiente |
| S17 | `UpdateChecker.fetchLatestRelease` (`UpdateChecker.kt:58-59`) no cierra el `response` de OkHttp cuando no es 2xx: usar `use {}` (hallazgo de IT-31) | Pendiente |
| S18 | **Soulseek** como respaldo FLAC (descartado en IT-32). Si se retoma: versión reducida (opción B de IT-32): paquete `soulseek/` de `archivo/original-1.6`, desactivado por defecto, credenciales fuera del backup, interruptor en Ajustes, solo caché + bajar la siguiente canción desde `prefetch()`. Tiene sentido sobre todo si S16 confirma que los proveedores lossless están caídos | Pendiente (mejora) |
| S19 | `QueueScreen.kt:211` usa `key = s.id`: una cola con la misma canción dos veces (p. ej. al reproducir una playlist con repetidas) puede cerrar la app. Cambiar la clave y el arrastre (`draggingId`) (hallazgo de IT-36) | Pendiente — **prioritaria** |

S3, S4 y S5 siguen vigentes en la base H4zh4n (`SpotifyWebPlayer` sigue ahí y desactivado, y
`isMinifyEnabled = false`).
