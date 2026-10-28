# IT-33 · Arreglar el salto al final en Deezer y blindar el lector de Canvas

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-31 |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
Traer `f1631c8` de RMNO21 (rama `archivo/original-1.6`) con cherry-pick, conservando su autoría.
Arregla el bug encontrado en IT-31: saltar dentro del último trozo parcial de una canción de Deezer daba `IllegalArgumentException` (longitud negativa en `copyOfRange`).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Se trae? → "si, trae los tres que propones" (propuesta de IT-31, §7)

## 3. Salida esperada
- Compila en Android Studio.
- Con Deezer Direct (ARL): saltar a los últimos segundos de una canción ya no da error. Los Canvas de Spotify se siguen viendo.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../deezer/DeezerDataSource.kt` | `fillPending()`: si `dropFirst >= total`, descarta el trozo y lee el siguiente |
| `spotify/.../SpotifyCanvas.kt` | `readVarint`/`readBytes`/`skip`: no salen del array ni retroceden con longitudes negativas |

## 5. Fuera de alcance
Probar Deezer sin ARL (no hay forma).

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Cherry-pick sin conflictos. No se ha compilado (lo hace el usuario en Android Studio) | `4500f5c` |
