# IT-21 · Pasar los tests unitarios en Android Studio

| Campo | Valor |
|---|---|
| Tipo | Verificación |
| Depende de | IT-18 |
| Estado | ✅ Hecha (2026-10-28) |

## 1. Qué hacer
Lanzar los tests unitarios de la base H4zh4n y apuntar qué pasa y qué falla. Sin tocar código.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hacemos? → "vamos a los test unitarios de la IT-21"
- [x] ¿Quién los lanza? → "quiero que lances tu los test pero quiero que sea muy poco a poco y si es posible de uno en uno"
- [x] ¿También `ProviderStreamTest` (usa red)? → "vamos por el ultimo test tambien"

Cómo se lanzaron (una clase por llamada, con topes duros para no colgar el PC):

```
systemd-run --user --scope -p MemoryMax=3584M -p MemorySwapMax=0 -p CPUQuota=200% \
  env JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ANDROID_HOME=$HOME/Android/Sdk \
  nice -n 19 ionice -c3 ./gradlew --no-daemon --offline --max-workers=1 \
  -Dorg.gradle.jvmargs="-Xmx2g -Dfile.encoding=UTF-8" -Pkotlin.compiler.execution.strategy=in-process \
  :app:testDebugUnitTest --tests "<clase>"
```
(`:spotify` con `-Xmx1536m` y `MemoryMax=3G`). La RAM libre se mantuvo en ~8 GB.

## 3. Salida esperada
Tests que existen (`:innertube` no tiene ninguno):

| Módulo | Fichero | Nota |
|---|---|---|
| `:spotify` (JVM) | `SpotifyMapperMatchScoreTest.kt` | Debería pasar |
| `:spotify` (JVM) | `SpotifyMapperPerformanceTest.kt` | Mide tiempos: puede ser lento o frágil |
| `:app` | `ExampleUnitTest.kt` | Plantilla (se borra en IT-25) |
| `:app` | `util/SpotifyDeepLinkTest.kt` | Debería pasar |
| `:app` | `di/TrackMatcherTest.kt` | 10 tests de IT-28, valores calculados a mano |
| `:app` | `providers/ProviderStreamTest.kt` | Usa red: se espera que falle o tarde (S11) |

Orden recomendado, de más ligero a más pesado (botón ▶ sobre cada fichero en Android Studio, o terminal):

1. `./gradlew :spotify:test`
2. `./gradlew :app:testDebugUnitTest --tests "com.music.spotui.di.TrackMatcherTest"`
3. `./gradlew :app:testDebugUnitTest --tests "com.music.spotui.util.SpotifyDeepLinkTest"`
4. `./gradlew :app:testDebugUnitTest --tests "com.music.spotui.ExampleUnitTest"`
5. (Opcional) `./gradlew :app:testDebugUnitTest --tests "com.music.spotui.providers.ProviderStreamTest"`

Si alguno falla, **no se arregla aquí**: se apunta y se abre una iteración nueva.

## 4. Cambios
Ninguno (solo este documento y el índice).

## 5. Fuera de alcance
- Arreglar tests que fallen.
- Separar `ProviderStreamTest` (S11).

## 6. Sugerencias (nuevas iteraciones candidatas)
- S11 (ya existía): `ProviderStreamTest` tarda ~2 min y no tiene asserts, siempre pasa.
- S16: en `ProviderStreamTest` solo YouTube Music resuelve; el resto de proveedores falla (ver Registro).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ | `SpotifyMapperMatchScoreTest` 7/7 · `SpotifyMapperPerformanceTest` 12/12 · `TrackMatcherTest` 10/10 (verifica IT-28) · `SpotifyDeepLinkTest` 14/14 · `ExampleUnitTest` 1/1 · `ProviderStreamTest` 1/1 (2 min 13 s). **45/45 en verde**, 14-19 s por clase salvo el de red | _(este commit)_ |

Resultado de `ProviderStreamTest` (10 canciones):

| Proveedor | Resultado | Nota |
|---|---|---|
| YouTube Music | 10/10 ✅ | HTTP 206, audio/webm ~145 kbps |
| Amazon | 0/10 | `toString(...) must not be null` (falla en 1-40 ms) |
| Qobuz | 0/10 | Timeout (6 s) |
| TIDAL | 0/10 | match not found |
| Deezer Mirror | 0/10 | match not found |
| Deezer Direct | 0/10 | Not logged in (No ARL), esperado |
| SpotiFLAC | 0/10 | No match |
| SoundCloud | 0/10 | match not found |
