# IT-24 · Renombrar `LibraryScreeen.kt` → `LibraryScreen.kt`

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · probada por el usuario |

## 1. Qué hacer
Renombrar `app/src/main/java/com/music/spotui/ui/screens/LibraryScreeen.kt` (sobra una "e") a `LibraryScreen.kt`
con `git mv`, sin tocar el contenido. Igual que IT-05 en la fase 1.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Lo hago? → "si, hazlo"

## 3. Salida esperada
- `grep -rn "LibraryScreeen"` en fuentes (`app/src`, `spotify`, `innertube`; `.kt`, `.kts`, `.pro`, `.xml`) → sin resultados (ya lo estaba antes del cambio; no hay `@JvmName` ni reglas ProGuard que dependan del nombre).
- `git show --stat -M` → renombrado al 100 %, 0+/0−.
- Compila en Android Studio (lo comprueba el usuario). La clase generada pasa de `LibraryScreeenKt` a `LibraryScreenKt`; los restos en `app/build/` se regeneran.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `ui/screens/LibraryScreeen.kt` → `ui/screens/LibraryScreen.kt` | Solo nombre |

## 5. Fuera de alcance
- Cualquier cambio dentro del fichero.

## 6. Sugerencias
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Renombrado al 100 % (0+/0−). Sin referencias al nombre viejo en fuentes. Sin compilar (lo hace el usuario en Android Studio) | `c2acd1c` |
| 2026-10-28 | ✅ Probada | El usuario compila y prueba la app en el móvil: "ya la he probado, todo funciona bien" | — |
