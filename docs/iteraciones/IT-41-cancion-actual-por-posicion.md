# IT-41 · La canción que suena se localiza por su posición en la cola, no por id (S20)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-37 |
| Estado | ✅ Hecha (2026-10-29) · pendiente de prueba manual |

## 1. Qué hacer
S20: con la misma canción dos veces en la cola, todo buscaba "la que suena" con `indexOfFirst { it.id == songId }`, que
siempre da la **1.ª copia**. Efectos:
- **Visual:** si suena la 2.ª copia, la cola (y el carrusel de portadas del reproductor) se colocan en la 1.ª.
- **Reproducción (más grave):** `next()`/`previous()`, el fin de canción, el crossfade y el aviso de error partían de la
  1.ª copia, así que "siguiente" desde la 2.ª copia volvía a la canción de detrás de la 1.ª (bucle).

Las pantallas ya pasaban la posición correcta al reproducir (`songIndex`), pero `CurrentSongState.updateSongState` la
sustituía por la 1.ª aparición del id.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Empezamos por S20? → "me parece bien". Incluye el fallo de reproducción, que tiene la misma causa.

## 3. Salida esperada
Cola con la canción A en dos posiciones (p. ej. una playlist que la repite o "Add to queue" + "Play next"):
- Tocar la **2.ª** copia de A → la cola marca esa fila como "Now playing" y lo que sigue es lo que va detrás de ella.
- "Siguiente" / fin de canción → pasa a la canción de detrás de la 2.ª copia, no de la 1.ª.
- Mover o quitar filas de la cola mientras suena → la canción actual sigue marcada correctamente.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../di/CurrentSongState.kt` | `positionIn(q)`: usa `songIndex` si apunta a la canción que suena; si no, 1.ª aparición por id y luego por url. `updateSongIndex()`. `updateSongState` respeta el índice recibido si apunta a esa canción. Shuffle parte de `positionIn` |
| `app/.../ui/viewmodel/PlayerViewModel.kt` | `currentPositionIn` (ahora pública) usa `positionIn`; `playNext`/`moveQueueItem`/`removeQueueItem` mantienen `songIndex` al día (`playNext` ya no quita la copia que está sonando) |
| `app/.../di/SongPlayer.kt` | `next`, `previous` y crossfade usan `positionIn` |
| `app/.../ui/notification/PlaybackService.kt` | Repetir una / fin de cola / error usan `positionIn` |
| `app/.../ui/screens/QueueScreen.kt`, `PlayerScreen.kt` | Cola, carrusel de portadas y precarga de vecinas usan `currentPositionIn` |

## 5. Fuera de alcance
- Las listas (playlist, álbum, búsqueda…) colorean de verde **todas** las copias de la canción que suena, porque
  comparan por id → S21.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S21 (arriba).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí (lo hace el usuario en Android Studio) | `aba1c5f` |
