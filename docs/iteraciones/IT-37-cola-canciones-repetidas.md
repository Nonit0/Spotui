# IT-37 · La cola se cierra con canciones repetidas

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-36 |
| Estado | ✅ Hecha y probada (2026-10-28) |

## 1. Qué hacer
S19: que `QueueScreen` funcione cuando la cola tiene la misma canción dos veces (p. ej. al reproducir la playlist
de IT-36). El usuario confirmó que la cola también se cerraba.

Mismo fallo que IT-36 (`key = s.id` repetida) y, además, todo lo de la cola buscaba la canción por id
(`indexOfFirst { it.id == song.id }`), así que con dos copias:
- tocar la 2.ª copia reproducía desde la posición de la 1.ª;
- arrastrar la 2.ª movía la 1.ª;
- deslizar para quitar una borraba **las dos** (`removeFromQueue` hacía `removeAll { it.id == song.id }`).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Se arregla? → "Pues si que era eso y la cola tambein ocurre"

## 3. Salida esperada
Con una cola que tenga una canción repetida: la pantalla de cola abre; tocar, arrastrar y deslizar actúan sobre
la copia concreta.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../ui/screens/QueueScreen.kt` | Cada fila se identifica por `id` + nº de aparición (`"<id>_<n>"`), que no cambia al arrastrarla por encima de otra canción. Tocar/arrastrar/quitar usan `indexOfOccurrence(id, n)` (nueva función privada). `draggingId: Int` → `draggingKey: String?`. Fuera el import sin uso `mutableIntStateOf` |
| `app/.../ui/viewmodel/PlayerViewModel.kt` | `removeFromQueue(song)` (borraba todas las copias; solo lo usaba la cola) → `removeQueueItem(index)` |

## 5. Fuera de alcance
- La canción **actual** se localiza por id (`curIdx = indexOfFirst { it.id == currentId }`, y el reproductor
  entero usa `currentSongId`): si suena la 2.ª copia, la cola se dibuja como si sonara la 1.ª. Arreglarlo implica
  llevar la posición en todo el reproductor → S20.
- Arrastrar una copia justo por encima de su gemela intercambia sus números de aparición (el arrastre puede saltar
  a la otra fila). Caso muy raro; se deja.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S20: identificar la canción que suena por posición en la cola, no solo por id.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | No compilado aquí (lo hace el usuario en Android Studio) | `4bd7b46` |
| 2026-10-28 | ✅ Probada | El usuario: "Parece que todo funciona" | — |
