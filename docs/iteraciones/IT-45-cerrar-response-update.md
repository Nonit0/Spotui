# IT-45 · Cerrar el `response` de OkHttp en UpdateChecker (S17)

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-31 |
| Estado | ✅ Hecha (2026-10-29) · pendiente de prueba manual |

## 1. Qué hacer
S17 (hallazgo de IT-31): en `UpdateChecker.fetchLatestRelease` el `return null` cuando la respuesta no es 2xx
salía sin cerrar el `response` de OkHttp, que se quedaba con la conexión abierta. Envolverlo en `use {}`.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Hacer la S17 como IT-45? → **Sí.**

## 3. Salida esperada
Sin cambios visibles: la comprobación de actualizaciones funciona igual. El `response` se cierra en todos los
casos (2xx, no 2xx, cuerpo nulo o excepción), y `string()` sigue leyéndose dentro del `use`.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../data/update/UpdateChecker.kt` | `execute().use { response -> … response.body?.string() } ?: return null` |

## 5. Fuera de alcance
-

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí (lo compila el usuario en Android Studio) | `22193ed` |
