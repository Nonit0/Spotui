# IT-30 · Mover la cookie de YouTube a su propio fichero y excluirlo del backup

| Campo | Valor |
|---|---|
| Tipo | Análisis (iba a ser Código) |
| Depende de | IT-29 |
| Estado | 🚫 Cerrada sin cambios (2026-10-28): no hace falta |

## 1. Qué hacer
Sacar `youtube_cookie` de `settings_prefs` (que sí debe copiarse) a un fichero propio y excluirlo del backup,
migrando el valor existente.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? → "si, [...] sigue con la IT-30"
- [x] Tras el análisis: ¿cerrarla sin cambios o borrar además las funciones sin uso?

> Respuesta del usuario: "la 1, ciérrala"

## 3. Salida esperada
Análisis y decisión.

## 4. Cambios
Ninguno en código.

## 5. Análisis
- `getYoutubeCookie`, `setYoutubeCookie` e `isYoutubeLoggedIn` (`data/preferences/SettingsPref.kt:82-85`)
  **no se llaman desde ningún sitio** del proyecto (`grep` en todo el repo, sin contar `build/`).
- No hay pantalla de login de YouTube. La única WebView que lee cookies es la de Deezer (`DeezerLoginScreen.kt:141`).
- `MyApplication.kt:52` fija `YouTube.cookie = null` a propósito ("YouTube playback runs anonymously").
- Por tanto la clave `youtube_cookie` nunca se escribe y no hay ninguna credencial de YouTube que pueda acabar en el backup.
- IT-20 afirmó que H4zh4n tenía login de YouTube por WebView: era un error (se vieron las funciones y se supuso que se usaban).
  Se corrige `CONTEXTO.md` (§4.4 y D6).

## 6. Sugerencias
- **S15:** borrar las 3 funciones sin uso y `KEY_YT_COOKIE` de `SettingsPref.kt:76-85`.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | 🚫 Cerrada sin cambios | No hay cookie de YouTube que proteger. Docs corregidos; S15 al backlog | (solo docs) |
