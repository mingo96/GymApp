# 12 — Planificación (Planning Edit Sheet)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `682afa729e1545e59eedf067c819ea93` |
| Nombre | PlanningEditSheet |
| Tipo | BottomSheet (modal, task-focused) |
| Ancho diseño | 780 px (móvil) |
| Navegación | Suprimida — es un viaje de tarea enfocado |

---

## Jerarquía Visual

```
BottomSheet  bg-[#1A1A24]  rounded-t-32  z-50
├── DragHandle  w-12 h-1.5  outline-variant/30  rounded-full
├── Header  px-6  gap-4
│   ├── BackButton  40dp  rounded-xl  bg-surfaceContainerHigh
│   │   └── Icon "arrow_back"  onSurfaceVariant
│   └── Column
│       ├── Title "Planificación"  headline  bold  2xl  onSurface
│       └── Subtitle "Objetivo el Lun, 13 Abr 2026"  primary  sm  medium
├── Body  px-6  pb-28  space-y-8
│   ├── Question "¿Qué quieres planificar?"  onSurface  lg  semibold
│   ├── SelectionGrid  2 cols  gap-4
│   │   ├── Card:Selected  "Parte del cuerpo"
│   │   │   ├── GlowLayer  absolute  primary/20  blur-md  rounded-2xl
│   │   │   ├── Container  bg-surfaceContainerHigh  border-2 border-primary  rounded-2xl  p-6
│   │   │   ├── Icon "fitness_center"  FILL=1  primary  3xl
│   │   │   └── Label  onPrimaryContainer  bold  sm
│   │   └── Card:Inactive  "Rutina"
│   │       ├── Container  bg-surfaceContainerLow  rounded-2xl  p-6
│   │       ├── Icon "list"  onSurfaceVariant  3xl
│   │       └── Label  onSurfaceVariant  medium  sm
│   ├── DropdownSection
│   │   ├── Label "SELECCIONAR PARTE DEL CUERPO"  outline  xs  bold  uppercase  tracking-[0.2em]
│   │   └── DropdownRow  bg-surfaceContainerHighest  rounded-xl  p-4  border outline-variant/10
│   │       ├── DotIndicator  w-2 h-2  rounded-full  tertiary  glow(8px #27e0a9)
│   │       ├── Value "Pecho"  onSurface  medium
│   │       └── Icon "expand_more"  outline
│   └── ReminderSection
│       ├── Header  gap-2
│       │   ├── Icon "alarm"  secondary  xl
│       │   └── Label "Recordatorio"  onSurface  bold  sm
│       └── Card  bg-surfaceContainerLow  p-4  rounded-2xl  border outline-variant/10
│           ├── TimeDisplay
│           │   ├── Time "09:00"  headline  font-black  4xl  onSurface  tracking-tighter
│           │   └── Period "am"  xs  bold  outlineVariant  uppercase
│           └── DayChips  gap-2
│               ├── Chip:Inactive "L"  40dp  rounded-lg  bg-surfaceContainerHigh  onSurface  bold
│               ├── Chip:Active "M"  40dp  rounded-lg  bg-primaryContainer  onPrimaryContainer  bold  shadow(primary/20)
│               └── Chip:Inactive "X"  40dp  rounded-lg  bg-surfaceContainerHigh  onSurface  bold
└── StickyBottom  absolute bottom-0  p-6  gradient-to-t from-[#1A1A24]
    └── Button "Guardar"  w-full  h-14  rounded-xl
        ├── Background  gradient(primary → primaryContainer)
        ├── Shadow  0 8px 24px  primary/30
        ├── Label  onPrimary  bold  lg
        └── Icon "check_circle"  onPrimary
```

---

## Componentes Detallados

### 1. BottomSheet Container

| Propiedad | Valor |
|-----------|-------|
| Fondo | `#1A1A24` (custom, no token directo — cercano a surfaceContainerLow) |
| Esquinas | `rounded-t-32` → 32 dp top |
| Overlay | `bg-black/60 backdrop-blur-sm` detrás |
| Layout | `flex flex-col`, overflow hidden |
| Sombra | `shadow-2xl` |

### 2. Drag Handle

| Propiedad | Valor |
|-----------|-------|
| Ancho | 48 dp (`w-12`) |
| Alto | 6 dp (`h-1.5`) |
| Color | `outlineVariant/30` |
| Redondeo | `rounded-full` |
| Padding | `py-4` (16 dp arriba y abajo) |

### 3. Header

| Propiedad | Valor |
|-----------|-------|
| Back button | 40×40 dp, `rounded-xl` (12dp), `bg-surfaceContainerHigh` |
| Icono back | `arrow_back`, color `onSurfaceVariant` |
| Título | "Planificación", `Space Grotesk`, `bold`, `2xl` (~24sp), `onSurface` |
| Subtítulo | "Objetivo el Lun, 13 Abr 2026", `Manrope`, `medium`, `sm` (~14sp), `primary` |
| Separación header-body | `mb-6` (24 dp) |

### 4. Selection Grid

**Tarjeta Seleccionada ("Parte del cuerpo"):**

