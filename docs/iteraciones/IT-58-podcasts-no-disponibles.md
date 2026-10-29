# IT-58 · Aviso *podcasts no disponibles* al reproducir un episodio

| Campo | Valor |
|---|---|
| Tipo | Código |
| Depende de | IT-57 (descartada) |
| Estado | ✅ Hecha (2026-10-29) · probada por el usuario |

## 1. Qué hacer
S28, opción B. Sin reproductor web (IT-57: no reproduce), un episodio caía en la búsqueda de YouTube y sonaba
otra cosa. Hacer: en `SongPlayer.playSong`, si es `episode:` y no lo ha cogido el reproductor web, no buscar
nada; mostrar un Toast y el error en la pantalla del reproductor (*"Podcasts not available yet"*, con el mismo
mecanismo que *No stream found*). `prefetch` tampoco resuelve episodios.

Ampliada tras la primera prueba (petición del usuario): el chip *Podcasts* de Inicio ya no muestra la lista de
shows/episodios (parecía una imagen que no responde), sino directamente *Podcasts not available yet*.
Segunda ampliación: al abrir un show (desde *Search* o un enlace) `ShowScreen` muestra flecha atrás, nombre del
show y el mismo aviso, sin pedir nada (sus REST `shows/{id}` y `shows/{id}/episodes` daban 429).
Tercera ampliación: el recuadro *Podcasts* de "Browse all" en *Search* (`CategoryScreen`, género `Podcast`)
muestra el aviso y no carga la categoría. El aviso es un único composable, `PodcastsUnavailableNotice`.

## 2. Preguntar al usuario (antes de empezar)
- [x] ¿Descartar IT-57 y avisar de que los podcasts no están disponibles? → **Sí; la vía de los MP3 directos,
  como sugerencia (S31).**
- [x] Tras probar: → **Que al pulsar el chip salga ya el aviso, no la lista.**
- [x] Tras probar otra vez: → **Al entrar en un podcast desde Search también debe salir el aviso.**
- [x] Tras la 3.ª prueba: → **También el recuadro *Podcasts* de Search (no la barra de búsqueda).**

## 3. Salida esperada
Al pulsar un episodio no suena nada, sale el Toast *Podcasts are not available yet* y el reproductor muestra
*Podcasts not available yet*. En el log no hay `resolveVideoId … 'episode:…'`. Al poner después una canción,
suena y el aviso desaparece.

El chip *Podcasts* muestra solo la cabecera de chips y el aviso.

### Pasos del usuario
1. Compilar debug; Inicio → chip *Podcasts*: sale el aviso, sin lista.
2. *Search* → abrir un podcast: flecha atrás, nombre y aviso; la flecha vuelve.
3. *Search* → pulsar un episodio suelto: Toast y aviso en el reproductor.
4. *Search* (sin escribir) → recuadro *Podcasts*: título, flecha atrás y aviso. Los demás recuadros, igual que antes.
5. Poner una canción normal: suena y el aviso desaparece.

## 4. Cambios
| Fichero | Cambio |
|---|---|
| `app/.../di/SongPlayer.kt` | `playSong`: episodios sin web → aviso y `return`; `prefetch`: ignora episodios |
| `app/.../ui/screens/HomeScreen.kt` | `HomePodcastsFeedContent`: cabecera + aviso en lugar de la lista |
| `app/.../ui/screens/ShowScreen.kt` | Solo flecha atrás + nombre + aviso (sin `ShowViewModel` ni lista de episodios); define `PodcastsUnavailableNotice` |
| `app/.../ui/screens/CategoryScreen.kt` | Género `Podcast` → aviso, sin `viewModel.load` |

## 5. Fuera de alcance
- Reproducir los episodios de verdad (S31).
- Letras de episodios (S32): siguen pidiéndose a lrclib.
- En *Search* siguen saliendo shows y episodios en la lista de resultados (al abrir/reproducir, aviso).
- `ShowViewModel`, `Api.getShowEpisodes`/`getShow` y las preferencias de orden de shows quedan sin usar: se
  dejan para S31.
- `HomeViewModel.fetchPodcasts()` sigue pidiendo los podcasts aunque el chip ya no los muestre (S33).

## 6. Sugerencias (nuevas iteraciones candidatas)
- S33: quitar `fetchPodcasts()` de `HomeViewModel` mientras el chip no use la lista.

## 7. Registro
| Fecha | Estado | Resultado real | Commit |
|---|---|---|---|
| 2026-10-29 | 🔁 Probada (1.ª) | Ningún `resolveVideoId … 'episode:…'` en el log. El usuario pide que el chip avise directamente → ampliada | — |
| 2026-10-29 | 🔁 Probada (2.ª) | Chip OK; sin errores en el log. El usuario pide el aviso también dentro de un show → `ShowScreen` | — |
| 2026-10-29 | 🔁 Probada (3.ª) | Show OK; sin `shows/…` ni crash en el log. El usuario pide el aviso también en el recuadro *Podcasts* de Search → `CategoryScreen` | — |
| 2026-10-29 | ✅ Hecha y probada | Aviso al reproducir un episodio, en el chip *Podcasts*, en un show y en el recuadro *Podcasts* de Search; sin `resolveVideoId … 'episode:…'` ni `shows/…` en el log | `f5b31af` |
