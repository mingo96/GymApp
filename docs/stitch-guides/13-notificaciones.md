# 13 — Notificaciones (Notifications Sheet)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `820cc7332e9c4b349185dec6234cc6c2` |
| Nombre | Notifications Sheet |
| Tipo | BottomSheet (modal) |
| Ancho diseño | 780 px (móvil) |
| Navegación | BottomNav visible (Perfil activo) |

---

## Jerarquía Visual

```
BottomSheet  bg-[#1A1A24]  rounded-t-32  border-t white/5
├── DragHandle  w-12 h-1.5  surfaceVariant/50  rounded-full
├── Content  px-6  py-4
│   ├── Header  mb-8
│   │   ├── Row
│   │   │   ├── BackButton  40dp  rounded-full  bg-white/5
│   │   │   │   └── Icon "arrow_back"  onSurface
│   │   │   └── Title "Notificaciones"  2xl  bold  tracking-tight
│   │   └── MarkAllButton  "MARCAR TODAS COMO LEÍDAS"
│   │       └── primary  xs  extrabold  uppercase  tracking-widest
│   ├── FilterChips  horizontal-scroll  gap-2  pb-6
│   │   ├── Chip:Active "Todas"  bg-primary  onPrimary  shadow(primary/20)
│   │   ├── Chip:Inactive "No leídas (3)"  bg-surfaceContainerHigh  onSurfaceVariant  border white/5
│   │   └── Chip:Inactive "Leídas"  bg-surfaceContainerHigh  onSurfaceVariant  border white/5
│   ├── NotificationList  space-y-4  pb-12
│   │   ├── Notification:Unread (Entrenador)
│   │   │   ├── Container  bg-primaryContainer/10  border-l-4 primary  rounded-2xl  p-4
│   │   │   ├── IconBox  48dp  rounded-xl  bg-primaryContainer
│   │   │   │   └── Icon "notifications" FILL=1  onPrimaryContainer
│   │   │   ├── Title  onPrimaryContainer  bold  base
│   │   │   ├── Time "hace 2 horas"  primary  10px  bold  uppercase
│   │   │   └── Body  onSurfaceVariant/80  sm  truncate
│   │   ├── Notification:Unread (PR)
│   │   │   ├── Container  bg-surfaceContainerLow  border-l-4 tertiary  rounded-2xl  p-4
│   │   │   ├── IconBox  48dp  rounded-xl  bg-tertiary/20  border tertiary/20
│   │   │   │   └── Icon "emoji_events" FILL=1  tertiary
│   │   │   ├── Title  onSurface  bold  base
│   │   │   ├── Time "Ayer"  onSurfaceVariant/50  10px  bold  uppercase
│   │   │   └── PRValue "100 kg"  tertiary  sm  font-black
│   │   └── Notification:Read (Swipe-to-delete)
│   │       ├── Container  bg-surfaceContainerLow  rounded-2xl  p-4  opacity-60  translate-x[-40px]
│   │       ├── IconBox  48dp  rounded-xl  bg-surfaceVariant
│   │       │   └── Icon "person" FILL=1  outline
│   │       ├── Title  onSurface  bold  base
│   │       ├── Time "hace 3 días"  onSurfaceVariant/50  10px  bold  uppercase
│   │       └── DeleteAction  60dp  bg-errorContainer/40  rounded-r-2xl
│   │           └── Icon "delete"  error
│   └── MotivationBanner  rounded-3xl  140dp  overflow-hidden
│       ├── Image  grayscale  object-cover  (hover: color, 700ms)
│       ├── Gradient overlay  from-[#1A1A24]  via-transparent  to-transparent
│       └── Content  bottom-4  left-6
│           ├── Label "MOTIVACIÓN DEL DÍA"  primary  xs  font-black  uppercase  tracking-[0.2em]
│           └── Quote  xl  bold  headline  italic
└── BottomNav (standard)
```

---

## Componentes Detallados

### 1. Filter Chips

