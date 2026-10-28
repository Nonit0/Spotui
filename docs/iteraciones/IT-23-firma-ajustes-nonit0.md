# IT-23 · Firma de Ajustes a `Nonit0`

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · probada por el usuario |

## 1. Qué hacer
Al final de Ajustes (`SettingsScreen.kt:827-841`) no hay un botón "GitHub" (el índice lo describía mal): hay una firma
`Maintained with ♥ by Hazhan Salih` cuyo nombre abre `https://github.com/H4zh4n/Spotui/`.
Cambiar el nombre a `Nonit0` y el enlace a `https://github.com/Nonit0/Spotui/`.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Enlace a tu fork o al original? → "a mi fork, y créditos a H4zh4n en el readme"
- [x] Como es una firma, cambiar solo la URL confundiría. ¿Qué texto? → "la 1, Maintained with ♥ by Nonit0"

## 3. Salida esperada
- `grep -rn "H4zh4n\|Hazhan" app/src/main --include=*.kt` → sin resultados.
- En la app: al final de Ajustes se lee `Maintained with ♥ by Nonit0` y al pulsar el nombre se abre tu repo (lo comprueba el usuario).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `ui/screens/SettingsScreen.kt` | Texto del nombre (l.834) y URL (l.839) |

## 5. Fuera de alcance
- Créditos a H4zh4n → en el README (S10).

## 6. Sugerencias
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | `grep "H4zh4n\|Hazhan"` en `app/src/main` → sin resultados. Diff: 1 fichero, 2+/2−. Sin compilar (lo hace el usuario en Android Studio) | `110cf89` |
| 2026-10-28 | ✅ Probada | El usuario compila y prueba la app en el móvil: "ya la he probado, todo funciona bien" | — |
