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
| IT-21 | Pasar los tests unitarios en Android Studio | El usuario los lanza (`:app`, `:spotify`, `:innertube`) y se apunta qué pasa y qué falla. `ProviderStreamTest` usa red: se espera que falle o tarde | Verificación | IT-18 | ⏳ pendiente (la lanza el usuario) |
| [IT-22](IT-22-actualizaciones-a-nonit0.md) | Apuntar las actualizaciones a `Nonit0/Spotui` | `DEFAULT_UPDATE_REPO_URL` (`SettingsPref.kt:36`) → `https://github.com/Nonit0/Spotui`. Sustituye a IT-09/IT-13. Sin avisos hasta la 1ª release del fork | Código | — | ✅ |
| [IT-23](IT-23-firma-ajustes-nonit0.md) | Firma de Ajustes a `Nonit0` | `SettingsScreen.kt:834-839`: `Maintained with ♥ by Hazhan Salih` → `by Nonit0`, enlace a tu fork. Créditos a H4zh4n van al README (S10) | Código | — | ✅ |
| [IT-24](IT-24-renombrar-library-screen.md) | Renombrar `LibraryScreeen.kt` → `LibraryScreen.kt` | Igual que IT-05: renombrar el fichero, sin tocar código | Código | — | ✅ |
| IT-25 | Borrar los tests de plantilla | Igual que IT-06: `ExampleUnitTest.kt` y `ExampleInstrumentedTest.kt` | Código | IT-21 | ⏳ |
| IT-26 | Quitar `runBlocking` de `PoTokenGenerator` | Igual que IT-11 (`PoTokenGenerator.kt:40`): hacerlo `suspend`. Antes se revisan las llamadas, que pueden haber cambiado respecto a IT-08 | Código | — | ⏳ **siguiente** |
| IT-27 | Sacar la comparación de canciones de `SongPlayer` a `TrackMatcher.kt` | Igual que IT-14, pero ahora el bloque está en `SongPlayer.kt` ~l.1625-1838 (el fichero tiene 2612 líneas) | Código | — | ⏳ |
| IT-28 | Tests unitarios de `TrackMatcher` | Igual que IT-15 | Código | IT-27 | ⏳ |
| IT-29 | Sacar las credenciales del backup de Google | **Más grave que en la fase 1:** `backup_rules.xml` copia *todas* las prefs (incluidas `spotify_session` con la cookie `sp_dc`, `Deezer` y `SpotiflacSession`), y `data_extraction_rules.xml` no tiene reglas (en Android 12+ se copia todo). Cambio: excluir esos 3 ficheros en los dos XML | Código | — | ⏳ |
| IT-30 | Mover la cookie de YouTube a su propio fichero y excluirlo del backup | Igual que la mitad de IT-16: `youtube_cookie` vive en `settings_prefs` (`SettingsPref.kt:82`), que sí debe copiarse | Código | IT-29 | ⏳ |
| IT-31 | Revisar los 4 commits de RMNO21 contra H4zh4n | Análisis, sin tocar código: ver si `0035218`, `bfe4bdc`, `f1631c8` y `7244e4d` aplican aquí. Cada commit que aplique pasa a ser una iteración propia | Análisis | — | ⏳ |
| IT-32 | Decidir si se trae Soulseek | Decisión, sin código: solo existe en `Spotui/Spotui` 1.6. Si se trae, se parte en iteraciones antes de empezar | Decisión | IT-31 | ⏳ |

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
| S11 | `ProviderStreamTest` usa red: separarlo o marcarlo para que no corra por defecto | Fase 2, según el resultado de IT-21 |
| S12 | La release se firma con `app/debug.keystore`, que está en git (contraseña `android`): ¿clave propia fuera de git? | Fase 2, hallazgo de IT-20 |

S3, S4 y S5 siguen vigentes en la base H4zh4n (`SpotifyWebPlayer` sigue ahí y desactivado, y
`isMinifyEnabled = false`).
