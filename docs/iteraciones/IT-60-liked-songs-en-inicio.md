# IT-60 · *Liked Songs* en la cuadrícula de Inicio

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | 🧪 Pendiente de prueba manual |

## 1. Qué hacer
Poner siempre *Liked Songs* como primera casilla de la cuadrícula de accesos rápidos de Inicio
(chips *All* y *Music*). Spotify ya no la manda en el feed que nos llega por la API, así que la
añadimos nosotros y abre la pantalla de favoritos (`Routes.Liked`).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Confirmas que hago exactamente esto?

> Respuesta del usuario: lo pidió directamente (2026-11-26): "en el fragmento de inicio que salga liked songs".

## 3. Salida esperada
En Inicio (*All* y *Music*, sin *Following*) la primera casilla es *Liked Songs* (degradado morado + corazón,
igual que en Biblioteca); al tocarla se abre la lista de favoritos. Debajo, hasta 7 accesos del feed (8 en total).
Si el feed no trae cuadrícula, aparece igualmente la casilla de *Liked Songs* sola.

## 4. Cambios

| Fichero | Cambio |
|---|---|
| `app/src/main/java/com/music/spotui/ui/screens/HomeScreen.kt` | `HomeShortcutGrid` antepone una casilla *Liked Songs* (`null` en la lista), quita del feed cualquier "Liked Songs" duplicada y limita a 7 más; `HomeFeedContent` y `HomeMusicFeedContent` siempre muestran la cuadrícula |

## 5. Fuera de alcance
- `SumUpHomeScreen`/`HomePlaylistGrid` (Inicio sin feed, con datos locales): no se toca.
- Menú de guardar en playlists al dar *like* → IT-61 (otra rama).

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-11-26 | 🧪 Pendiente de prueba manual | Código hecho, sin compilar (lo compila el usuario en Android Studio) | `f6c9cd6` |
