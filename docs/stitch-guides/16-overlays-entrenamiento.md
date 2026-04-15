# 16 — Overlays de Entrenamiento Activo (Workout Overlays)

Este documento cubre dos pantallas complementarias que representan el entrenamiento activo cuando el usuario está **fuera de la app** o en **otra sección de la app**.

---

## A) Mini Workout Bubble (Burbuja flotante)

### Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `d77d56ae78c44d0995e124241b0d29c0` |
| Nombre | Mini Workout Bubble |
| Tipo | Overlay flotante (Picture-in-Picture / Bubble) |
| Contexto | Home screen del dispositivo (fuera de la app) |

### Jerarquía Visual

```
DeviceHomeScreen  (simulado con wallpaper + app icons)
├── StatusBar  9:41  signal/wifi/battery
├── AppGrid  4 cols  (Spotify, Instagram, etc.)
│   └── KineticApp  bg-slate-800  border white/10
│       └── Icon "bolt"  tertiary  FILL=1
├── WorkoutBubble  absolute  right-6  centerY  z-50
│   ├── BubbleCircle  56dp  bg-surface/70  backdrop-blur-xl
│   │   ├── Border  tertiary/40
│   │   ├── Ring  2px  tertiary/20
│   │   ├── Animation  neon-pulse (scale 0.95→1, shadow tertiary 0→15px→0, 2s infinite)
│   │   ├── Icon "fitness_center"  tertiary  xl  FILL=1
│   │   └── TimerBadge  bg-tertiary  rounded-full  px-2 py-0.5
│   │       └── Time "01:23:45"  8px  bold  onTertiary  tracking-tight
│   └── Tooltip  right-of-bubble  mr-4
│       ├── Container  bg-surfaceContainerHigh/90  backdrop-blur-md  px-4 py-2  rounded-xl
│       ├── Border  outlineVariant/30
│       ├── Shadow  2xl
│       ├── Label "CURRENT SET"  10px  uppercase  tracking-widest  slate-400  bold
│       ├── Value "Bench Press • 80kg"  xs  bold  onSurface
│       └── Icon "play_arrow"  tertiary  sm
└── Dock  h-90  bg-white/10  backdrop-blur-2xl  rounded-[36px]
```

### Componentes Detallados

**Bubble:**

