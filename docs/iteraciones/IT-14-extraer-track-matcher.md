# IT-14 · Sacar la comparación de canciones de `SongPlayer.kt` a `TrackMatcher.kt`

| Campo | Valor |
|---|---|
| Tipo | Código (refactor, sin cambio de comportamiento) |
| Depende de | — |
| Estado | ⏳ Pendiente |

## 1. Qué hacer
Primer y único trozo del troceo de `SongPlayer.kt` (~1800 líneas) que se propone por ahora: **mover sin modificar** el
bloque de funciones puras que puntúan si un vídeo de YouTube es la canción correcta
(`SongPlayer.kt` ~l.846-1052) a un fichero nuevo `di/TrackMatcher.kt` como `internal object TrackMatcher`:

`CandidateScore`, `normalizedForMatch`, `anyLatinTransliterator`, `foldLatinDiacritics`, `transliterateCyrillic`,
`bigramSimilarity`, `VersionMarker`, `markerPattern`, `alternateVersionMarkers`, `versionMarkers`,
`hardVersionMarkers`, `ytmusicTransferScore`, `isAcceptableMatch`.

`SongPlayer` las llama como `TrackMatcher.xxx(...)`. Las que dependen de estado de `SongPlayer`
(`ensureSpotifyMatchMetadata`, registros) **se quedan donde están**.

Se elige este trozo porque es autocontenido, es el más fácil de probar (IT-15) y es el que decide
"¿suena la canción correcta?".

## 2. Preguntar al usuario (antes de empezar)
- [ ] ¿Lo hacemos? Es mover código, pero toca el fichero central de la reproducción.
- [ ] ¿Te vale el nombre `TrackMatcher` y dejarlo en el paquete `di` junto a `SongPlayer`?

> Respuesta del usuario: _(rellenar)_

## 3. Salida esperada
- `assembleDebug testDebugUnitTest :spotify:test` → `BUILD SUCCESSFUL`, 22 tests en verde.
- `SongPlayer.kt` ~200 líneas más corto; el cuerpo de cada función movida, idéntico (se compara con `git diff --color-moved`).
- Prueba manual: canciones que salen por YouTube suenan y son la canción correcta.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `di/TrackMatcher.kt` | Nuevo, con las funciones movidas |
| `di/SongPlayer.kt` | Bloque eliminado; llamadas con prefijo `TrackMatcher.` |

## 5. Fuera de alcance
Cambiar lógica, renombrar funciones, mover más partes de `SongPlayer`, tests (→ IT-15).
Si una función movida resulta depender de estado de `SongPlayer`, **se queda** y se anota aquí.

## 6. Sugerencias
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
