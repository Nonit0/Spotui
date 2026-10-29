# IT-56 · Podcasts desde `searchDesktop` (sin REST `/search`)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-29) · probada por el usuario |

## 1. Qué hacer
El chip *Podcasts* de Inicio sale vacío. Log por adb (2026-10-29 19:33): `HomeViewModel.fetchPodcasts()` →
`Api.searchEverything("podcast")`. Las canciones/álbumes/artistas vienen del GQL `searchDesktop` (**200**, y
la respuesta ya trae las secciones `podcasts` y `episodes`), pero los podcasts se piden aparte al REST
`GET /search` (`Spotify.searchPodcasts`), que da **429 Rate limited** → `shows=-1 episodes=-1` → lista vacía.
El reintento con `"show"` vuelve a pedir `GET /search` y alarga el bloqueo.

Hacer: parsear `podcasts` y `episodes` de la respuesta de `searchDesktop` y usarlos en `searchEverything`;
`searchPodcasts` (REST) solo si el GQL no trae ninguno.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Abro IT-56 y lo implemento? → **Sí.**

## 3. Salida esperada
Al pulsar el chip *Podcasts* aparecen shows y episodios aunque el REST esté en 429. En el log sale
`podcasts: shows=N episodes=M` con N, M > 0 y ya no aparece `REST GET search`.

### Pasos del usuario
Compilar debug, abrir Inicio → chip *Podcasts* (también la búsqueda normal: sección *Podcasts*).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `spotify/.../models/SpotifySearchResult.kt` | Campos `shows` y `episodes` |
| `spotify/.../Spotify.kt` | `search()` parsea `podcasts`/`episodes` del GQL (`parseGqlSearchShow`, `parseGqlSearchEpisode`) |
| `app/.../data/api/Api.kt` | `searchEverything` usa los del GQL; REST solo si vienen vacíos |

## 5. Fuera de alcance
- Abrir un show sigue usando REST (`shows/{id}` y `shows/{id}/episodes`): si da 429, otra iteración.
- `me/top/tracks` y `me/top/artists` (chip *Music*) también dan 429.
- El reintento `"show"` de `HomeViewModel.fetchPodcasts()` se deja igual.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S28: los episodios se buscan en YouTube (reproductor web desactivado) y suena otra cosa.
- S29: no repetir `me/top/*` mientras dure el `retryAfter` del 429.
- S30: medir el *lag* de Inicio/Podcasts.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | `searchEverything` toma shows/episodios del GQL `searchDesktop`; REST `/search` solo de respaldo | `50f02a7` |
| 2026-10-29 | ✅ Probada | El chip *Podcasts* muestra shows y episodios; ya no sale `REST GET search` en el log. Al reproducir, los episodios suenan a otra cosa (YouTube) → S28 | — |
