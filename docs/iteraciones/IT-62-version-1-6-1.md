# IT-62 · Versión 1.6.1 y release

| Campo | Valor |
|---|---|
| Tipo | Código / Release |
| Depende de | IT-60, IT-61 |
| Estado | 🚀 En curso |

## 1. Qué hacer
Subir la versión a `1.6.1` (`versionCode` `202611260`) y sacar la release siguiendo `docs/RELEASE.md`
(tag `v1.6.1` → Actions crea un borrador con la APK firmada).

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Confirmas que hago exactamente esto?

> Respuesta del usuario (2026-11-26): "si ponla 1.6.1".

## 3. Salida esperada
Workflow *Release* en verde, borrador `Spotui v1.6.1` con `Spotui_v1.6.1.apk` firmada con `CN=Nonit0`.

## 4. Cambios

| Fichero | Cambio |
|---|---|
| `app/build.gradle.kts` | `versionName` 1.6.0 → 1.6.1, `versionCode` 202610290 → 202611260 |

Incluye desde 1.6.0: IT-52…IT-61 (R8 + arreglo ML Kit, podcasts desde searchDesktop y aviso de no disponibles,
cooldown de los 429, *Liked Songs* en Inicio, menú «Guardado en» al dar like).

## 5. Fuera de alcance
- `CHANGELOG.md` está desfasado (llega a 1.4.5): no se toca.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-11-26 | 🚀 Borrador listo | Workflow 45903412268 en verde (4 min 58 s); borrador `Spotui v1.6.1` con `Spotui_v1.6.1.apk` (73 MB), firma `CN=Nonit0` (SHA-256 a20dca38…faa6), versionCode 202611260 comprobados. Falta publicar como *Latest* | `f0c6971` |