| Propiedad | Valor |
|-----------|-------|
| Glow externo | `absolute -inset-0.5`, `bg-primary/20`, `blur-md`, `rounded-2xl` |
| Fondo | `surfaceContainerHigh` (#2A292F) |
| Borde | `2px solid primary` (#BAC3FF) |
| Redondeo | `rounded-2xl` (16 dp) |
| Padding | `p-6` (24 dp) |
| Icono | `fitness_center`, FILL=1, `primary`, 30sp (`text-3xl`) |
| Icono margin-bottom | `mb-3` (12 dp) |
| Texto | `onPrimaryContainer` (#F4F2FF), `bold`, `sm`, centrado |

**Tarjeta Inactiva ("Rutina"):**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerLow` (#1B1B20) |
| Borde | ninguno (`border-transparent`) |
| Hover | `bg-surfaceContainerHigh` |
| Icono | `list`, `onSurfaceVariant`, 30sp |
| Texto | `onSurfaceVariant`, `medium` |

### 5. Dropdown (Parte del cuerpo)

| Propiedad | Valor |
|-----------|-------|
| Label | `outline` (#8E8FA1), `xs`, `bold`, `uppercase`, `tracking-[0.2em]`, `px-1` |
| Row fondo | `surfaceContainerHighest` (#35343A) |
| Row redondeo | `rounded-xl` (12 dp) |
| Row padding | `p-4` (16 dp) |
| Row borde | `outlineVariant/10` |
| Dot indicador | `w-2 h-2`, `rounded-full`, `tertiary` (#27E0A9), `shadow 0 0 8px #27e0a9` |
| Valor | "Pecho", `onSurface`, `medium` |
| Icono chevron | `expand_more`, `outline` |

### 6. Reminder Section

| Propiedad | Valor |
|-----------|-------|
| Icono cabecera | `alarm`, `secondary` (#D2BBFF), `xl` (20sp) |
| Label cabecera | "Recordatorio", `onSurface`, `bold`, `sm` |
| Card fondo | `surfaceContainerLow` (#1B1B20) |
| Card redondeo | `rounded-2xl` (16 dp) |
| Card padding | `p-4` (16 dp) |
| Card borde | `outlineVariant/10` |

**Hora:**

| Propiedad | Valor |
|-----------|-------|
| Dígitos | "09:00", `Space Grotesk`, `font-black` (900), `4xl` (~36sp), `onSurface`, `tracking-tighter` |
| Periodo | "am", `xs`, `bold`, `outlineVariant`, `uppercase` |

**Day Chips (L, M, X):**

| Propiedad | Valor |
|-----------|-------|
| Tamaño | 40×40 dp |
| Redondeo | `rounded-lg` (8 dp) |
| **Activo** | `bg-primaryContainer` (#4361EE), `onPrimaryContainer` (#F4F2FF), `bold`, `shadow-lg shadow-primary/20` |
| **Inactivo** | `bg-surfaceContainerHigh` (#2A292F), `onSurface` (#E4E1E9), `bold` |

### 7. Sticky Bottom Button

| Propiedad | Valor |
|-----------|-------|
| Contenedor | `absolute bottom-0`, `p-6`, gradiente to-top `from-[#1A1A24] via-[#1A1A24] to-transparent` |
| Botón ancho | `w-full` |
| Botón alto | `h-14` (56 dp) |
| Fondo | `gradient(primary #BAC3FF → primaryContainer #4361EE)` dirección `to-br` |
| Redondeo | `rounded-xl` (12 dp) |
| Sombra | `0 8px 24px rgba(67,97,238,0.3)` |
| Texto | "Guardar", `onPrimary`, `bold`, `lg` |
| Icono | `check_circle`, `onPrimary` |
| Press | `scale(0.98)`, transición 200ms |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Tap tarjeta tipo | Alterna selección con animación glow (primary/20 blur) |
| Tap dropdown | Abre selector de parte del cuerpo (presumiblemente lista o picker) |
| Tap day chip | Toggle activo/inactivo con transición de color |
| Tap hora | Abre TimePicker nativo |
| Tap "Guardar" | Persiste la planificación, cierra sheet |
| Tap back button | Cierra sheet sin guardar |
| Drag handle | Swipe-down para cerrar |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Planning | No existe como sheet independiente | BottomSheet modal dedicado |
| Tipo selector | N/A | Grid 2 cols con glow en selección |
| Dropdown body part | N/A | Custom dropdown con dot indicador tertiary |
| Reminder | N/A | Inline time display + day chips |
| Day chips | N/A | 40dp cuadrados, primary vs surfaceContainerHigh |
| Glow effects | No se usan | primary/20 blur-md en selección activa |
| Bottom CTA | N/A | Gradient sticky con fade-up |

---

## Plan de Implementación

1. **Crear `PlanningEditSheet` composable** — ModalBottomSheet con drag handle, fondo `#1A1A24`, rounded-t-32
2. **SelectionGrid** — `LazyVerticalGrid(2)` con dos estados (selected con glow Box + border, inactive con surfaceContainerLow)
3. **DropdownRow** — Componente con dot indicator tertiary + exposed dropdown menu o custom sheet selector
4. **ReminderSection** — Time display (Space Grotesk black 36sp), TimePicker dialog, DayChip row con toggle state
5. **StickyBottom** — Box con gradient fade + gradient button (rutinAppButtonsColours o custom gradient brush)
6. **ViewModel** — `PlanningViewModel` con states para tipo seleccionado, parte del cuerpo, hora, días, y acción guardar
