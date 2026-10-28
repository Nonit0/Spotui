# IT-22 · Apuntar las actualizaciones a `Nonit0/Spotui`

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
En `app/src/main/java/com/music/spotui/data/preferences/SettingsPref.kt:36`, cambiar
`DEFAULT_UPDATE_REPO_URL` de `https://github.com/H4zh4n/Spotui` a `https://github.com/Nonit0/Spotui`.

`data/update/UpdateChecker.kt` construye con esa URL la llamada a
`https://api.github.com/repos/<repo>/releases/latest` y saca la versión del `tag_name`
(primer `\d+(\.\d+)+`, así que valen `v1.5.1`, `1.5.1`, etc.). Se compara con `versionName` (hoy `1.5.0`).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Lo cambio ya, sabiendo que hasta tu primera release no saldrá ningún aviso?

> Respuesta del usuario: "a, a partir de ahora usaremos mis releases, no creo q el repo de H... tenga actualizaciones"

## 3. Salida esperada
- `grep -rn "H4zh4n" app/src/main --include=*.kt` → solo `SettingsScreen.kt:839` (enlace de Ajustes, → IT-23).
- Compila en Android Studio (lo comprueba el usuario).
- En Ajustes, el campo de repo de actualizaciones muestra `https://github.com/Nonit0/Spotui` en una instalación limpia.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `data/preferences/SettingsPref.kt` | 1 constante |

## 5. Fuera de alcance
- Enlace "GitHub" de Ajustes (`SettingsScreen.kt:839`) → IT-23.
- Publicar la primera release del fork.

## 6. Sugerencias
- Solo cambia el **valor por defecto**: si en un móvil ya se guardó a mano otra URL en `update_repo_url`, se respeta.
  El botón de restablecer de Ajustes (`SettingsScreen.kt:624`) la devuelve al nuevo valor por defecto. No hace falta nada más.
- Al publicar releases, usar tags con número de versión mayor que `versionName` (p. ej. `v1.5.1`) para que el aviso salga.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | `grep "H4zh4n"` en `app/src/main` → solo `SettingsScreen.kt:839`. Diff: 1 fichero, 1+/1−. Sin compilar (lo hace el usuario en Android Studio) | `5d843a5` |

**Efecto:** hasta que publiques tu primera release en `Nonit0/Spotui`, no sale ningún aviso de actualización (la API devuelve 404). Después avisará de **tus** versiones.
