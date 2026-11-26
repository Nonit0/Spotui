# IT-61 · Menú «Guardado en» en todos los corazones

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ⏳ Pendiente de prueba manual (no se ha compilado ni ejecutado nada) |

## 1. Qué hacer
Que **todo** botón de like/corazón de una canción abra el menú `SavedInSheet` (Liked Songs + playlists
con casillas y confirmación), tanto si la canción ya está en favoritos como si no, en lugar de
añadirla/quitarla de Liked Songs directamente.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Confirmas que hago exactamente esto? (petición directa del usuario)

> Respuesta del usuario: petición original: al pulsar like sin favorito, abrir menú con Liked Songs y
> playlists con checkboxes y confirmar; con favorito, abrir el mismo menú con los estados marcados.

## 3. Salida esperada
- Pulsar el corazón/+/✓ en reproductor, mini-reproductor, álbum, artista, búsqueda, Liked Songs y
  las filas «Add/Remove to Liked Songs» de los menús de la canción abre el menú «Saved in».
- Nada cambia hasta pulsar **Save**; cerrar el menú descarta los cambios. Sin cambios el botón dice **Done**.
- Los estados actuales salen marcados (Liked Songs, playlists locales, playlists propias de Spotify).
- Tras guardar, el icono de la fila se actualiza y la canción aparece/desaparece en la playlist de Spotify.

## 4. Cambios

| Fichero | Cambio |
|---|---|
| `ui/components/SavedInSheet.kt` | Selección *staged* (`pending`) + botón Save/Done (`applyNow`); ya no aplica al tocar cada fila. Playlists remotas filtradas a las propias (owner == `Spotify.me()`; sin perfil se muestran todas). Tocar una playlist remota se ignora hasta saber si ya tiene la canción. «New playlist» sigue siendo inmediato |
| `ui/screens/PlayerScreen.kt` | Corazón y fila del menú abren el sheet siempre |
| `ui/components/AppComponents.kt` | Mini-reproductor: el `+` abre el sheet |
| `ui/screens/AlbumScreen.kt`, `ArtistScreen.kt`, `SearchScreen.kt`, `ui/components/LikedSongsAlbum.kt` | `onClick` abre el sheet; `onLikedChanged` refresca el `likeState` compartido |
| `ui/components/SongOptionsSheet.kt` | La fila de like abre el sheet |

API usada (ya existía): `SpotifySync.addTrackToPlaylist` / `removeTrackFromPlaylist` (GQL `addToPlaylist` /
`removeFromPlaylist`, no el REST con 429), `SpotifySync.setTrackSaved`, `LocalPlaylistPref`.

## 5. Fuera de alcance
No se tocan IT-60, ni la creación de playlists, ni `BackupHelper`/`Api` (siguen usando `addLikedSongId` directo).

## 6. Sugerencias (nuevas iteraciones candidatas)
- **Playlists colaborativas**: `parsePlaylistWrapper` (`Spotify.kt`) no rellena `collaborative` (siempre `false`) y
  `libraryV3` no se ha comprobado que lo dé, así que las colaborativas ajenas quedan **ocultas** en el menú. Leer ese
  campo del GQL y mostrarlas.
- `SpotifySync.removeTrackFromPlaylist` solo mira las primeras 100 pistas de la playlist; en listas largas puede no
  encontrar la canción (igual que `playlistTrackIds` para marcar el estado).
- Estado de las playlists remotas: se pide una a una al abrir (cacheado por sesión); con muchas playlists son muchas peticiones.
- Si una escritura en playlist remota falla solo sale un Toast genérico; no se revierte nada (ya se cerró el menú).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-11-26 | ⏳ Hecha, sin compilar | Código escrito sin ejecutar Gradle; falta prueba manual en Android Studio / móvil | ver `git log` (`feat: … (IT-61)`) |
