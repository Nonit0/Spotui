# IT-26 · Quitar `runBlocking` de `PoTokenGenerator`

| Campo | Valor |
|---|---|
| Tipo | Análisis (iba a ser Código) |
| Depende de | — |
| Estado | 🚫 Cerrada sin cambios (2026-10-28) · riesgo pendiente → S13 |

## 1. Qué hacer
Hacer `suspend` `PoTokenGenerator.getWebClientPoToken` y quitar su `runBlocking`, como IT-11 en la fase 1.
Antes, revisar las llamadas, que podían haber cambiado respecto a IT-08.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? → "sigue con la IT-26"
- [x] Tras el análisis: ¿cerrarla sin cambios o hacer el cambio completo (2 ficheros)?

> Respuesta del usuario: "la 1, ciérrala [...] pero guarda en fisico (no en tu memoria) el posible bloqueo de hilo, como problema a tratar a futuro"

## 3. Salida esperada
Análisis de las llamadas y decisión del usuario.

## 4. Cambios
Ninguno en código.

## 5. Análisis

### Qué hay en la base H4zh4n
- `utils/potoken/PoTokenGenerator.kt:30-44`: `fun getWebClientPoToken(videoId, sessionId)` sigue usando
  `runBlocking { withTimeout(POTOKEN_TIMEOUT_MS = 5_000) { ... } }`. A diferencia de la fase 1, **ya tiene tiempo límite propio**.
- Único llamador: `utils/YTPlayerUtils.kt:126-136`, dentro de `playerResponseForPlayback` (que es `suspend`). No lo llama
  directamente: lo lanza con `CompletableFuture.supplyAsync { ... }` (pool común `ForkJoinPool`), en paralelo con
  `getSignatureTimestampOrNull`, y lo espera con `potFuture.get(POT_FUTURE_TIMEOUT_SEC = 5, SECONDS)` (l.150).
- `playerResponseForPlayback` se llama desde `di/SongPlayer.kt:2040` y `ui/viewmodel/AlternativeSearchViewModel.kt:105`.

### Por qué no se hace ahora
- El `runBlocking` ya no corre en el hilo principal ni puede quedarse colgado sin límite: está acotado por dos tiempos de 5 s.
- Hacerlo `suspend` obliga a cambiar `YTPlayerUtils` (un `suspend` no se puede llamar dentro de `supplyAsync`):
  pasar el futuro a `async` + `withTimeoutOrNull`. Son 2 ficheros en la resolución de URLs de YouTube, la parte más delicada
  de la reproducción, para un beneficio pequeño.

### Riesgo que queda (→ S13)
1. `runBlocking` ocupa un hilo del `ForkJoinPool` común (tiene unos *núcleos − 1* hilos) hasta 5 s por canción. Si se resuelven
   varias canciones a la vez (precarga, cola), puede dejar sin hilos a otras tareas que usen ese pool.
2. `potFuture.get(...)` y `sigFuture.get(...)` bloquean el hilo de la corrutina que llama (hasta 5 s cada uno) en vez de suspender.
3. Si se cancela la corrutina (salto de canción), la cancelación no llega al futuro: la generación del token sigue hasta acabar o agotar los 5 s.

## 6. Sugerencias
- **S13** (en el backlog del índice): pasar la generación de PoToken y de la firma a corrutinas (`suspend` + `async` +
  `withTimeoutOrNull`) para no bloquear hilos y que la cancelación funcione. Requiere probar bien la reproducción en el móvil.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | 🚫 Cerrada sin cambios | Análisis hecho; el usuario elige no tocar código. Riesgo apuntado como S13 | (solo docs) |
