# IT-29 · Sacar las credenciales del backup de Google

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | — |
| Estado | ✅ Hecha (2026-10-28) · pendiente de compilar y de prueba manual |

## 1. Qué hacer
`AndroidManifest.xml` tiene `allowBackup="true"` y ninguna regla real: `backup_rules.xml` (Android ≤ 11) y
`data_extraction_rules.xml` (Android 12+) solo tenían comentarios de plantilla, así que Google copiaba **todas** las
SharedPreferences, incluidas las credenciales. Excluir en los dos XML los 3 ficheros con credenciales:

| Fichero de prefs | Qué guarda | Dónde se define |
|---|---|---|
| `spotify_session.xml` | Cookie `sp_dc` de Spotify | `data/api/SpotifySession.kt:12` |
| `Deezer.xml` | ARL de Deezer | `data/preferences/DeezerPref.kt:9` |
| `SpotiflacSession.xml` | Sesión de SpotiFLAC | `data/preferences/SpotiflacSessionPref.kt:11` |

En `data_extraction_rules.xml` se excluyen tanto en `cloud-backup` como en `device-transfer` (igual que IT-16).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿La hago? (al restaurar en otro móvil habrá que volver a iniciar sesión) → "Si, [...] haz el 29"

## 3. Salida esperada
- Los dos XML son válidos (comprobado con un parser) y solo usan `<exclude>`, así que el resto se sigue copiando.
- Compila en Android Studio (lo comprueba el usuario).
- Prueba opcional (Android 12+, móvil o emulador por USB):
  `adb shell bmgr backupnow com.music.spotui` y luego desinstalar, reinstalar y `adb shell bmgr restore …`:
  los ajustes vuelven y hay que iniciar sesión otra vez.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `res/xml/backup_rules.xml` | Comentario de plantilla → 3 `<exclude>` |
| `res/xml/data_extraction_rules.xml` | Comentario de plantilla → 3 `<exclude>` en `cloud-backup` y 3 en `device-transfer` |

## 5. Fuera de alcance
- La cookie de YouTube (`youtube_cookie` vive en `settings_prefs`, que sí debe copiarse) → IT-30.
- Cifrar credenciales (descartado en la fase 1, S6).

## 6. Sugerencias
- La plantilla de `backup_rules.xml` decía "This file is ignored for devices older that API 31", que es al revés
  (se ignora en API 31+). Se ha corregido el comentario al reescribir el fichero.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | XML válidos. Diff: 2 ficheros, solo reglas `<exclude>`. Sin compilar (lo hace el usuario en Android Studio) | `08b7851` |

**Efecto:** al restaurar un backup de Google o pasar la app a un móvil nuevo, se recuperan ajustes, me gusta, listas, etc.,
pero **no** las sesiones de Spotify, Deezer y SpotiFLAC: hay que volver a iniciar sesión.
