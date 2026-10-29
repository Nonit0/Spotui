# IT-44 · Borrar las preferencias de la cookie de YouTube sin uso (S15)

| Campo | Valor |
|---|---|
| Tipo | Código (borrado) |
| Depende de | IT-30 |
| Estado | ✅ Hecha (2026-10-29) · probada por el usuario |

## 1. Qué hacer
S15 (hallazgo de IT-30): `KEY_YT_COOKIE`, `getYoutubeCookie`, `setYoutubeCookie` e `isYoutubeLoggedIn`
(`SettingsPref.kt`) no se usan en ningún sitio; no hay login de YouTube (`YouTube.cookie = null`). Borrarlos.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Empezar la IT-44 (S15)? → **Sí.**

## 3. Salida esperada
La app compila igual y nada cambia en su comportamiento. Antes de borrar se comprobó con `grep` que no había
ninguna referencia fuera de `SettingsPref.kt` (ni en Kotlin, ni en XML, ni en las reglas de backup).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../data/preferences/SettingsPref.kt` | Borrados el KDoc, `KEY_YT_COOKIE` y las 3 funciones (−10 líneas) |
| `docs/CONTEXTO.md` | §4.4: las funciones ya no existen (IT-44) |

## 5. Fuera de alcance
- La clave `youtube_cookie` nunca se llegó a escribir, así que no hay datos que limpiar en los dispositivos.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí (lo compila el usuario en Android Studio) | `0234aa2` |
| 2026-10-29 | ✅ Probada | Compila y la app funciona (usuario) | — |
