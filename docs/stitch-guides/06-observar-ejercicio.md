# 06 — Observar Ejercicio (Observe Exercise Dialog)

## Metadatos Stitch
- **Screen ID**: `cc68e34343704798909fbed0eb8c8e71`
- **Título**: Observar Ejercicio - Dialog
- **Tipo**: Dialog (modal, centrado)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Overlay (bg-black/80, backdrop-blur-sm)
└── Dialog (bg-surfaceContainerLow, rounded-xl, max-w-lg)
    ├── Accent gradient bar (top, h-1, primary→primaryContainer, opacity-50)
    │
    ├── Content (p-8)
    │   ├── Header
    │   │   ├── Left: Nombre "Bench Press" + Tag "PECHO" (tertiary)
    │   │   └── Right: Icono fitness_center (surfaceContainerHigh box)
    │   │
    │   ├── Descripción
    │   │   ├── Section label: "DESCRIPCIÓN"
    │   │   └── Card: surfaceContainerLowest, border-l-2 primary/30
    │   │
    │   ├── Metrics Grid (2 cols)
    │   │   ├── Sets y reps: "4x8 — 80kg"
    │   │   └── Tipo: "Progresivo"
    │   │
    │   ├── Ejercicios Equivalentes
    │   │   ├── Section label + "Añadir relación" button
    │   │   └── Chips: "Press inclinado", "Cable fly"
    │   │
    │   └── Action Row (border-t)
    │       ├── "Cerrar" (outlined)
    │       └── "Editar" (gradient)
    │
    └── (sombra: shadow-2xl)
```

---

## Componentes Detallados

### 1. Overlay & Dialog Container

```
Overlay: fixed inset-0, bg-black/80, backdrop-blur-sm, z-60
         flex items-center justify-center, p-4

