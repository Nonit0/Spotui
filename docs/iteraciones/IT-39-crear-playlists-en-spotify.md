# IT-39 · Crear las playlists nuevas en Spotify (local si falla)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
Al crear una playlist desde la app, que se cree en la cuenta real de Spotify y no solo como "Local Playlist".

Contexto: H4zh4n ya creaba las playlists en Spotify (`SpotifySync.createPlaylistWithTrack`), pero en `5517d19` lo
cambió por playlists locales sin explicar por qué. El helper se quedó sin usar. La llamada (`Spotify.createPlaylist`,
REST `POST me/playlists`) sigue en el módulo `:spotify`.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Dónde se crea? → **En Spotify; si falla (sin sesión, red o error de Spotify), como local con aviso.**
- [x] ¿En qué pantallas? → **Las dos:** diálogo de Biblioteca y hoja "Saved in" de una canción.

## 3. Salida esperada
- Biblioteca → crear playlist → aparece en Spotify (comprobar en la app oficial o en la web) y se abre en Spotui.
- "Saved in" → New playlist → se crea en Spotify con la canción dentro y sale en la lista de playlists de Spotify.
- Sin sesión de Spotify o si Spotify falla: aviso "Couldn't reach Spotify — … created as a local playlist" y se
  crea local como antes.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../data/api/SpotifySync.kt` | `createPlaylistWithTrack` (sin uso, fire-and-forget) → `suspend createPlaylist(...)`: devuelve la playlist o `null`; añade la canción si se pasa y vacía la caché de la biblioteca |
| `app/.../ui/screens/LibraryScreen.kt` | El diálogo ("Create Playlist") intenta Spotify primero y, si falla, local; se bloquea mientras crea |
| `app/.../ui/components/SavedInSheet.kt` | `createNow()` igual: Spotify primero (la nueva aparece arriba en la lista de Spotify), local si falla o si la canción no tiene id de Spotify |

## 5. Fuera de alcance
- Subir a Spotify las playlists locales que ya existen.
- Elegir pública/privada (se crean privadas, como hacía H4zh4n).

## 6. Sugerencias (nuevas iteraciones candidatas)
- Si en la prueba Spotify rechaza la creación (salen siempre locales), mirar el log `Spotify`/`SpotifySync`
  (`createPlaylist(...) HTTP xxx`): seguramente fue el motivo del cambio de H4zh4n y habría que crearla por GraphQL.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Spotify primero, local con aviso si falla. No compilado aquí (lo hace el usuario en Android Studio) | `7c7aa42` |
