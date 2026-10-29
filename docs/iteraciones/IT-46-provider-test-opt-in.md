# IT-46 · ProviderStreamTest solo bajo demanda (S11)

| Campo | Valor |
|---|---|
| Tipo | Test + build |
| Depende de | IT-21 |
| Estado | ✅ Hecha (2026-10-29) · pendiente de prueba manual |

## 1. Qué hacer
S11 (hallazgo de IT-21): `ProviderStreamTest` llama a la red, tarda ~2 min y corría con cada
`testDebugUnitTest`. Hacer que por defecto no se ejecute, pero que siga siendo útil como diagnóstico.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Cómo excluirlo? Opciones: flag opt-in, `@Ignore`, exclude en Gradle → **Flag opt-in** (no se rompe y
  sigue teniendo utilidad).

## 3. Salida esperada
- `testDebugUnitTest` normal: `ProviderStreamTest` sale como **skipped** en ~0 s; el resto, igual.
- Con `-PproviderTests=true`: se ejecuta como antes.
  `.\gradlew.bat testDebugUnitTest --tests "com.music.spotui.providers.ProviderStreamTest" -PproviderTests=true`
  (en Android Studio: añadir `-PproviderTests=true` a los argumentos de la configuración de ejecución).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/build.gradle.kts` | `unitTests.all { systemProperty("providerTests", …) }`, `"false"` si no se pasa la propiedad |
| `app/src/test/.../providers/ProviderStreamTest.kt` | `@Before` con `Assume.assumeTrue(providerTests == "true")`; KDoc con el comando nuevo |

## 5. Fuera de alcance
- El test sigue sin asserts (siempre pasa cuando se ejecuta): es un informe por consola. Lo que resuelve cada
  proveedor sigue en S16.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No ejecutado aquí (lo ejecuta el usuario) | `804d93a` |
