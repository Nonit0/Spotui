# IT-40 · Crear las playlists en Spotify como el reproductor web (spclient)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-39 |
| Estado | ✅ Hecha y probada (2026-10-29) |

## 1. Qué hacer
En la prueba de la IT-39, `POST api.spotify.com/v1/me/playlists` respondió **429 "API rate limit exceeded"** a los
3 intentos (unos 90 s de espera hasta crearla local). La API pública no admite esto con el token del reproductor web.

Crear la playlist como lo hace el reproductor web, por `spclient.wg.spotify.com`, que la app ya usa con el mismo token
para letras (`color-lyrics`) y radio (`inspiredby-mix`):
1. `POST /playlist/v2/playlist` con la operación `UPDATE_LIST_ATTRIBUTES` (nombre) → devuelve `spotify:playlist:…`.
2. `POST /playlist/v2/user/{usuario}/rootlist/changes` con `ADD` (al principio) → aparece en tu biblioteca.
3. La canción (en "Saved in") se añade como antes, por GraphQL `addToPlaylist`.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Probamos spclient? → **Sí.** Aviso: el formato de estas llamadas no es público (sale del reproductor web);
  puede hacer falta otra prueba con logcat para ajustarlo.

## 3. Salida esperada
- Crear desde Biblioteca o desde "Saved in" → la playlist aparece en Spotify (app oficial o web) en unos segundos.
- Si falla: aviso de playlist local **al momento** (ya no hay reintentos con espera) y en logcat una línea
  `createPlaylist(<nombre>) create HTTP …` o `… rootlist HTTP …` con la respuesta de Spotify.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `spotify/.../Spotify.kt` | `createPlaylist(name)`: REST + reintentos 429 → spclient playlist/v2 (crear + añadir al rootlist). Se quita el parámetro `public` |
| `app/.../data/api/SpotifySync.kt` | Sin el parámetro `public` |

## 5. Fuera de alcance
- Pública/privada: se queda con lo que ponga Spotify por defecto.
- Subir a Spotify las playlists locales ya existentes.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | spclient playlist/v2 + rootlist. No compilado aquí (lo hace el usuario en Android Studio) | `e478ef3` |
| 2026-10-29 | ✅ Probada | El usuario: "ahora las playlist se crean en spotify" | — |
