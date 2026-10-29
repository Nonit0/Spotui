# IT-59 · Respetar el `Retry-After` de los 429 del REST

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-29) · probada por el usuario |

## 1. Qué hacer
S29. `Spotify.authenticatedGet` lee el `Retry-After` de un 429 pero no lo guarda: la siguiente llamada REST
vuelve a salir y se come otro 429. En los logs de IT-56/58, cada vuelta a Inicio repetía `me/top/tracks` y
`me/top/artists` con `retryAfter` de 10–57 s, y al abrir un show `shows/{id}` también daba 429. Spotify limita
por app (ventana común a todos los endpoints) y cada petición durante el límite puede alargarlo.

Hacer: un *cooldown* común para todo el REST (`RateLimitCooldown`). Un 429 lo arranca (o lo alarga) con su
`Retry-After`; mientras dure, `authenticatedGet` no llama a la red: si quedan más de 3 s (o la llamada es
`failFastOn429`), lanza el mismo `SpotifyException(429, "Rate limited")` que antes; si quedan ≤ 3 s, espera y
sigue (como ya hacía con un 429 corto). El GQL no se toca.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Push y luego S29? → **Sí** (push hecho: `main` = `update-h4zh4n` = `c2f03e2`). Sin más adb.

## 3. Salida esperada
- `:spotify:test` pasa, con `RateLimitCooldownTest` (5 casos).
- En la app: tras un 429, volver a Inicio no lanza `REST GET me/top/…` hasta que pasa el `retryAfter`
  (en el log sale `REST me/top/tracks skipped: rate-limit cooldown, Ns left`). La pantalla se comporta igual
  que con un 429 (mismo error).

### Pasos del usuario
Compilar debug y usar la app como siempre (Inicio, cambiar de pestaña y volver): nada debe ir peor que antes.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `spotify/.../RateLimitCooldown.kt` | Nuevo: guarda hasta cuándo dura el límite (reloj inyectable) |
| `spotify/.../Spotify.kt` | `authenticatedGet`: comprueba el cooldown antes de cada intento; un 429 lo arranca |
| `spotify/src/test/.../RateLimitCooldownTest.kt` | Nuevo: 5 tests |

## 5. Fuera de alcance
- Las pantallas siguen pidiendo `me/top/*` cada vez (ahora sin red durante el límite); cachear o pedir menos
  veces es otra cosa (S30: Inicio carga dos veces).
- El GQL tiene su propio manejo de 429 (`graphqlPost`), sin cambios.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S35: las peticiones REST que salen a la vez antes de que vuelva el primer 429 no las frena el cooldown (4 en 140 ms al arrancar); se reduce con S30 (Inicio carga dos veces).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Test | `:spotify:test --tests …RateLimitCooldownTest` con límites (23 s): 5/5 OK; `:spotify` compila | — |
| 2026-10-29 | ✅ Hecha | Cooldown común en `authenticatedGet` | `c401862` |
| 2026-10-29 | ✅ Probada | Funciona bien. Logcat del usuario (filtro 429): 4 × `me/top/*` → 429 en 140 ms a las 20:16:11 (salieron a la vez, antes de la primera respuesta), otro a las 20:16:19 al caducar el `retryAfter=7s` (→ 59 s) y ninguno más: el resto se saltó sin red | — |
