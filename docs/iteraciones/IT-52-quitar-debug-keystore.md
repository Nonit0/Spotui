# IT-52 · Dejar de versionar `app/debug.keystore` (S22)

| Campo | Valor |
|---|---|
| Tipo | Build |
| Depende de | IT-47 |
| Estado | ✅ Hecha (2026-10-29) · pendiente de prueba manual |

## 1. Qué hacer
S22: `app/debug.keystore` (clave de H4zh4n, alias `hazhan`, contraseña `android`) seguía en git y firmaba
debug y el *fallback* de release (`sharedDebug`). Sacarla del repo y usar la clave debug que Android crea en cada
equipo (`~/.android/debug.keystore`).

## 2. Preguntar al usuario (antes de empezar)
- [x] Sin `keystore.properties`, ¿cómo se firma la release? → **Clave debug local** + *warning* (un clon nuevo
  sigue generando una release instalable).
- [x] La app debug (`com.music.spotui.debug`) cambia de firma: hay que desinstalarla una vez → **Vale.**

## 3. Salida esperada
- Debug: firmada con la clave debug local (configuración `debug` por defecto de AGP).
- Release con `keystore.properties`: sin cambios (clave propia).
- Release sin `keystore.properties`: clave debug local y el aviso
  `keystore.properties not found: release is signed with the local debug key`.

### Pasos del usuario
1. Sync de Gradle en AS y compilar debug.
2. Si la debug está instalada en el móvil: desinstalarla (falla con `INSTALL_FAILED_UPDATE_INCOMPATIBLE`).
3. Opcional: compilar release y comprobar con `apksigner verify --print-certs` que sigue saliendo `CN=Nonit0`.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/build.gradle.kts` | Fuera `signingConfigs.sharedDebug`; `debug` usa la firma por defecto; *fallback* de release → `signingConfigs.getByName("debug")` |
| `app/debug.keystore` | Borrado (`git rm`) |
| `docs/CONTEXTO.md` | D9 actualizada |

## 5. Fuera de alcance
- Reescribir el historial para borrar la clave antigua: ya no firma nada nuestro (la release usa la clave
  propia desde IT-47), así que no compensa.

## 6. Sugerencias (nuevas iteraciones candidatas)
-

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | ✅ Hecha | No compilado aquí; `~/.android/debug.keystore` existe en el equipo del usuario | `04846bd` |
