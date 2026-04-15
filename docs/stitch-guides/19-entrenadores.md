# 19 — Entrenadores (Trainer Management Sheet)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `544d9931111b4010b677a4e49e97277c` |
| Nombre | TrainerManagementSheet |
| Tipo | BottomSheet modal |
| Ancho diseño | 780 px (móvil) |
| Fondo sheet | Gradient `#1A1A24 → #131318` (top→bottom) |
| Navegación | BottomNav visible pero opacity-20 (contexto, no interactiva) |

---

## Jerarquía Visual

```
Overlay  bg-black/60  backdrop-blur-sm  z-60
└── TrainerSheet  sheet-gradient  rounded-t-32  shadow(-12px 48px rgba(0,0,0,0.6))  border-t white/5  pb-10
    ├── DragHandle  w-12 h-1.5  outlineVariant/30  rounded-full
    ├── Header  px-6  py-4
    │   ├── BackButton  p-2  rounded-full  hover:bg-white/5
    │   │   └── Icon "arrow_back"  onSurface
    │   └── Title "Entrenadores"  center  headline  bold  xl  tracking-tight  (pr-8 for centering)
    └── Content  px-6  space-y-8  mt-2
        ├── NotificationsToggle
        │   ├── Container  bg-surfaceContainerLow  p-4  rounded-xl
        │   ├── Row
        │   │   ├── Icon "notifications_active" FILL=1  tertiary
        │   │   ├── Label "NOTIFICACIONES ACTIVADAS"  sm  semibold  tracking-wide  uppercase
        │   │   └── CheckCircle  24dp  bg-tertiary/20  rounded-full
        │   │       └── Icon "check"  tertiary  lg  bold
        ├── RedeemCodeSection
        │   ├── SectionTitle "CANJEAR CÓDIGO"  headline  sm  bold  outline  uppercase  tracking-[0.2em]
        │   └── Row  gap-3
        │       ├── Input  bg-surfaceContainerHigh  rounded-xl  px-4 py-4  mono  primary
        │       │   ├── Placeholder "EJ: COACH-2024"  outlineVariant
        │       │   └── Focus  ring-2 primary/30
        │       └── RedeemButton  button-metallic  px-6 py-4  rounded-xl
        │           ├── Text "Canjear"  headline  bold  onPrimary
        │           └── Shadow  primaryContainer/20
        ├── MyTrainersSection
        │   ├── SectionTitle "MIS ENTRENADORES"  headline  sm  bold  outline  uppercase  tracking-[0.2em]
        │   └── TrainerList  space-y-4
        │       ├── TrainerCard:Approved "Juan García"
        │       │   ├── Container  bg-surfaceContainerLow  rounded-2xl  p-4  hover:bg-surfaceVariant/30
        │       │   ├── Avatar  56dp  rounded-xl  object-cover
        │       │   │   └── VerifiedBadge  absolute -bottom-1 -right-1  bg-background  p-0.5  rounded-lg
        │       │   │       └── Icon "verified" FILL=1  tertiary  sm
        │       │   ├── Name  headline  bold  lg
        │       │   ├── Badge "APROBADO"  bg-tertiary/10  tertiary  10px  bold  uppercase  tracking-wider  rounded-md  border tertiary/20
        │       │   ├── Subtitle "Entrenador personal"  outline  sm
        │       │   └── ActionButtons  grid-2cols  gap-3
        │       │       ├── BlockBtn  outlined  border error/30  text error  bold  sm  hover:bg-error/5
        │       │       └── RevokeBtn  bg-surfaceContainerHighest  onSurface  bold  sm  hover:bg-surfaceBright
        │       └── TrainerCard:Pending "María López"
        │           ├── Container  bg-surfaceContainerLow  rounded-2xl  p-4  border white/5
        │           ├── Avatar  56dp  rounded-xl  grayscale  opacity-70
        │           ├── Name  headline  bold  lg  onSurface/80
        │           ├── Badge "PENDIENTE"  bg-yellow-500/10  yellow-500  10px  bold  uppercase  rounded-md  border yellow-500/20
        │           └── Subtitle "Especialista en Nutrición"  outline  sm
        └── CapacityIndicator  mt-8  px-6
            ├── ProgressBar  h-1  w-full  bg-surfaceContainerHighest  rounded-full
            │   └── Fill  w-2/3  bg-tertiary  shadow(0 0 8px tertiary/50)  rounded-full
            └── Label "Capacidad del equipo: 2/3"  10px  bold  outlineVariant  uppercase  tracking-widest  center
```

---

## Componentes Detallados

### 1. Sheet Background

| Propiedad | Valor |
|-----------|-------|
| Gradiente | `linear-gradient(180deg, #1A1A24 0%, #131318 100%)` |
| Esquinas | `rounded-t-[32px]` |
| Sombra | `0 -12px 48px rgba(0,0,0,0.6)` |
| Borde top | `white/5` |

### 2. Notifications Toggle

