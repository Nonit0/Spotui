# IT-31 · Revisar los 4 commits de RMNO21 contra H4zh4n

| Campo | Valor |
|---|---|
| Tipo | Análisis |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · decisión del usuario pendiente |

## 1. Qué hacer
Ver si los 4 commits de RMNO21 (2026-08-15, en `archivo/original-1.6`) aplican a la base H4zh4n.
Sin tocar código. Cada commit que merezca la pena pasa a ser una iteración propia.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? → "si, haz la IT-31"

## 3. Salida esperada
Tabla con, por cada commit: si aplica (`git apply --check`), si el problema existe en H4zh4n y si merece la pena.

## 4. Cambios
Ninguno (solo este documento y el índice).

## 5. Fuera de alcance
Aplicar los commits.

## 6. Análisis

### `0035218` · fix(concurrency): cachés LRU, init del singleton y sets — aplica limpio
| Parte | ¿El problema existe en H4zh4n? | Valor |
|---|---|---|
| `SpotifySync.kt:102` `mutableSetOf` → `ConcurrentHashMap.newKeySet` | Sí. Es el único sitio de `membershipCache` que mete un set no concurrente (en la l.120 ya usa `newKeySet`), y luego se hace `add`/`remove` desde corrutinas | Bajo-medio |
| `NewPipe.kt` `init()` con `@Synchronized` + `@Volatile` | Sí. `MyApplication` llama a `init()` en segundo plano al arrancar y el reproductor también puede llamarlo a la vez: doble inicialización | Bajo |
| `SpotifyMapper.kt` cachés con `synchronized` | El problema existe (un `LinkedHashMap` con `accessOrder = true` se modifica incluso al leer), pero **`SpotifyMapper` no lo usa la app**, solo sus tests | Nulo hoy |

### `bfe4bdc` · fix(network): cerrar siempre conexiones HTTP — **no aplica limpio**
| Parte | ¿El problema existe en H4zh4n? | Valor |
|---|---|---|
| `UpdateChecker.kt` | **No aplica**: H4zh4n reescribió el `UpdateChecker` con OkHttp. Hallazgo nuevo: si la respuesta no es 2xx (`if (!response.isSuccessful) return null`, l.59) no se cierra el `response` → S17 | — |
| `DeezerSession.kt` `post()` sin `disconnect()` | Sí, pero las excepciones ya acaban en `null`. Solo libera la conexión antes | Bajo |
| `LosslessSource.kt` / `SpotiflacGated.kt`: `disconnect()` en `finally` | Sí: si `responseCode` o la lectura lanzan excepción, la conexión no se cierra | Bajo |

### `f1631c8` · fix: protobuf de Canvas y seek en Deezer — aplica limpio
| Parte | ¿El problema existe en H4zh4n? | Valor |
|---|---|---|
| `DeezerDataSource.kt` `fillPending()` | **Sí, es un bug real**: al saltar dentro del último trozo (parcial) de la canción, `dropFirst` puede ser mayor que lo leído → `copyOfRange` con longitud negativa → `IllegalArgumentException` y error de reproducción. Solo con Deezer Direct (necesita ARL) | Medio |
| `SpotifyCanvas.kt` lector protobuf con límites | Casi todo ya lo atrapa el `try/catch` de `canvasUrl()`. Lo único que no: una longitud negativa en `skip(2)` hace retroceder `pos` y podría dar un bucle infinito con una respuesta corrupta | Bajo |

### `7244e4d` · fix(prefs): `toIntOrNull` y `ConnectivityManager` nulo — aplica limpio
| Parte | ¿El problema existe en H4zh4n? | Valor |
|---|---|---|
| `LikedSongPref` / `LikedAlbumPref` `toInt()` → `toIntOrNull()` | Hoy las claves siempre son ids enteros (`song.id.toString()`). Solo protege ante datos raros (p. ej. una copia restaurada a mano) | Bajo (defensivo) |
| `SettingsPref.kt` `as? ConnectivityManager` | En Android real nunca es nulo | Muy bajo |

## 7. Propuesta
| Commit | Propuesta |
|---|---|
| `f1631c8` | **Traerlo** (bug real de Deezer + Canvas) |
| `0035218` | Traerlo (cherry-pick limpio; lo de `SpotifyMapper` no molesta) |
| `bfe4bdc` | Traer solo `DeezerSession`, `LosslessSource` y `SpotiflacGated` (sin la parte de `UpdateChecker`) |
| `7244e4d` | Opcional: es barato y limpio, pero casi no aporta |

## 8. Sugerencias (nuevas iteraciones candidatas)
- S17: en `UpdateChecker.fetchLatestRelease` (`UpdateChecker.kt:58-59`) cerrar el `response` con `use {}` cuando no es 2xx.

## 9. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ | 3 de 4 commits aplican limpio; `bfe4bdc` falla solo en `UpdateChecker.kt` (reescrito con OkHttp en H4zh4n). Bug real: seek en Deezer (`f1631c8`). Hallazgo S17 | _(este commit)_ |
