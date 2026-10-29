# IT-50 · `update-h4zh4n` como nuevo `main` en GitHub

| Campo | Valor |
|---|---|
| Tipo | Git / publicación |
| Depende de | IT-18, IT-49 |
| Estado | ✅ Hecha (2026-10-29) |

## 1. Qué hacer
Todo el trabajo (IT-18…IT-49) estaba solo en local, en `base-h4zh4n`; `main` en GitHub seguía siendo el fork
antiguo de la 1.6 (`e8d85d9`). H4zh4n está abandonado: hacer que lo que ve la gente (`main`) sea este trabajo.

## 2. Preguntar al usuario (antes de empezar)
- [x] Nombre de la rama → **`update-h4zh4n`** (renombrada desde `base-h4zh4n`).
- [x] ¿Cómo llevarla a `main`? → **Sustituir `main`** (force-push), con copia del `main` antiguo antes.
- [x] ¿Lanzar los pushes? → **Sí.**

## 3. Salida esperada
En GitHub: `main` = `update-h4zh4n` = `f45de29`; `archivo/original-1.6` (`8174507`) como copia del fork antiguo.

## 4. Cambios
| Dónde | Cambio |
|---|---|
| Local | `git branch -m base-h4zh4n update-h4zh4n`; `origin` → `git@github.com:Nonit0/Spotui.git` (SSH) |
| GitHub | `push archivo/original-1.6`; `push --force-with-lease=main:e8d85d9 origin main` (`e8d85d9...f45de29`); `push -u origin update-h4zh4n` |

## 5. Fuera de alcance
- Separar el repo de la red de forks (es fork de `Spotui/Spotui`): solo desde la web, Settings → General →
  Danger Zone → "Leave fork network". Irreversible; lo decide el usuario.
- Borrar del GitHub `e8d85d9`/historial antiguo: sigue accesible en `archivo/original-1.6`.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | `ls-remote`: `main`, `update-h4zh4n` → `f45de29`; `archivo/original-1.6` → `8174507` | `f45de29` |