| Propiedad | Valor |
|-----------|-------|
| Tamaño | 56×56 dp (w-14 h-14) |
| Fondo | `surface/70` (#131318 al 70%) + `backdrop-blur-xl` |
| Borde | `tertiary/40` (#27E0A9 al 40%) |
| Ring | `2px`, `tertiary/20` |
| Animación | `neon-pulse`: scale 0.95→1, box-shadow tertiary 0→15px→0, ciclo 2s infinite |
| Icono | `fitness_center`, `tertiary`, `xl`, FILL=1 |
| Timer badge | `bg-tertiary`, `rounded-full`, `px-2 py-0.5` |
| Timer texto | `8px`, `bold`, `onTertiary` (#003827), `tracking-tight` |

**Tooltip (contextual):**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerHigh/90` + `backdrop-blur-md` |
| Padding | `px-4 py-2` |
| Redondeo | `rounded-xl` (12 dp) |
| Borde | `outlineVariant/30` |
| Label | "CURRENT SET", `10px`, `uppercase`, `tracking-widest`, `slate-400`, `bold` |
| Valor | "Bench Press • 80kg", `xs`, `bold`, `onSurface` |
| Icono | `play_arrow`, `tertiary`, `sm` |

### Nota sobre implementación Android

Esta pantalla es conceptual. En Android, esto se implementaría como:
- **Notification persistente** con controls (ya parcialmente implementado)
- **Bubble API** (Android 11+) para la burbuja flotante
- **Picture-in-Picture** no aplica directamente para este caso

El tooltip es un concepto de diseño — en Android la burbuja se expandiría al tocarla para mostrar el panel overlay.

---

## B) Workout Overlay Panel (Panel compacto)

### Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `3247c506c4a14d01aa75683dff6bedb5` |
| Nombre | Workout Overlay Panel |
| Tipo | Overlay panel flotante (centrado) |
| Dimensiones | 340×520 dp |
| Navegación | BottomNav visible (Train activo) |

### Jerarquía Visual

```
Background  blurred gym dashboard  opacity-40  blur-sm
├── TopAppBar  fixed  h-16  bg-[#131318]/70  backdrop-blur-xl
│   ├── Icon "menu"  indigo-200
│   ├── Title "KINETIC VAULT"  Space Grotesk  uppercase  tracking-widest
│   └── Avatar  40dp  rounded-full  bg-surfaceContainerHighest  border white/10
└── OverlayPanel  340×520  rounded-32  glass-panel  border white/10  ambient-glow  z-10
    ├── Header  p-6 pb-2
    │   ├── Row
    │   │   ├── Column
    │   │   │   ├── Label "CURRENT WORKOUT"  10px  bold  tracking-[0.2em]  onSurfaceVariant  uppercase  headline
    │   │   │   └── WorkoutName "Empuje Superior A"  lg  headline  bold  onPrimaryContainer
    │   │   └── ActionButtons  gap-2
    │   │       ├── MinimizeBtn  32dp  rounded-full  bg-white/5
    │   │       │   └── Icon "keyboard_arrow_down"  sm
    │   │       └── CloseBtn  32dp  rounded-full  bg-white/5
    │   │           └── Icon "close"  sm  error
    │   └── Timer "01:23:45"  3xl  headline  font-black  tracking-tighter  tertiary
    ├── MainContent  flex-1  px-6  gap-6
    │   ├── ExerciseInfo
    │   │   ├── Name "Bench Press"  2xl  headline  bold  white  tracking-tight
    │   │   └── SetProgress "Set 3 / 4"  sm  medium  tertiary
    │   ├── InputGrid  2cols  gap-4
    │   │   ├── RepsInput  bg-surfaceContainerLow  rounded-2xl  p-3  focus:ring-2 primary/30
    │   │   │   ├── Label "REPS"  10px  bold  onSurfaceVariant  uppercase  tracking-widest
    │   │   │   ├── MinusBtn  32dp  rounded-full  bg-surfaceContainerHighest
    │   │   │   ├── Value "10"  2xl  headline  bold
    │   │   │   └── PlusBtn  32dp  rounded-full  bg-surfaceContainerHighest
    │   │   └── WeightInput  (same structure, label "WEIGHT (KG)", value "80")
    │   ├── LogSetButton  w-full  h-14
    │   │   ├── Background  gradient(tertiary → tertiaryContainer)
    │   │   ├── Redondeo  rounded-xl
    │   │   ├── Text "LOG SET"  onTertiary  bold  headline  uppercase  tracking-widest  sm
    │   │   └── Shadow  tertiary/20
    │   └── ExerciseChips  horizontal-scroll  gap-2
    │       ├── Chip:Active "Bench Press"  bg-primaryContainer  onPrimaryContainer  bold  xs  ring-1 primary/50  rounded-full
    │       ├── Chip:Inactive "Incline DB"  bg-surfaceContainerHigh  onSurfaceVariant  semibold
    │       └── Chip:Inactive "Tricep Pushdown"
    └── Footer  bg-black/20  px-6 py-4  border-t white/5
        ├── Stat "Sets: 12"
        ├── Divider  h-3 w-1  white/10
        ├── Stat "Vol: 2.8k kg"
        ├── Divider
        └── Stat "Time: 83m"
```

### Componentes Detallados

**Panel Container (Glass):**

| Propiedad | Valor |
|-----------|-------|
| Dimensiones | 340×520 dp |
| Fondo | `rgba(19,19,24,0.85)` + `backdrop-filter: blur(24px)` |
| Redondeo | `rounded-[32px]` (32 dp) |
| Borde | `white/10` |
| Sombra | `ambient-glow`: `0 32px 64px -12px rgba(0,0,0,0.6)`, `0 0 40px 0 rgba(67,97,238,0.1)` |

**Timer:**

| Propiedad | Valor |
|-----------|-------|
| Texto | "01:23:45" |
| Font | `Space Grotesk`, `font-black` (900), `3xl` (~30sp) |
| Color | `tertiary` (#27E0A9) |
| Tracking | `tracking-tighter` |

**Input Step Controls:**

| Propiedad | Valor |
|-----------|-------|
| Container | `bg-surfaceContainerLow`, `rounded-2xl`, `p-3` |
| Focus ring | `ring-2 primary/30` |
| Label | `10px`, `bold`, `onSurfaceVariant`, `uppercase`, `tracking-widest` |
| Buttons (±) | 32×32 dp, `rounded-full`, `bg-surfaceContainerHighest` |
| Valor | `2xl` (~24sp), `Space Grotesk`, `bold` |

**Log Set Button:**

| Propiedad | Valor |
|-----------|-------|
| Alto | 56 dp (h-14) |
| Fondo | `gradient(tertiary #27E0A9 → tertiaryContainer #007F5D)` dirección `to-br` |
| Texto | "LOG SET", `onTertiary`, `bold`, `headline`, `uppercase`, `tracking-widest`, `sm` |
| Redondeo | `rounded-xl` (12 dp) |
| Sombra | `tertiary/20` |
| Press | `scale(0.98)` |

**Exercise Chips:**

| Propiedad | Activo | Inactivo |
|-----------|--------|----------|
| Fondo | `primaryContainer` (#4361EE) | `surfaceContainerHigh` (#2A292F) |
| Texto | `onPrimaryContainer` (#F4F2FF), `bold` | `onSurfaceVariant`, `semibold` |
| Ring | `1px primary/50` | ninguno |
| Redondeo | `rounded-full` | `rounded-full` |
| Size | `xs` | `xs` |

**Footer Stats Row:**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `black/20` |
| Borde top | `white/5` |
| Label | `10px`, `bold`, `onSurfaceVariant`, `uppercase`, `tracking-tighter` |
| Value | `xs`, `Space Grotesk`, `bold`, `white` |
| Divider | `h-3 w-[1px] bg-white/10` |

---

## Comportamiento e Interacciones

### Bubble

| Interacción | Efecto |
|-------------|--------|
| Tap burbuja | Expande al Overlay Panel o abre la app en ActiveWorkout |
| Long press | Muestra tooltip contextual |
| Drag | Reposicionar burbuja en pantalla |
| Animación continua | neon-pulse 2s infinite mientras hay workout activo |

### Panel

| Interacción | Efecto |
|-------------|--------|
| Tap "keyboard_arrow_down" | Minimiza panel → vuelve a burbuja |
| Tap "close" (error) | Cierra overlay (no termina workout, vuelve a app) |
| Tap ±buttons | Incrementa/decrementa reps o weight |
| Tap "Log Set" | Registra serie actual, avanza al siguiente set |
| Tap exercise chip | Cambia al ejercicio seleccionado |
| Stats footer | Solo lectura, actualizados en tiempo real |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Overlay workout | No hay overlay panel | Panel glass 340×520 centrado flotante |
| Bubble | No existe | Burbuja 56dp con neon-pulse animation |
| Tooltip | N/A | Glass card con datos del set actual |
| Log Set button | En pantalla completa | En overlay panel, gradient tertiary→tertiaryContainer |
| Exercise chips | Tab/carousel en activo | Horizontal scroll chips (primaryContainer activo) |
| Stats footer | Dentro de ActiveWorkout | Footer persistente en overlay panel |
| Input controls | Stepper en dialog | Stepper inline en panel (32dp ±buttons) |

---

## Plan de Implementación

1. **Notification persistente mejorada** — Actualizar la notificación de entrenamiento activo con controles MediaStyle (ya existe parcialmente)
2. **Overlay Service** — Crear `WorkoutOverlayService` con `TYPE_APPLICATION_OVERLAY` permission para la burbuja flotante
3. **BubbleComposable** — Burbuja 56dp con animación scale+glow infinite, timer badge, posición draggable
4. **OverlayPanelComposable** — Panel 340×520 con glass effect (`Modifier.background(brush).blur()`), stepper inputs, exercise chips, log set button con tertiary gradient
5. **Panel↔Bubble toggle** — Minimizar (flecha abajo) anima de panel a burbuja; tap burbuja expande a panel
6. **Integrar con WorkoutViewModel existente** — El overlay comparte el mismo estado que ActiveWorkoutPage, actualizando sets/reps/weight en tiempo real
