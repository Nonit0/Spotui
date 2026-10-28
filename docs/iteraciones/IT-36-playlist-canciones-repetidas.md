# IT-36 · La app se cierra al abrir una playlist con canciones repetidas

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
Que `PlaylistScreen` no se cierre cuando la playlist tiene la misma canción dos veces.

Error que pasó el usuario (playlist privada de ~254 canciones; una pública de ~240 abre bien):
```
java.lang.IllegalArgumentException: Key "900223239" was already used. If you are using LazyColumn/Row
please make sure you provide a unique key for each item.
```

Causa: cada fila usaba `key = song.id`, y `song.id = stableId("track:<id de Spotify>")` (`Api.kt:99`).
Spotify permite repetir una canción en una playlist → dos filas con la misma clave → Compose lanza la excepción.
No tiene que ver con que sea privada ni con el parseo del JSON (la lista ya se había cargado y se estaba dibujando).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Se arregla? → "Antes de las sugerencias vamos a arreglar una cosa, cuando abro una playlist…"
- Decisión tomada por defecto: **mantener las repetidas** (como en Spotify) en vez de quitarlas, para no cambiar
  el contenido de la playlist ni la cola.
- El prompt de análisis que pasó el usuario (parseo, paginación, auth…) no hace falta: la traza apunta directamente
  a la clave de la lista.

## 3. Salida esperada
La playlist privada abre y muestra las canciones repetidas.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../ui/screens/PlaylistScreen.kt:671` | `key = { _, song -> song.id }` → `key = { index, song -> "${song.id}_$index" }` |

## 5. Fuera de alcance
- `QueueScreen.kt:211` usa también `key = s.id`: si se reproduce esa playlist, la cola tendrá la canción repetida y
  puede cerrarse igual. Su arrastre usa `draggingId == song.id`, así que no es el mismo cambio de una línea → S19.
- `LikedSongsScreen.kt:449`: las favoritas de Spotify no se pueden repetir; se deja.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S19: claves únicas en `QueueScreen` (y su arrastre) para colas con la misma canción dos veces.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Cambio de una línea. No compilado aquí (lo hace el usuario en Android Studio) | `35d234d` |
