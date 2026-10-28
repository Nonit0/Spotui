# IT-34 · Arreglos de concurrencia de RMNO21

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-31 |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
Traer `0035218` de RMNO21 (rama `archivo/original-1.6`) con cherry-pick, conservando su autoría.


## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Se trae? → "si, trae los tres que propones" (propuesta de IT-31, §7)

## 3. Salida esperada
- Compila en Android Studio.
- Añadir/quitar canciones de playlists de Spotify ("Guardado en") y la primera reproducción de YouTube funcionan igual. Los tests de `:spotify` (19) siguen en verde.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../data/api/SpotifySync.kt` | l.102: el set de `membershipCache` pasa a `ConcurrentHashMap.newKeySet`, como en la l.120 |
| `innertube/.../pages/NewPipe.kt` | `NewPipeExtractor.init()` con `@Synchronized` y campos `@Volatile`: no se inicializa dos veces |
| `spotify/.../SpotifyMapper.kt` | Cachés LRU con `synchronized` (la app no usa `SpotifyMapper`; solo sus tests) |

## 5. Fuera de alcance
Quitar `SpotifyMapper` si no se usa (sería otra iteración).

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Cherry-pick sin conflictos. No se ha compilado (lo hace el usuario en Android Studio) | `6424ea4` |
| 2026-10-28 | ✅ | `:spotify` recompilado con IT-33 e IT-34: `:spotify:test` 19/19 en verde (MatchScore 7 · Performance 12), 21 s. Solo el aviso previo de `readBytes()` obsoleto en `SpotifyCanvas.kt:57` (no viene del parche) | — |