Dialog:
  Fondo: #1A1A24 (ligeramente más claro que surface, similar a Exercise List sheet)
  Variante alternativa: surfaceContainerLow (#1B1B20)
  Forma: rounded-xl (12dp)
  Ancho: max-w-lg (100%)
  Sombra: 0 32px 64px rgba(0,0,0,0.5)
  Borde: 1px outlineVariant/10
  Overflow: hidden
```

**Accent Gradient Bar (top decorativo):**
- Position: absolute, top-0, left-0, full width
- Altura: 4dp (h-1)
- Gradient: 135deg, primary (#BAC3FF) → primaryContainer (#4361EE)
- Opacity: 50%

---

### 2. Header

```
Padding: p-8
Layout: Row, space-between, items-start
Margin bottom: 32dp (mb-8)
```

**Left side (Column):**
- Nombre: "Bench Press"
  - Fuente: Space Grotesk, 30sp (text-3xl), bold, tracking-tight
  - Color: onSurface (#E4E1E9)
  - Margin bottom: 8dp (mb-2)
- Tag grupo muscular: "PECHO"
  - Fondo: tertiary/10
  - Color texto: tertiary (#27E0A9)
  - Borde: 1px tertiary/20
  - Padding: px-3 py-1, rounded-full
  - Fuente: 12sp (text-xs), bold, uppercase, tracking-widest

**Right side:**
- Container: surfaceContainerHigh (#2A292F), p-3, rounded-xl
- Borde: 1px outlineVariant/15
- Icono: fitness_center, color primary (#BAC3FF)

---

### 3. Descripción

**Section label:**
- Fuente: Space Grotesk, 12sp (text-xs), font-black, uppercase, tracking-widest
- Color: slate-500 (~outline tono)
- Margin bottom: 12dp (mb-3)

**Card descripción:**
```
Fondo: surfaceContainerLowest (#0E0E13)
Padding: 20dp (p-5)
Forma: rounded-xl
Borde izquierdo: 2px solid primary/30
```
- Texto: Manrope, regular, leading-relaxed
- Color: onSurfaceVariant (#C4C5D7)

---

### 4. Metrics Grid

```
Layout: Grid 2 columnas, gap-4
Margin bottom: 32dp (mb-8)
```

**Card métrica:**
```
Fondo: surfaceContainerHigh/40
Padding: 16dp (p-4)
Forma: rounded-xl
Borde: 1px outlineVariant/5
```

| Métrica | Label | Valor | Extra |
|---------|-------|-------|-------|
| Sets y reps | 10sp, bold, uppercase, tracking-[0.2em], slate-500 | "4x8" — Space Grotesk, 24sp, bold, onSurface | "— 80kg" 14sp, medium, primary |
| Tipo | 10sp, bold, uppercase, tracking-[0.2em], slate-500 | "Progresivo" — Space Grotesk, 20sp, semibold, onSurface | — |

---

### 5. Ejercicios Equivalentes

**Header row (flex, space-between, items-center, mb-4):**
- Label: "EJERCICIOS EQUIVALENTES" — Space Grotesk, 12sp, font-black, uppercase, tracking-widest, slate-500
- Botón "Añadir relación":
  - Row, gap-1.5
  - Border: 1px outlineVariant
  - Padding: px-3 py-1.5, rounded-lg
  - Fuente: 10sp, bold, uppercase, tracking-wider
  - Color: onSurfaceVariant
  - Hover: bg white/5
  - Icono: add, 14sp

**Chips (flex-wrap, gap-2):**
```
Cada chip:
  Layout: Row, items-center, gap-2
  Padding: px-4 py-2
  Fondo: surfaceContainerHighest (#35343A)
  Forma: rounded-xl
  Borde: 1px white/5
  Hover: border primary/30
  Cursor: pointer
```
- Icono: link, 14sp, primary, hover rotate-45
- Texto: 14sp (text-sm), medium, onSurface

---

### 6. Action Row

```
Margin top: 40dp (margin via mb-10 en section anterior)
Padding top: 16dp (pt-4)
Border top: 1px white/5
Layout: Row, gap-4
```

| Botón | Flex | Fondo | Color | Forma | Fuente | Extra |
|-------|------|-------|-------|-------|--------|-------|
| Cerrar | flex-1 | transparent | onSurface | rounded-xl | bold, uppercase, tracking-widest, 12sp | border 1px outlineVariant/30, hover bg white/5 |
| Editar | flex-1 | gradient primary→primaryContainer | onPrimaryFixed (#001159) | rounded-xl | bold, uppercase, tracking-widest, 12sp | shadow-lg primaryContainer/20 |

- Padding: py-4 px-6
- Active: scale-95
- Transición: all, 150ms

---

## Comportamiento e Interacciones

1. **Overlay tap**: Cierra el diálogo
2. **Cerrar**: Cierra el diálogo
3. **Editar**: Navega a Create/Edit Exercise dialog con datos pre-cargados
4. **Chips equivalentes**: Tap para ver ese ejercicio equivalente
5. **Añadir relación**: Abre selector para vincular ejercicios equivalentes
6. **Link icon rotate**: Animación sutil al hover (45° rotation)

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Layout | Sheet simple / sin dialog | Dialog modal centrado con overlay |
| Accent | Sin decoración | Gradient bar top |
| Descripción | Texto plano | Card con borde izquierdo primary |
| Métricas | Lista simple | Grid 2 cols con cards |
| Equivalentes | Sin sección | Chips con icono link |
| Acciones | Botones básicos | Cerrar (outlined) + Editar (gradient) |

---

## Plan de Implementación

### Paso 1: Crear Dialog composable
- `ObserveExerciseDialog` con AlertDialog o Dialog de Material3
- Overlay con blur
- Gradient accent bar

### Paso 2: Layout interno
- Header con nombre + tag muscle + icono
- Descripción card con border-start
- Metrics grid 2 cols

### Paso 3: Ejercicios equivalentes
- Chips clickables con icono link
- Botón "Añadir relación"

### Paso 4: Action Row
- Cerrar (OutlinedButton) + Editar (gradient button)
