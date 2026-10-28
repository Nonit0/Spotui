# IT-42 · En una playlist solo se resalta la copia que suena (S21)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-41 |
| Estado | ✅ Hecha y probada (2026-10-29) |

## 1. Qué hacer
S21: las listas pintan de verde la canción que suena comparando por id, así que si se repite se resaltan **todas**
las copias. Solo las playlists pueden repetir canciones (álbumes, Liked Songs, búsqueda, artista, descargas y
archivos locales no), así que el cambio va solo en `PlaylistScreen`.

Cada fila calcula qué copia es (0 = 1.ª aparición en la lista) y se resalta solo si coincide con la copia que suena
en la cola (`CurrentSongState.playingOccurrence()`, que usa la posición de IT-41).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Seguimos con S21? → "ahora la S21".

## 3. Salida esperada
Playlist con la canción A dos veces → tocar la 2.ª copia → solo esa fila en verde. Al pasar a otra canción, se apaga.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../di/CurrentSongState.kt` | `playingOccurrence()`: nº de copia de la canción que suena dentro de la cola |
| `app/.../ui/viewmodel/PlaylistViewModel.kt` | `currentSongOccurrence()` |
| `app/.../ui/screens/PlaylistScreen.kt` | La fila se resalta si coincide el id **y** el nº de copia |

## 5. Fuera de alcance
- Si la cola no es esta playlist (p. ej. suena desde otra lista), se compara el nº de copia dentro de la cola, que
  puede no casar con esta lista: caso raro, se acepta.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí (lo hace el usuario en Android Studio) | `da843e3` |
| 2026-10-29 | ✅ Probada | El usuario: "funciona" | — |
