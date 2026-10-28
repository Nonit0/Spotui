# IT-27 · Sacar la comparación de canciones de `SongPlayer.kt` a `TrackMatcher.kt`

| Campo | Valor |
|---|---|
| Tipo | Código (refactor, sin cambio de comportamiento) |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · pendiente de compilar y de prueba manual |

## 1. Qué hacer
Lo mismo que IT-14 en la fase 1, sobre la base H4zh4n: **mover sin modificar** el bloque de funciones puras que puntúan
si un resultado de YouTube Music es la canción de Spotify correcta (`di/SongPlayer.kt` l.1625-1838) y `featSearchPattern`
(l.355) a `di/TrackMatcher.kt` como `internal object TrackMatcher`.

Diferencias del bloque respecto a la fase 1 (vienen de H4zh4n, se mueven tal cual): marcadores de versión `clean` y
`explicit`, y el parámetro `wantExplicit` de `isAcceptableMatch` (sube el mínimo 0.6 si no coincide el flag explícito).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hacemos? → "sigue con la IT-27 (vamos en orden)"
- [x] Nombre y paquete: los mismos que en IT-14 (`TrackMatcher`, paquete `di`), ya elegidos por el usuario entonces.

## 3. Salida esperada
- El cuerpo movido es idéntico al original (se compara cambiando `internal` → `private`).
- Compila en Android Studio (lo comprueba el usuario).
- Prueba manual: canciones que salen por YouTube suenan y son la versión correcta (no live/remix ni otro artista; explícita si toca).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `di/TrackMatcher.kt` | Nuevo (230 líneas) con el bloque movido |
| `di/SongPlayer.kt` | Bloque y `featSearchPattern` eliminados; 4 imports de `TrackMatcher.*`; fuera `java.text.Normalizer` y `kotlin.math.abs` (solo los usaba el bloque) |

## 5. Fuera de alcance
Cambiar lógica, renombrar, mover más partes de `SongPlayer`, tests (→ IT-28).

## 6. Sugerencias
- **S14:** el marcador `clean` (`TrackMatcher.kt:110`) contiene `пensored`, con una `п` cirílica en vez de la `c` latina,
  así que nunca casa con "censored". Probable errata de H4zh4n. Arreglo: cambiar la letra (iteración propia, cambia comportamiento).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Movido con script. Bloque y `featSearchPattern` idénticos al original (`diff` vacío salvo `private` → `internal`). Comparado con el `TrackMatcher` de la fase 1 (`d6b02d8`): solo difieren las novedades de H4zh4n. `SongPlayer.kt` 2612 → **2397 líneas**. Sin compilar (lo hace el usuario en Android Studio) | `6f9a5ab` |

**Visibilidad:** igual que en IT-14, quedan `internal` `CandidateScore`, `featSearchPattern`, `normalizedForMatch`,
`foldLatinDiacritics`, `bigramSimilarity`, `versionMarkers`, `ytmusicTransferScore` e `isAcceptableMatch` (las 4 primeras
para que IT-28 pueda probarlas sin tocar `TrackMatcher`); el resto sigue `private`.
