# IT-55 · Traducción y detección de idioma rotas por R8

| Campo | Valor |
|---|---|
| Tipo | Build (regresión de IT-54) |
| Depende de | IT-54 |
| Estado | ✅ Hecha (2026-10-29) · pendiente de prueba manual |

## 1. Qué hacer
Con la release minificada (IT-54), traducir letras falla con *"Attempt to read from field
'…com.google.mlkit…' on a null object reference in method …SongPlayer$prefetch$1…"*.

Causa (vista en el `.dex`): ML Kit crea sus componentes por reflexión (`MlKitContext.get(TranslatorImpl.Factory)`);
R8 los quitó o los fusionó con otras clases (*horizontal class merging*: p. ej. el receptor HDMI de ExoPlayer
acabó como `com.google.mlkit.nl.translate.internal.zzf`), así que `Translation.getClient()` recibe un *factory*
`null`. El método que sale en el error es `SongPlayer$prefetch$1` porque R8 fusionó en esa clase la corrutina
de la traducción. `detectLanguage()` (`LyricsViewModel.kt:88-90`) usa el mismo mecanismo: fallaba en silencio y
devolvía `"en"`, por eso no parecía detectar el idioma.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Compilar la release en terminal con los límites de IT-54? → **Sí.**

## 3. Salida esperada
La traducción funciona en la release y el idioma de origen se detecta solo al cargar la letra.

### Pasos del usuario
Instalar la APK nueva encima y traducir la letra de una canción en otro idioma (comprobar también que el idioma
de origen sale bien sin elegirlo).

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/proguard-rules.pro` | `-keep class com.google.mlkit.** { *; }` y `-keep class com.google.android.gms.internal.mlkit_** { *; }` |

## 5. Fuera de alcance
- Podcasts vacíos al pulsar el chip: otra iteración (sospecha: REST de Spotify → 429, como en IT-39).

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | Compilada en terminal (3 min 57 s). APK 73,3 MB (+80 KB), firma `CN=Nonit0`; `TranslatorImpl$Factory` presente y ya no hay clases ajenas fusionadas en las de ML Kit | `25197de` |
