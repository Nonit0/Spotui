# IT-57 · Episodios de podcast por el reproductor web de Spotify

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-56 |
| Estado | ❌ Descartada (2026-10-29): el reproductor web no reproduce |

## 1. Qué hacer
S28 (opción A): con `webPlayerEnabled = false` (`SongPlayer.kt:189`) los episodios no llegaban al reproductor
web (`SpotifyWebPlayer.playEpisode`) y caían en la búsqueda de YouTube, que ponía otra cosa.

Comprobado por adb al arrancar (2026-10-29 19:44): `widevine canPlay=true`, `command API ready` → el WebView
del móvil sí tiene Widevine.

Hacer: los episodios van al reproductor web si `SpotifyWebPlayer.canPlay`, aunque `webPlayerEnabled` y la
preferencia `isWebPlaybackEnabled` sigan apagados (la música no cambia). Mientras suena un episodio por web,
`webPlaybackActive()` es `true`, así que play/pausa/seek/progreso y la notificación usan el reproductor web;
al pasar a una canción normal se pausa el web.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Opción A (reproductor web) antes que B (aviso *no disponible*)? → **Sí, prueba la A.**

## 3. Salida esperada
Al pulsar un episodio suena el audio real del episodio. En el log: `playUri(spotify:episode:…)` y ningún
`resolveVideoId: … 'episode:…'`.

### Pasos del usuario
1. Compilar debug y abrir un episodio desde el chip *Podcasts*.
2. Pausa / reanudar / mover la barra; mirar que el progreso avanza.
3. Desde el episodio, poner una canción normal: el episodio debe callarse y sonar la canción.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../di/SongPlayer.kt` | `webEpisodeActive`; rama de episodios solo exige `canPlay`; `webPlaybackActive()` la tiene en cuenta; pausa del web al salir del episodio |

## 5. Fuera de alcance
- Sin Widevine (`canPlay=false`) los episodios siguen cayendo a YouTube: eso es la opción B.
- Pasar solo al siguiente elemento de la cola cuando termina el episodio (comprobar en la prueba).
- La música sigue sin reproductor web (S3).

## 6. Sugerencias (nuevas iteraciones candidatas)
- S31: reproducir los episodios sin DRM (MP3 externos) directamente con ExoPlayer.
- S32: no buscar letras de episodios (lrclib responde 503).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ❌ Probada, no funciona | El episodio llega al web (`playUri(spotify:episode:5cSHvwZ2AgFNvNR0XKvyl5)`, página `open.spotify.com/episode/…`), pero `console: Failed to resolve endpoints via x-resolve … Failed to fetch (apresolve.spotify.com)`; `play click: "clicked"` ×7 sin audio y se cierra la sesión DRM. Tampoco cae a YouTube (el cambio lo impedía) → silencio. Los 503 del log son de `LyricsApi` (lrclib), no del audio. Cambio revertido (`git restore`), sin commit → IT-58 (opción B) | — |