| Propiedad | Activo | Inactivo |
|-----------|--------|----------|
| Padding | `px-5 py-2.5` | `px-5 py-2.5` |
| Redondeo | `rounded-full` | `rounded-full` |
| Fondo | `primary` (#BAC3FF) | `surfaceContainerHigh` (#2A292F) |
| Texto | `onPrimary` (#00218D), `bold`, `sm` | `onSurfaceVariant` (#C4C5D7), `bold`, `sm` |
| Sombra | `shadow-lg shadow-primary/20` | ninguna |
| Borde | ninguno | `white/5` |
| Scroll | horizontal con `overflow-x-auto`, `no-scrollbar` |

### 2. Notification Cards

**Variante: No leída (tipo entrenador/primary)**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `primaryContainer/10` (azul muy sutil) |
| Borde izquierdo | `4px solid primary` (#BAC3FF) |
| Redondeo | `rounded-2xl` (16 dp) |
| Padding | `p-4` (16 dp) |
| IconBox | 48×48 dp, `rounded-xl`, `bg-primaryContainer` (#4361EE) |
| Icono | FILL=1, `onPrimaryContainer` (#F4F2FF) |
| Título | `onPrimaryContainer` (#F4F2FF), `bold`, `base` (~16sp) |
| Timestamp | `primary`, `10px`, `bold`, `uppercase`, `tracking-tighter` |
| Body | `onSurfaceVariant/80`, `sm`, `truncate` (1 línea) |

**Variante: No leída (tipo PR/tertiary)**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerLow` (#1B1B20) |
| Borde izquierdo | `4px solid tertiary` (#27E0A9) |
| IconBox | 48×48 dp, `bg-tertiary/20`, `border tertiary/20` |
| Icono | `emoji_events` FILL=1, `tertiary` |
| PR Value | `tertiary`, `sm`, `font-black` (900) |

**Variante: Leída (con swipe-to-delete)**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerLow` (#1B1B20) |
| Opacidad | `0.6` |
| Desplazamiento | `translate-x(-40px)` (swipe parcial visible) |
| IconBox | 48×48 dp, `bg-surfaceVariant` (#35343A) |
| Icono | FILL=1, `outline` (#8E8FA1) |
| Delete zone | 60 dp ancho, `bg-errorContainer/40`, `rounded-r-2xl` |
| Delete icon | `delete`, `error` (#FFB4AB) |

### 3. Motivation Banner

| Propiedad | Valor |
|-----------|-------|
| Alto | 140 dp |
| Redondeo | `rounded-3xl` (24 dp) |
| Imagen | `grayscale`, `object-cover`, hover→color en 700ms |
| Overlay | gradient `from-[#1A1A24] via-transparent to-transparent` (bottom-up) |
| Label | "MOTIVACIÓN DEL DÍA", `primary`, `xs`, `font-black`, `uppercase`, `tracking-[0.2em]` |
| Quote | `xl` (~20sp), `bold`, `Space Grotesk`, `italic` |

### 4. "Marcar todas como leídas"

| Propiedad | Valor |
|-----------|-------|
| Color | `primary` (#BAC3FF) |
| Tamaño | `xs` |
| Peso | `extrabold` (800) |
| Estilo | `uppercase`, `tracking-widest` |
| Hover | `primary-fixed` |
| Press | `scale(0.95)` |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Tap filter chip | Filtra lista (todas / no leídas / leídas) |
| Tap notificación no leída | Navega al contenido relacionado, marca como leída |
| Swipe-left en leída | Revela botón delete con `errorContainer/40` |
| Tap delete | Elimina notificación (con confirmación o undo) |
| Tap "Marcar todas" | Todas las notificaciones pasan a leídas |
| Tap back | Cierra sheet |
| Hover imagen motivación | Transición grayscale → color (700ms) |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Notificaciones | No hay pantalla dedicada | BottomSheet con filtros y 3 variantes |
| Tipos de notificación | N/A | Primary (entrenador), Tertiary (PR), Surfacelight (leída) |
| Border-left por tipo | N/A | 4px con color semántico por categoría |
| Swipe-to-delete | N/A | Gesture con zona errorContainer |
| Motivation banner | N/A | Imagen grayscale con quote overlay |
| Filter chips | N/A | Horizontal scroll, bg-primary activo |

---

## Plan de Implementación

1. **Crear `NotificationsSheet` composable** — ModalBottomSheet con drag handle, header con back + "Marcar todas"
2. **FilterChipRow** — LazyRow con chips (Todas/No leídas/Leídas), estado seleccionado con primaryContainer
3. **NotificationCard** — Componente con 3 variantes: `unreadPrimary`, `unreadTertiary`, `read` — border-left colored, iconBox con body part-specific colors
4. **SwipeToDismiss** — `SwipeToDismissBox` de Material3 para acción delete en notificaciones leídas
5. **MotivationBanner** — Card con `Image(grayscale)` + gradient overlay + texto posicionado con `Modifier.align(BottomStart)`
6. **NotificationsViewModel** — Estado para filtro activo, lista de notificaciones, acciones marcar/eliminar
