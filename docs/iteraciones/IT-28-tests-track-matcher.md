# IT-28 · Tests unitarios de `TrackMatcher`

| Campo | Valor |
|---|---|
| Tipo | Código (solo tests) |
| Depende de | IT-27 |
| Estado | ✅ Escrita (2026-10-28) · **pendiente de que el usuario la ejecute** |

## 1. Qué hacer
Crear `app/src/test/java/com/music/spotui/di/TrackMatcherTest.kt` con casos que fijen el comportamiento **actual**
(plan de IT-15, que en la fase 1 no llegó a hacerse, más casos de las novedades de H4zh4n).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? → "si, sigue con la IT-28"
- Los tests los ejecuta el usuario (Gradle satura su PC), así que los valores esperados se han calculado a mano.

## 3. Salida esperada
`./gradlew :app:testDebugUnitTest --tests "com.music.spotui.di.TrackMatcherTest"` (o botón ▶ en Android Studio)
→ 10 tests en verde.
Si alguno falla: **no se cambia `TrackMatcher`**; se ajusta el test al comportamiento real o se abre iteración nueva.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/src/test/java/com/music/spotui/di/TrackMatcherTest.kt` | Nuevo, 10 tests |

Casos (canción de referencia: "Blinding Lights" · The Weeknd · After Hours · 200 s):

| Test | Qué fija |
|---|---|
| `normalizedForMatchDropsFeatAndPunctuation` | Quita "(feat. X)", signos y espacios de más |
| `foldLatinDiacriticsRemovesAccents` | "Beyoncé" → "Beyonce" |
| `bigramSimilarityIgnoresAccents` | "Beyoncé" vs "Beyonce" = 1.0 |
| `bigramSimilarityIsLowForDifferentTitles` | Mismo título (mayúsculas aparte) = 1.0; títulos distintos < 0.3 |
| `versionMarkersDetectsAlternateVersions` | live, remix, acoustic, remaster; nada en un título limpio |
| `acceptsExactMatch` | Coincidencia exacta: puntuación 4.0, aceptada |
| `rejectsUnexpectedLiveVersion` | "(Live)" no pedido → rechazada |
| `rejectsSameTitleByDifferentArtist` | Mismo título y duración, otro artista → rechazada |
| `softMarkerIsPenalisedButAccepted` | "(Remastered)": penalización 0.65, pero aceptada |
| `explicitMismatchRaisesTheBar` | Puntuación 2.5: aceptada sin preferencia o pidiendo no explícita; rechazada si se pide explícita (mínimo 2.25 → 2.85) |

## 5. Fuera de alcance
Cambiar `TrackMatcher`. Transliteración (cirílico, japonés): `android.icu.Transliterator` no existe en tests JVM
(con `isReturnDefaultValues = true` devuelve `null`). La errata `пensored` (S14) no se fija en un test.

## 6. Sugerencias
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Escrita | Sin ejecutar: valores esperados calculados a mano. Pendiente: el usuario la lanza y se anota aquí el resultado | `0a2a8d2` |
