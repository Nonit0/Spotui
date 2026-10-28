# Traceroute de iteraciones

Cada iteración es **un solo cambio atómico** con su propio fichero `IT-XX-*.md`, que funciona
como traza (traceroute) de lo que se pidió, se preguntó, se hizo y salió.

Contexto de la app: [`../CONTEXTO.md`](../CONTEXTO.md). Plantilla: [`PLANTILLA.md`](PLANTILLA.md).

## Reglas (obligatorias)

1. **No salir de esta carpeta** (`Spotui/`). Nada se lee ni escribe fuera del repo.
2. **Preguntar siempre al usuario** antes de tocar nada: se presenta el *qué hacer* y se espera
   un "sí". Si la respuesta es "no" o "cambia X", se actualiza el fichero y se vuelve a preguntar.
3. **Un cambio por iteración, sin subcambios.** Si al hacerla aparece otro cambio necesario,
   **no se hace**: se apunta en *Sugerencias* como nueva iteración candidata y se cierra la actual.
   Esto evita la iteración infinita.
4. **Si el cambio no cabe en una iteración, se parte** en varias antes de empezar, nunca durante.
5. **Criterio de parada**: la iteración termina cuando se obtiene la *Salida esperada* o cuando
   falla una vez. Un fallo no se arregla dentro de la misma iteración: se registra y se propone
   una nueva.
6. Al terminar, se rellena el *Registro* (estado, fecha, resultado real, commit si lo hay).

## Estados

`⏳ Pendiente` · `⏸️ Aplazada` · `❓ Esperando usuario` · `🔧 En curso` · `✅ Hecha` · `❌ Fallida` · `🚫 Descartada`

## Índice

| ID | Título | Tipo | Depende de | Estado |
|---|---|---|---|---|
| [IT-01](IT-01-baseline-build-tests.md) | Comprobar que compila y pasan los tests | Verificación | — | ✅ |
| [IT-02](IT-02-decidir-update-checker.md) | Decidir qué `UpdateChecker` se queda | Decisión | IT-01 | ✅ |
| [IT-09](IT-09-update-checker-releases-latest.md) | Apuntar `data/update` a `Nonit0/Spotui` `releases/latest` | Código | IT-02 + 1ª release en tu fork | ⏸️ aplazada |
| [IT-03](IT-03-migrar-badge-home.md) | Migrar el badge de Home a `data/update` | Código | IT-09 | ⏸️ (espera a IT-09) |
| [IT-10](IT-10-adaptar-update-checker-test.md) | Adaptar `UpdateCheckerTest` a `data/update` | Código | IT-03 | ⏳ |
| [IT-04](IT-04-borrar-update-checker-sobrante.md) | Borrar el `UpdateChecker` que sobra | Código | IT-10 | ⏳ |
| [IT-05](IT-05-renombrar-library-screen.md) | Renombrar `LibraryScreeen.kt` → `LibraryScreen.kt` | Código | IT-01 | ✅ |
| [IT-06](IT-06-borrar-tests-plantilla.md) | Borrar los tests de plantilla | Código | IT-01 | ✅ |
| [IT-07](IT-07-actualizar-readme.md) | Actualizar `README.md` con las funciones reales | Docs | — | ✅ |
| [IT-08](IT-08-analizar-runblocking-potoken.md) | Analizar el `runBlocking` del PoToken (sin cambiar código) | Análisis | — | ✅ |
| [IT-11](IT-11-potoken-suspend.md) | Quitar `runBlocking` de `PoTokenGenerator` (hacerlo `suspend`) | Código | IT-08 | ✅ (falta prueba manual) |

## Backlog de sugerencias (aún no son iteraciones)

> Los IDs no se renumeran (para que la traza sea estable): el orden de ejecución lo marca la columna *Depende de*.

Se convierten en iteración solo si el usuario lo pide, y siempre partidas en pasos atómicos.

- **S1 – Trocear `SongPlayer.kt`** (D2): primer paso posible = mover las funciones puras de
  *matching* (`normalizedForMatch`, `bigramSimilarity`, `versionMarkers`…) a un fichero propio.
- **S2 – Tests del matching** de YouTube, una vez extraído (depende de S1).
- **S3 – Quitar `SpotifyWebPlayer`** (D7): primero preguntar si se piensa recuperar.
- **S4 – Activar `isMinifyEnabled`** (D8): requiere reglas ProGuard; varias iteraciones.
- **S5 – Subir dependencias** (D9): una dependencia por iteración.
- **S9 – Badge de Home apunta al repo original** `Spotui/Spotui` (ver riesgo en IT-09). Si urge antes de la cadena IT-09→IT-04: iteración de 2 constantes en `data/api/UpdateChecker.kt`.
- **S7 – Caché de 6 h / `force`** en `data/update/UpdateChecker` (lo que se pierde al borrar `data/api`).
- **S8 – Ejecutar los tests de `:spotify`** (`./gradlew :spotify:test`), que no se ejecutaron en IT-01.
- **S6 – Cifrar credenciales** (D10) con `EncryptedSharedPreferences` / Keystore, una preferencia por iteración.