| Propiedad | Valor |
|-----------|-------|
| Container | `surfaceContainerLow`, `rounded-xl`, `p-4` |
| Icono | `notifications_active` FILL=1, `tertiary` (#27E0A9) |
| Label | `sm`, `semibold`, `tracking-wide`, `uppercase` |
| Check indicator | 24×24 dp, `bg-tertiary/20`, `rounded-full` |
| Check icon | `check`, `tertiary`, `lg`, `bold` |

### 3. Redeem Code Input

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerHigh` (#2A292F) |
| Borde | ninguno |
| Redondeo | `rounded-xl` |
| Padding | `px-4 py-4` |
| Texto | `primary` (#BAC3FF), `mono` (monospace) |
| Placeholder | "EJ: COACH-2024", `outlineVariant` |
| Focus | `ring-2 primary/30` |

**Botón Canjear:**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `button-metallic` = `gradient(135deg, #BAC3FF → #4361EE)` |
| Padding | `px-6 py-4` |
| Redondeo | `rounded-xl` |
| Texto | "Canjear", `headline`, `bold`, `onPrimary` |
| Sombra | `primaryContainer/20` |
| Press | `scale(0.95)`, transición 200ms |

### 4. Trainer Cards

**Variante: Aprobado (Juan García)**

| Propiedad | Valor |
|-----------|-------|
| Container | `surfaceContainerLow`, `rounded-2xl`, `p-4` |
| Hover | `surfaceVariant/30` |
| Avatar | 56×56 dp, `rounded-xl`, `object-cover` |
| Verified badge | Absolute bottom-right, `bg-background`, `p-0.5`, `rounded-lg`, `verified` FILL=1 `tertiary` `sm` |
| Nombre | `Space Grotesk`, `bold`, `lg` |
| Status badge | `bg-tertiary/10`, `tertiary`, `10px`, `bold`, `uppercase`, `tracking-wider`, `rounded-md`, `border tertiary/20` |
| Subtítulo | `outline`, `sm` |

**Botones de acción (grid 2 cols):**

| Botón | Estilo |
|-------|--------|
| Bloquear | `outlined`, `border error/30`, `text error`, `bold`, `sm`, hover: `bg-error/5` |
| Revocar | `bg-surfaceContainerHighest`, `onSurface`, `bold`, `sm`, hover: `bg-surfaceBright` |

**Variante: Pendiente (María López)**

| Propiedad | Diferencia vs Aprobado |
|-----------|----------------------|
| Avatar | `grayscale`, `opacity-70` |
| Nombre | `onSurface/80` (más tenue) |
| Badge | `bg-yellow-500/10`, `yellow-500`, `border yellow-500/20`, texto "PENDIENTE" |
| Container | borde adicional `white/5` |
| Sin botones de acción | No se muestran hasta aprobación |

### 5. Capacity Indicator

| Propiedad | Valor |
|-----------|-------|
| Track | `h-1`, `w-full`, `bg-surfaceContainerHighest`, `rounded-full` |
| Fill | `w-2/3` (66%), `bg-tertiary` (#27E0A9), `rounded-full` |
| Fill glow | `shadow 0 0 8px rgba(39,224,169,0.5)` |
| Label | "Capacidad del equipo: 2/3", `10px`, `bold`, `outlineVariant`, `uppercase`, `tracking-widest`, centrado |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Toggle notificaciones | Activa/desactiva notificaciones de entrenadores |
| Input código + "Canjear" | Valida código de invitación, añade entrenador como Pendiente |
| Tap "Bloquear" | Bloquea entrenador (confirmación previa), color error |
| Tap "Revocar" | Revoca acceso del entrenador (confirmación previa) |
| Entrenador pendiente | Sin acciones — espera aprobación del entrenador |
| Progress bar | Visual del límite de entrenadores (2/3 en ejemplo) |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Gestión entrenadores | No existe como sheet dedicado | BottomSheet con gradient bg |
| Código canjeo | N/A | Input mono + botón gradient |
| Trainer cards | N/A | 2 variantes (Approved con actions, Pending sin actions) |
| Verified badge | N/A | Icono tertiary absolute en avatar |
| Status badges | N/A | Tertiary (Aprobado), Yellow (Pendiente) |
| Capacity indicator | N/A | Progress bar tertiary con glow + label |
| Block/Revoke | N/A | Grid 2 cols con error outlined y surface filled |

---

## Plan de Implementación

1. **Crear `TrainerManagementSheet` composable** — ModalBottomSheet con gradient Brush background, rounded-t-32
2. **NotificationToggle** — Row con switch M3 (tertiary colors) + icon notifications_active
3. **RedeemCodeRow** — TextField mono + gradient button (reutilizar button-metallic brush)
4. **TrainerCard** — Composable con sealed class para estados (Approved/Pending/Blocked), avatar con verified badge overlay, action buttons
5. **CapacityIndicator** — LinearProgressIndicator custom con tertiary color + glow shadow
6. **TrainerViewModel** — Estado lista entrenadores, canjeo de código, toggle notificaciones, acciones block/revoke con confirmación
