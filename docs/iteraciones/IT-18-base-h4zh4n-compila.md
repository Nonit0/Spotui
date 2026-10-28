# IT-18 · Traer H4zh4n a la rama `base-h4zh4n` y comprobar que compila

| Campo | Valor |
|---|---|
| Tipo | Verificación (no modifica código de la app) |
| Depende de | — (abre la fase 2: base H4zh4n) |
| Estado | ✅ Hecha |

## 1. Qué hacer
Hallazgo: `Nonit0/Spotui` se forkeó de `Spotui/Spotui` (1.6, `2502236`), pero la versión que el
usuario usaba es `H4zh4n/Spotui` (1.5.0, `9180164`). Comparativa completa:
https://claude.ai/artifact/H5trPHosnovMSTaTe4Mge3

Se elige la **opción A**: se conserva todo lo anterior y se cambia de base.
1. Guardar el `main` actual (`8174507`, IT-01…IT-14) en la rama `archivo/original-1.6`.
2. Crear la rama `base-h4zh4n` desde `H4zh4n/Spotui@9180164` sin tocar `main`.
3. Copiar `docs/` (contexto e iteraciones) a la rama nueva.
4. Compilar (`assembleDebug`) con el entorno de esta máquina, instalando el SDK 37 si hace falta.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Hago la rama `base-h4zh4n` sin tocar `main`, instalo el SDK 37 y compruebo que compila?
- [x] ¿Opción A (archivar el repo e iteraciones antiguas) u opción B (fork nuevo)?

> Respuesta del usuario: "traer H4zh4n a una rama nueva base-h4zh4n sin tocar main, instalar el
> SDK 37 y comprobar que compila (100% compila pues la apk funciona en movil). Y guardamos el repo
> e iteraciones antiguas, es decir, A. Y además plantearemos las mismas iteraciones pero adaptadas
> a esta nueva plantilla!"

## 3. Salida esperada
- `git branch` muestra `archivo/original-1.6` (en `8174507`) y `base-h4zh4n` (en `9180164` + docs).
- `assembleDebug` → `BUILD SUCCESSFUL`, APK en `app/build/outputs/apk/debug/`.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `docs/` | Copiado desde `main` + este fichero + índice actualizado |

Código de la app: ninguno.

## 5. Fuera de alcance
- Sustituir `main` por `base-h4zh4n` → se hará cuando el usuario haya probado la base en el móvil.
- Tests unitarios (`ProviderStreamTest` usa red) → iteración propia.
- Actualizar `CONTEXTO.md` a la nueva base → iteración propia.
- Rehacer las iteraciones antiguas → se plantean en el índice como iteraciones nuevas.
- Subida del AGP 9.2.1 → 9.4.1 que hizo Android Studio → iteración propia.

## 6. Sugerencias (nuevas iteraciones candidatas)
Ver el índice (`README.md`, fase 2).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | Ramas `archivo/original-1.6` (`8174507`) y `base-h4zh4n` (`9180164` + `docs/`) creadas. SDK 37 instalado. El usuario compiló en Android Studio: Gradle OK y la APK (`app/build/outputs/apk/debug/Spotui_v1.5.0.apk`) funciona. Ojo: Android Studio subió el AGP 9.2.1 → 9.4.1 en `build.gradle.kts`; ese cambio **no** entra en este commit (queda como iteración candidata). La compilación desde terminal se descartó porque saturó el PC. | ver commit de docs |
