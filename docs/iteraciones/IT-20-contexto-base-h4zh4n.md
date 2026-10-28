# IT-20 · Actualizar `CONTEXTO.md` a la base H4zh4n

| Campo | Valor |
|---|---|
| Tipo | Docs |
| Depende de | IT-18 |
| Estado | ✅ Hecha |

## 1. Qué hacer
Reescribir `docs/CONTEXTO.md`, que describía `Spotui/Spotui` 1.6, para que describa la base
actual `H4zh4n/Spotui` 1.5.0 (rama `base-h4zh4n`): stack y versiones, módulos y paquetes,
arranque, orden de fuentes de audio, credenciales, actualizaciones, deuda técnica (con su
iteración de la fase 2) y cómo se compila ahora.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Hago el commit del índice de la fase 2 y empiezo IT-20?

> Respuesta del usuario: "sí, haz commit y empieza con la IT-20"

## 3. Salida esperada
- `CONTEXTO.md` sin referencias a cosas que no existen en H4zh4n (onboarding de fuente, Soulseek,
  dos `UpdateChecker`, AGP 8.4.2…) y con cada dato comprobado en el código.
- Cada deuda de la §5 enlazada a su iteración de la fase 2 o al backlog.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `docs/CONTEXTO.md` | Reescrito para la base H4zh4n |
| `docs/iteraciones/IT-20-contexto-base-h4zh4n.md` | Este fichero |
| `docs/iteraciones/README.md` | Estado de IT-20 y S12 en el backlog |

Código de la app: ninguno.

## 5. Fuera de alcance
- Corregir ninguna de las deudas que se documentan.
- `README.md` del proyecto → S10.

## 6. Sugerencias (nuevas iteraciones candidatas)
- **S12** · La release se firma con `app/debug.keystore`, que está en git con contraseña
  `android`. Permite actualizar encima del APK de H4zh4n sin desinstalar, pero cualquiera puede
  firmar un APK que se instale encima. Decidir si se pasa a una clave propia fuera de git
  (implica desinstalar una vez).

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-28 | ✅ Hecha | `CONTEXTO.md` reescrito con datos comprobados en el código de `base-h4zh4n`. Hallazgo nuevo: keystore de release en git (→ S12). | `e424135` |
