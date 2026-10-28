# IT-35 · Cerrar siempre las conexiones HTTP (Deezer, Lossless, Spotiflac)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-31 |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
Traer `bfe4bdc` de RMNO21 (rama `archivo/original-1.6`) con cherry-pick, conservando su autoría.
Se trae **sin** `UpdateChecker.kt`: en H4zh4n está reescrito con OkHttp y el parche no aplica (su propio fallo va a S17).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Se trae? → "si, trae los tres que propones" (propuesta de IT-31, §7)

## 3. Salida esperada
- Compila en Android Studio.
- Las fuentes lossless (Qobuz/TIDAL/Amazon vía LosslessSource, SpotiFLAC) se comportan igual que antes.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../deezer/DeezerSession.kt` | `post()`: lee `errorStream` si no es 2xx y hace `disconnect()` en `finally` |
| `app/.../lossless/LosslessSource.kt` | `disconnect()` en `finally` aunque haya excepción |
| `app/.../lossless/SpotiflacGated.kt` | Igual |

## 5. Fuera de alcance
La parte de `UpdateChecker.kt` del commit original (→ S17).

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Cherry-pick sin conflictos. No se ha compilado (lo hace el usuario en Android Studio) | `7df7146` |
