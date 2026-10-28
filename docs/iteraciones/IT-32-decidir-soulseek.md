# IT-32 · Decidir si se trae Soulseek

| Campo | Valor |
|---|---|
| Tipo | Decisión |
| Depende de | IT-31 |
| Estado | 🚫 Cerrada sin traerlo (2026-10-28) → S18 |

## 1. Qué hacer
Decidir, sin tocar código, si el respaldo FLAC por Soulseek de `Spotui/Spotui` 1.6 (`archivo/original-1.6`,
commit `2502236`) se trae a la base H4zh4n. Si se trae, partirlo en iteraciones antes de empezar.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? → "sigue con la IT-32"
- [x] ¿Se trae? → "Creo que lo mejor es no traerlo y añadirlo como sugerencia / mejoras como hemos estado haciendo" (opción A; queda como S18)

## 3. Qué es Soulseek en 1.6
- Cliente propio del protocolo P2P de Soulseek (`server.slsknet.org:2242`), sin librerías: paquete `soulseek/`
  (`SoulseekClient` 582 líneas, `SoulseekSource` 141, `SlskBuffer` 119, `SoulseekModels` 38) +
  `SoulseekPref.kt` (35) + `SoulseekPickTest.kt`. Unas **900 líneas autocontenidas**.
- Crea **una cuenta por instalación** con nombre y contraseña aleatorios (`spotui-xxxxxxxxxx`), guardada en
  `SharedPreferences("Soulseek")`. **Activado por defecto.**
- Abre un puerto de escucha (2234) y se conecta a otros usuarios: **tu IP queda visible** para ellos. No comparte
  ficheros.
- Baja el FLAC **entero** (~30 MB por canción) a una caché; puede tardar ~1 min.

Cómo se engancha al reproductor en 1.6 (`SongPlayer.kt`, 25 referencias):
1. **Caché**: si el FLAC ya está bajado, suena directamente (paso 4 de la cadena de fuentes).
2. **Look-ahead**: mientras suena una canción, baja el FLAC de la **siguiente** de la cola.
3. **Upgrade**: empieza con YouTube y, si llega el FLAC, cambia sin cortes (`quickStartThenUpgrade` / `swapToLossless`).

## 4. Diferencias con la base H4zh4n
- H4zh4n **no tiene** el mecanismo de *upgrade* (`quickStartThenUpgrade`, `swapToLossless`, `losslessTrackInfo`
  no existen). El paso 3 no se puede traer sin portar también esa maquinaria (otra pieza grande de `SongPlayer`).
- H4zh4n **sí tiene** `prefetch()` / `prefetchList()` de las siguientes canciones (`SongPlayer.kt:645-669`):
  el look-ahead de Soulseek podría engancharse ahí.
- Esperar a Soulseek en el momento de darle a play (hasta ~1 min) **no es aceptable**: solo sirve vía caché.

## 5. A favor y en contra
| A favor | En contra |
|---|---|
| En IT-21 (S16) **ningún proveedor lossless respondió**: Soulseek sería hoy la única fuente FLAC | P2P: la IP es visible a otros usuarios; crea una cuenta en un servidor ajeno sin preguntar |
| Código autocontenido, con test propio | Gasta datos (~30 MB/canción en segundo plano) y batería |
| Desactivable desde Ajustes | Zona gris legal (descarga de música de terceros) |
| | Habría que adaptarlo a H4zh4n (sin *upgrade*) y mantener ~900 líneas más |

## 6. Fuera de alcance
Escribir código.

## 7. Opciones
| Opción | Qué supone | Iteraciones aprox. |
|---|---|---|
| **A. No traerlo** | Nada. Se apunta como descartado | 0 |
| **B. Traerlo reducido** | Paquete `soulseek/` + prefs + test; **desactivado por defecto**; credenciales fuera del backup; interruptor en Ajustes; solo caché + bajar la siguiente canción desde `prefetch()` (sin *upgrade*) | ~5 |
| **C. Traerlo completo** | Como B + portar *quick start then upgrade* de 1.6 a `SongPlayer` | ~8, la más arriesgada |
| **D. Aplazar** | Investigar antes S16 (si los proveedores lossless están caídos de verdad o es cosa del test) y decidir después | — |

Recomendación: **D** y luego decidir entre A y B. Si los mirrors lossless funcionan en el móvil, Soulseek aporta
poco para lo que cuesta; si están caídos, B es la forma razonable de tener FLAC.

## 8. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | 🚫 | No se trae. Queda en el backlog como S18 (con la opción B como forma de hacerlo si se retoma) | _(este commit)_ |
