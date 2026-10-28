# IT-38 · La barra inferior resalta la pestaña equivocada y Home no vuelve al inicio

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · pendiente de prueba manual |

## 1. Qué hacer
Arreglar el fallo que describió el usuario:

> App iniciada (Home) > abres una playlist (en Home) > Search/Library > vuelves a Home pulsando Home
> (1.er error: queda resaltado Search/Library, pero estás en la playlist abierta en Home) > vuelves a darle a Home
> (no devuelve a la pantalla Home original, se queda en la playlist) y así con todos.

Causa (`MainBottomNavigation.kt`):
- La pestaña resaltada (`currentTab`) solo se actualizaba cuando la pantalla visible era una raíz
  (`currentRoute in rootRoutes`). Al cambiar de pestaña, `restoreState = true` devuelve la última pantalla de esa
  pestaña (la playlist), que no es raíz → `currentTab` se quedaba en Search/Library. **1.er error.**
- Al pulsar Home otra vez, como `currentTab != "home"`, se repetía la navegación de "cambiar de pestaña" (que vuelve
  a restaurar la playlist) en vez de la rama "pestaña ya activa en una subpantalla" (`popBackStack(home)`), que ya
  existía. **2.º error**, consecuencia del 1.º.

No hacía falta tocar la pila ni las opciones de navegación.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Se arregla? → "ahora deberíamos arreglar el manejo de fragmentos…"
- Comportamiento elegido (como Spotify): 1.er toque en una pestaña → vuelve a donde estabas en ella; 2.º toque →
  a su pantalla inicial.

## 3. Salida esperada
Repetir la prueba del usuario: al volver a Home queda resaltado **Home** (mostrando la playlist) y un segundo toque
lleva a la pantalla Home original. Igual con Search y Library.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../ui/navigation/MainBottomNavigation.kt` | En `onClick`, al cambiar de pestaña: `currentTab = item.route` antes de `navigate` (+ comentario) |

## 5. Fuera de alcance
- Reorganizar la navegación en grafos anidados por pestaña.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Una línea + comentario. No compilado aquí (lo hace el usuario en Android Studio) | `f2ef436` |
