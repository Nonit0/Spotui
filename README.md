# Spotui [![Downloads](https://img.shields.io/github/downloads/Nonit0/Spotui/total?style=for-the-badge&labelColor=0d1117)](https://github.com/Nonit0/Spotui/releases)

A Spotify clone for Android, built with Jetpack Compose.

This is a maintained fork of [H4zh4n/Spotui](https://github.com/H4zh4n/Spotui) (1.5.0), maintained by
[Nonit0](https://github.com/Nonit0).

[English](#english) · [Español](#español)

<!-- Ko-fi: add the maintainer's own link here once it exists -->

## Screenshot

<p align="center">
  <img src="https://github.com/user-attachments/assets/dfd41cd7-92a8-46d5-800d-a4ecfddfeef8" width="32%" alt="Screenshot_2026-07-13-21-03-36-065_com music spotui" />
  <img src="https://github.com/user-attachments/assets/64e1a3d7-fef0-422e-aa80-276e5b66d874" width="32%" alt="Screenshot_2026-07-13-21-01-15-466_com music spotui" />
  <img src="https://github.com/user-attachments/assets/a64f54b3-f69c-4073-970d-d189d5ed5da5" width="32%" alt="Screenshot_2026-07-13-20-43-45-056_com music spotui" />
</p>

_____________

## English

### Features

It connects to your real Spotify account and mirrors the Spotify experience.

- 🎵 **Playlists** — browse, play, add/remove tracks, create new playlists, all synced with your Spotify account
- 📝 **Lyrics** — Spotify's own synced lyrics, with a live preview on the player and a full-screen view
- 📻 **Spotify recommendations** — the queue continues with Spotify's real track radio (autoplay), so "up next" matches what open.spotify.com would play
- ❤️ Liked songs, followed artists, listening history and downloads (including lossless FLAC)

### What's new in this fork

- **New playlists are created in your Spotify account**, the same way the web player does it (before, they only existed on the phone).
- **Repeated tracks work properly**: playlists and the queue no longer crash when a song appears twice, and only the copy that is playing is highlighted.
- **Bottom navigation** highlights the right tab, and Home goes back to the start.
- **Deezer**: seeking to the end of a track no longer crashes.
- **Stability**: concurrency fixes and HTTP connections that are always closed.
- **Privacy**: Spotify, Deezer and SpotiFLAC sessions are excluded from Google backups.
- **Better track matching**: "Censored" titles are recognised as clean versions.
- **Updates** are checked against this fork, and releases are signed with the maintainer's own key.

The changes made by H4zh4n before this fork are listed in [CHANGELOG.md](CHANGELOG.md).

### Building

Open the project in Android Studio (JDK 17+). Debug builds work out of the box. To sign a release with your
own key, copy `keystore.properties.example` to `keystore.properties` and fill it in; without it, the release
is signed with the shared debug key.

_____________

## Español

### Funciones

Se conecta a tu cuenta real de Spotify y reproduce la experiencia de Spotify.

- 🎵 **Playlists**: explorar, reproducir, añadir/quitar canciones y crear playlists nuevas, todo sincronizado con tu cuenta de Spotify
- 📝 **Letras**: las letras sincronizadas de Spotify, con vista previa en el reproductor y vista a pantalla completa
- 📻 **Recomendaciones de Spotify**: la cola sigue con la radio real de Spotify (autoplay), igual que en open.spotify.com
- ❤️ Canciones que te gustan, artistas que sigues, historial y descargas (también FLAC sin pérdida)

### Novedades de este fork

- **Las playlists nuevas se crean en tu cuenta de Spotify**, igual que en el reproductor web (antes solo existían en el móvil).
- **Canciones repetidas**: las playlists y la cola ya no se cierran cuando una canción aparece dos veces, y solo se resalta la copia que suena.
- **Barra inferior**: resalta la pestaña correcta e Inicio vuelve al principio.
- **Deezer**: saltar al final de una canción ya no cierra la app.
- **Estabilidad**: arreglos de concurrencia y conexiones HTTP que siempre se cierran.
- **Privacidad**: las sesiones de Spotify, Deezer y SpotiFLAC no entran en el backup de Google.
- **Mejor búsqueda de canciones**: los títulos "Censored" se reconocen como versión clean.
- **Actualizaciones**: se comprueban contra este fork, y las releases se firman con la clave propia del mantenedor.

Los cambios que hizo H4zh4n antes de este fork están en [CHANGELOG.md](CHANGELOG.md).

### Compilar

Abre el proyecto en Android Studio (JDK 17+). Las builds de debug funcionan tal cual. Para firmar una release
con tu propia clave, copia `keystore.properties.example` a `keystore.properties` y rellénalo; sin él, la
release se firma con la clave de debug compartida.

_____________

## Credits

- [H4zh4n/Spotui](https://github.com/H4zh4n/Spotui) by Hazhan Salih — the version this fork is based on
- [Meld](https://github.com/) — Spotify metadata + YouTube streaming layer
- [Neptune](https://github.com/navneet851/spotify-clone-jetpack-compose) — the original Jetpack Compose Spotify clone this app started from
- [SpotiFLAC](https://github.com/spotbye/SpotiFLAC) — lossless (FLAC) track resolving
- [SimpMusic](https://github.com/maxrave-dev/SimpMusic) — crossfade / DJ-style audio filter processing

## License

GPL-3.0, see [LICENSE](LICENSE).

## Disclaimer

This project is for educational purposes only. Spotify is a trademark of Spotify AB.
