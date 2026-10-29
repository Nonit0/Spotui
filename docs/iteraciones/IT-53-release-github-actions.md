# IT-53 · Release automática con GitHub Actions (S25)

| Campo | Valor |
|---|---|
| Tipo | Build / CI |
| Depende de | IT-47, IT-51, IT-52 |
| Estado | ✅ Hecha (2026-10-29) · probada por el usuario |

## 1. Qué hacer
S25: la release `v1.6.0` (IT-51) salió primero sin APK. Que al subir un tag `v*` GitHub compile, firme y cree
la release con la APK adjunta, y documentar los pasos.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Checklist manual, GitHub Actions o checklist + script? → **GitHub Actions** (la clave pasa a estar
  también en los secrets del repo).
- [x] ¿Dónde va el checklist? → **`docs/RELEASE.md`**.

## 3. Salida esperada
- Tag `v*` → borrador de release `Spotui vX` con `Spotui_vX.apk` y notas generadas; el usuario la publica.
- *Run workflow* manual → misma compilación, APK como *artifact*, sin release (para probar los secrets).
- Falla si: el tag ≠ `v` + `versionName`, falta alguno de los 4 secrets (no cae a la clave debug), o la APK no
  está firmada con el certificado `CN=Nonit0` (SHA-256 `a20dca38…faa6`, fijado en el workflow).

### Pasos del usuario
1. Crear los 4 secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`): ver `docs/RELEASE.md`.
2. Actions → *Release* → **Run workflow** en `main` y descargar el *artifact*: debe instalarse encima de la 1.6.0.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `.github/workflows/release.yml` | Nuevo: JDK 17, comprueba versión, genera `keystore.properties` desde secrets, `:app:assembleRelease`, verifica firma, sube *artifact* o crea borrador con `gh release create` |
| `docs/RELEASE.md` | Nuevo: secrets, pasos de cada release y plan B manual |
| `docs/CONTEXTO.md` | §6 enlaza `RELEASE.md` |

## 5. Fuera de alcance
- Publicar directamente (sin borrador): basta quitar `--draft` si un día se quiere.
- Notas desde `CHANGELOG.md`: de momento `--generate-notes`.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | YAML validado en local; no ejecutado (necesita los secrets y el push) | `98ea81b` |
| 2026-10-29 | ✅ Run manual OK | Ejecución `40911909641` en verde, *artifact* `Spotui_v1.6.0` (zip). Avisos Node 20 → `checkout@v7`, `setup-java@v6`, `upload-artifact@v7` con `archive: false` (APK sin zip) | `bf82bdc` |
| 2026-10-29 | ✅ Probada | APK del *artifact* instalada encima de la 1.6.0: actualiza sin perder datos | — |
