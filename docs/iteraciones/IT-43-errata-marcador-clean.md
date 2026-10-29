# IT-43 · Errata en el marcador `clean` de TrackMatcher (S14)

| Campo | Valor |
|---|---|
| Tipo | Código + test |
| Depende de | IT-27, IT-28 |
| Estado | ✅ Hecha (2026-10-29) · test ejecutado por el usuario |

## 1. Qué hacer
S14 (hallazgo de IT-27): en `TrackMatcher.kt:110` el término `пensored` empieza por una **п cirílica**, así que el
marcador "clean" nunca reconocía "Censored" en los títulos de YouTube. Cambiarlo por `censored` latino.

Sin riesgo con "uncensored": `markerPattern` exige espacio o inicio/fin alrededor del término, así que
"uncensored" sigue siendo solo "explicit".

## 2. Preguntar al usuario (antes de empezar)
- [x] "siguiente del backlog" → S14 (de las pequeñas propuestas).
- [x] ¿Quién ejecuta el test? → **El usuario.**

## 3. Salida esperada
`TrackMatcherTest` en verde (11 tests: 10 de IT-28 + 1), incluido el nuevo `versionMarkersTellCensoredFromUncensored`.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../di/TrackMatcher.kt` | `пensored` → `censored` |
| `app/src/test/.../di/TrackMatcherTest.kt` | Test: "(Censored)" → `clean`; "(Uncensored)" → `explicit` y no `clean` |

## 5. Fuera de alcance
-

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | Test no ejecutado aquí (lo pasa el usuario) | `451ce45` |
| 2026-10-29 | ✅ Probada | `TrackMatcherTest` 11/11 en verde, 0 fallos, 0 skipped (usuario). La salida esperada decía 12 por error: son 11 | — |
