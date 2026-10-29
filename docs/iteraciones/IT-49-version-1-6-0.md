# IT-49 · Versión 1.6.0 para la primera release del fork

| Campo | Valor |
|---|---|
| Tipo | Build |
| Depende de | IT-48 |
| Estado | ✅ Hecha (2026-10-29) · pendiente de prueba manual |

## 1. Qué hacer
La app seguía con la versión de H4zh4n (`1.5.0`, `versionCode` `202608150`). Darle número propio a la
primera release del fork.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿`1.5.1` o `1.6.0`? → **`1.6.0`** (hay funciones nuevas, p. ej. crear playlists en Spotify).

## 3. Salida esperada
- `versionName` `1.6.0`; `versionCode` `202610290` (mismo formato `AAAAMMDD` + contador, mayor que el anterior).
- La APK sale como `Spotui_v1.6.0.apk` y Ajustes muestra 1.6.0. Se instala encima de la release de IT-47 (misma clave).
- El aviso de actualizaciones compara con este `versionName`: avisará cuando haya una release > 1.6.0 en `Nonit0/Spotui`.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/build.gradle.kts` | `versionCode` 202608150 → 202610290; `versionName` 1.5.0 → 1.6.0 |

## 5. Fuera de alcance
- El "(1.5.0)" del README se refiere a la versión de H4zh4n en la que se basa el fork: se queda.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí | `6bde673` |
