# IT-51 · Primera release en GitHub: `v1.6.0`

| Campo | Valor |
|---|---|
| Tipo | Git / publicación |
| Depende de | IT-47, IT-49, IT-50 |
| Estado | ✅ Hecha (2026-10-29) |

## 1. Qué hacer
Publicar la primera release del fork en `Nonit0/Spotui` con la APK firmada con la clave propia (IT-47), para que
la gente pueda descargarla y para que el comprobador de actualizaciones (`UpdateChecker`, repo cambiado en IT-22)
la encuentre en `releases/latest`.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Tag y push? → **Sí**: tag `v1.6.0` sobre `42be4a4`, push junto a `main` y `update-h4zh4n`.
- [x] ¿Quién publica la release? → **El usuario, desde la web** (no hay `gh` instalado).

## 3. Salida esperada
Release `v1.6.0` marcada como *Latest*, con `Spotui_v1.6.0.apk` adjunta. `UpdateChecker.kt:71-74` coge el primer
asset que termina en `.apk`; si no hay ninguno, abre la página de la release.

## 4. Cambios
| Dónde | Cambio |
|---|---|
| GitHub | `git tag v1.6.0 42be4a4` + push del tag |
| Local (AS) | APK release compilada por el usuario: `app/release/Spotui_v1.6.0.apk` (14:39, después de `42be4a4`), versionCode 202610290, firmada `CN=Nonit0, OU=NoS0urce` |
| GitHub (web) | Release `v1.6.0` publicada por el usuario con la APK adjunta. Primero se lanzó sin APK y se añadió después |

## 5. Fuera de alcance
- Automatizar la release (GitHub Actions que compile y adjunte la APK): necesitaría el keystore como secret.
- "Leave fork network" (ver IT-50): sigue en manos del usuario.

## 6. Sugerencias (nuevas iteraciones candidatas)
- S25: checklist de release (tag → compilar release → comprobar firma/versión → adjuntar `.apk` → marcar *Latest*)
  para no olvidar la APK la próxima vez; o automatizarlo con GitHub Actions.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | API `releases/latest` → `v1.6.0` (15:18 UTC), asset `Spotui_v1.6.0.apk` 90 621 040 B, sha256 `88f35f8f…2747` = APK local | `42be4a4` (tag) |
