# IT-25 · Borrar los tests de plantilla

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-21 |
| Estado | ✅ Hecha (2026-10-28) |

## 1. Qué hacer
Igual que IT-06 en la fase 1: eliminar los dos tests generados por Android Studio que no prueban nada
(`2 + 2 == 4` y el nombre de paquete):

- `app/src/test/java/com/music/spotui/ExampleUnitTest.kt`
- `app/src/androidTest/java/com/music/spotui/ExampleInstrumentedTest.kt`

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? → "si, haz la IT-25"
- Carpeta `androidTest/`: se mantiene con `.gitkeep`, como decidió el usuario en IT-06 ("Mantén la carpeta y borra los tests").

## 3. Salida esperada
`:app:testDebugUnitTest` sigue en verde con un test menos (44 en total con `:spotify`, antes 45 en IT-21).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `ExampleUnitTest.kt` | Eliminado |
| `ExampleInstrumentedTest.kt` | Eliminado |
| `app/src/androidTest/java/com/music/spotui/.gitkeep` | Nuevo, vacío (mantiene la carpeta) |

## 5. Fuera de alcance
Escribir tests nuevos.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | `git rm` de los 2 tests + `.gitkeep`. Nadie más los referenciaba (solo docs). No se relanzó Gradle: solo se borran clases sueltas que nada usa | `e9e380a` |
