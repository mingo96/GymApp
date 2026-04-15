# 23 — Widget Flotante Rediseñado (Floating Workout Widget)

## Metadatos Stitch
- **Título**: Widget Flotante — Floating Workout Widget (Rediseño)
- **Dispositivo**: MOBILE (overlay sobre cualquier app)
- **Acceso**: Se activa al iniciar un entrenamiento y pulsar Home o cambiar de app
- **Nota**: Complementa la guía 16 (Overlays de Entrenamiento) con diseño actualizado

---

## Jerarquía Visual — Estado Colapsado (Bubble)

```
Overlay (WindowManager, TYPE_APPLICATION_OVERLAY)
├── Bubble Container (56dp circle)
│   ├── Fondo: #131318/95% (#F2131318), borde tertiary pulse
│   ├── Icono central: fitness_center (tertiary #27E0A9, 24dp)
│   └── Timer badge (pill, bottom-center, offset-y 4dp)
│       ├── Fondo: surfaceContainerHigh (#2A292F)
│       ├── Texto: "12:45" (tertiary, 10sp, bold, monospace)
│       └── Forma: rounded-full, px-2 py-0.5
│
└── Comportamiento drag
    ├── Arrastrable en X e Y
    ├── Snap to edges (left/right) al soltar
    └── Tap → expandir a panel
```

---

## Jerarquía Visual — Estado Expandido (Panel)

```
Overlay (WindowManager, TYPE_APPLICATION_OVERLAY)
├── Expanded Container (340×auto dp, max 520dp)
│   ├── Fondo: #131318/97% (#F7131318)
│   ├── Forma: rounded-3xl (24dp)
│   ├── Borde: 1px white/5
│   │
│   ├── Drag Handle (top, centered)
│   │   └── Bar: 40dp × 4dp, surfaceContainerHighest, rounded-full
│   │
│   ├── Header Row
│   │   ├── Left: Label "ENTRENAMIENTO ACTUAL" (10sp, outline, uppercase, tracking)
│   │   ├── Center: Título workout "Empuje A" (16sp, bold, onSurface)
│   │   └── Right: Botones minimize (remove) + close (close)
│   │       ├── Minimize: onSurfaceVariant, tap → colapsar a bubble
│   │       └── Close: onSurfaceVariant, tap → colapsar a bubble
│   │
│   ├── Timer Display
│   │   ├── "00:12:45" (32sp, bold, tertiary #27E0A9, monospace)
│   │   └── Fuente: Space Grotesk
│   │   └── Centered, margin vertical 8dp
│   │
│   ├── Exercise Info
│   │   ├── Nombre: "Press Banca" (20sp, bold, onSurface)
│   │   └── Badge: "Serie 3" (pill, tertiary/10 bg, tertiary text, 12sp bold)
│   │
│   ├── Stepper Row (horizontal o vertical según ancho)
│   │   ├── Stepper: REPS
│   │   │   ├── Label: "REPS" (10sp, outline, uppercase)
│   │   │   ├── Valor: "10" (28sp, bold, Space Grotesk, onSurface)
│   │   │   └── Botones: [−] [+] (36dp, surfaceContainerHighest, rounded-lg)
│   │   │       ├── − color: error (#FFB4AB)
│   │   │       └── + color: tertiary (#27E0A9)
│   │   │
│   │   └── Stepper: PESO (KG)
│   │       ├── Label: "PESO (KG)" (10sp, outline, uppercase)
│   │       ├── Valor: "60" (28sp, bold, Space Grotesk, onSurface)
│   │       └── Botones: [−] [+] (mismos estilos que REPS)
│   │
│   ├── Botón "REGISTRAR SERIE" (full width)
│   │   ├── Fondo: gradient to-r from-tertiary (#27E0A9) to-tertiary/80
│   │   ├── Color texto: onTertiary (#003827)
│   │   ├── Fuente: Space Grotesk, 14sp, bold, tracking-wider
│   │   ├── Forma: rounded-xl (12dp)
│   │   ├── Padding: py-3
│   │   └── Sombra: shadow-lg shadow-tertiary/20
│   │
│   ├── Exercise Chips (horizontal scroll)
│   │   ├── Chip activo: primary bg, onPrimary text
│   │   ├── Chip inactivo: surfaceContainerHigh bg, onSurfaceVariant text
│   │   ├── Forma: rounded-full
│   │   └── Padding: px-3 py-1.5, gap-2
│   │
│   └── Footer Stats Bar
│       ├── Layout: Row, space-between, items-center
│       ├── Fondo: surfaceContainerLow (#1B1B20), rounded-xl, p-3
│       ├── Stats: SETS: 8 | VOL: 1,200 kg | TIEMPO: 12m
│       │   ├── Label: 8sp, outline, uppercase
│       │   └── Valor: 12sp, bold, onSurface
│       └── Botón fullscreen (right)
│           ├── Icono: open_in_full (24dp)
│           ├── Color: primary (#BAC3FF)
│           └── Tap: animated exit → abrir app en WorkoutPage
│
└── Resize handles (4 bordes)
    ├── Horizontal: permite cambiar ancho (min 240dp, max screen width - 32dp)
    └── Layout se adapta a ancho (ver Adaptive Layout)
```

---

## Componentes Detallados

### 1. Bubble (Colapsado)

```
Dimensiones: 56×56dp
Fondo: #F2131318 (95% opacidad sobre #131318)
Forma: circle (rounded-full)
Elevación: WindowManager overlay
```

**Borde animado:**
- 2dp tertiary (#27E0A9) con animación pulse
- Alternancia: opacity 0.4 → 1.0, ciclo 2s
- Indica entrenamiento en progreso

**Icono central:**
- Material icon: `fitness_center`
- Color: tertiary (#27E0A9)
- Size: 24dp

**Timer badge:**
```
Position: below bubble, centered (offset-y +4dp)
Fondo: surfaceContainerHigh (#2A292F)
Borde: 1px white/10
Forma: rounded-full
Padding: px-2 py-0.5
Texto: "12:45" — Space Grotesk, 10sp, bold, tertiary
```

**Drag behavior:**
- Touch → elevar con sombra (shadow-2xl)
- Move → seguir dedo sin lag
- Release → snap al borde más cercano (left o right), animación spring 300ms

---

### 2. Drag Handle

```
Posición: top-center del panel expandido
Dimensiones: 40dp × 4dp
Fondo: surfaceContainerHighest (#35343A)
Forma: rounded-full
Margin top: 8dp
Margin bottom: 4dp
```

- Arrastrando: mover todo el panel
- Double-tap: toggle entre minimizado y maximizado

---

### 3. Header Row

```
Layout: Row, space-between, items-center
Padding: 12dp horizontal, 8dp vertical
```

**Label "ENTRENAMIENTO ACTUAL":**
- Fuente: Space Grotesk, 10sp, bold, tracking-[0.15em], uppercase
- Color: outline (#8E8FA1)

**Título workout:**
- Fuente: Space Grotesk, 16sp, bold
- Color: onSurface (#E4E1E9)
- Max 1 línea, ellipsis

**Botones (Row, gap-2):**

| Botón | Icono | Acción |
|-------|-------|--------|
| Minimize | remove | Colapsar a bubble (animated) |
| Close | close | Colapsar a bubble (animated) |

- Size: 28dp
- Fondo: transparent → surfaceContainerHigh on press
- Color: onSurfaceVariant (#C4C5D7)
- Forma: rounded-lg

---

### 4. Timer Display

```
Texto: "00:12:45"
Fuente: Space Grotesk, 32sp, bold, monospace/tabular-nums
Color: tertiary (#27E0A9)
Alineación: center
Margin: 8dp vertical
```

- Actualización: cada segundo
- Formato: HH:MM:SS
- Glow sutil: text-shadow 0 0 20px tertiary/20

---

### 5. Stepper Cards

```
Layout: Row (por defecto), Column si ancho < 320dp
Gap: 12dp
Margin: 12dp vertical
```

**Cada stepper:**
```
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl (12dp)
Padding: 12dp
Layout: Column, items-center
Flex: 1 (en row), full-width (en column)
```

**Label:**
- Fuente: Space Grotesk, 10sp, bold, tracking-[0.15em], uppercase
- Color: outline (#8E8FA1)
- Margin bottom: 4dp

**Valor:**
- Fuente: Space Grotesk, 28sp, bold
- Color: onSurface (#E4E1E9)
- Margin: 4dp vertical

**Botones [−] [+]:**
```
Size: 36×36dp
Fondo: surfaceContainerHighest (#35343A)
Forma: rounded-lg (8dp)
Layout: Row, gap-12dp (o gap-8dp en compact)
Icono: 18dp
```

| Botón | Icono | Color | Long press |
|-------|-------|-------|------------|
| − | remove | error (#FFB4AB) | Decremento rápido (−1/200ms) |
| + | add | tertiary (#27E0A9) | Incremento rápido (+1/200ms) |

---

### 6. Botón "REGISTRAR SERIE"

```
Ancho: full width (dentro del padding)
Fondo: gradient to-right from-tertiary to-tertiary-container (#27E0A9 → #007F5D)
Color texto: onTertiary (#003827)
Fuente: Space Grotesk, 14sp, bold, tracking-wider, uppercase
Forma: rounded-xl (12dp)
Padding: py-3
Sombra: 0 8px 24px tertiary/20
Active: scale-95, sombra reducida
```

**Feedback al registrar:**
1. Pulse animation (scale 1→0.95→1, 200ms)
2. Texto cambia brevemente a "✓ Serie 3 registrada"
3. Timer de descanso (opcional): countdown overlay
4. Stepper se resetea o avanza a siguiente serie

---

### 7. Exercise Chips (Scroll Horizontal)

```
Layout: HorizontalScrollView, gap-2
Padding horizontal: 12dp
Margin: 8dp vertical
```

**Chip activo:**
```
Fondo: primary (#BAC3FF)
Color texto: onPrimary (#00218D)
Fuente: 11sp, bold
Forma: rounded-full
Padding: px-3 py-1.5
```

**Chip inactivo:**
```
Fondo: surfaceContainerHigh (#2A292F)
Color texto: onSurfaceVariant (#C4C5D7)
Fuente: 11sp, medium
Forma: rounded-full
Padding: px-3 py-1.5
Tap: cambiar ejercicio activo
```

---

### 8. Footer Stats Bar

```
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl (12dp)
Padding: 12dp
Layout: Row, space-between, items-center
Margin top: 8dp
```

**Stats (Row, gap-6):**

| Stat | Label | Valor ejemplo |
|------|-------|---------------|
| SETS | "SETS" | 8 |
| VOL | "VOL" | 1,200 kg |
| TIEMPO | "TIEMPO" | 12m |

- Label: 8sp, outline, uppercase, tracking-wider
- Valor: 12sp, bold, onSurface
- Stack: Column, gap-1

**Botón fullscreen:**
- Icono: `open_in_full`
- Color: primary (#BAC3FF)
- Size: 24dp
- Tap: animated exit (scale 1.05 + fade out 200ms) → launch app en WorkoutPage

---

## Adaptive Layout (por ancho)

El widget se adapta según su ancho actual:

| Ancho | Comportamiento |
|-------|---------------|
| ≥ 320dp | Steppers en Row horizontal, chips visibles, stats bar completo |
| < 320dp | Steppers en Column vertical, chips ocultos, stats simplificados |
| ≥ 400dp | Layout más espaciado, timer más grande (36sp) |

**Implementación:**
- Constante `COMPACT_WIDTH_THRESHOLD = 320`
- `adaptLayoutToWidth(widthDp)` llamado desde `clampAndUpdate()` y `showExpanded()`
- Cambia `LinearLayout.orientation` del stepper container (HORIZONTAL ↔ VERTICAL)

---

## Animaciones

### Entry (bubble → expandido)
```
Tipo: Scale + Alpha
Scale: 0.92f → 1.0f
Alpha: 0.0f → 1.0f
Duración: 300ms
Interpolador: DecelerateInterpolator(2f)
```

### Exit (expandido → bubble)
```
Tipo: Scale + Alpha
Scale: 1.0f → 0.85f
Alpha: 1.0f → 0.0f
Duración: 200ms
Interpolador: AccelerateInterpolator(2f)
```

### Fullscreen exit
```
Tipo: Scale + Alpha (2-step)
1. Scale: 1.0f → 1.05f (150ms, AccelerateDecelerateInterpolator)
2. Alpha: 1.0f → 0.0f (200ms, AccelerateInterpolator)
Post: launch app intent → remove views
```

### Bubble entry
```
Tipo: Scale
Scale: 0.0f → 1.0f
Duración: 250ms
Interpolador: OvershootInterpolator(1.5f)
```

---

## Diferencias con Implementación Previa

| Aspecto | Antes (guía 16) | Rediseño actual |
|---------|-----------------|-----------------|
| Opacidad bubble | 70% (#B3131318) | 95% (#F2131318) |
| Opacidad expandido | 85% (#D9131318) | 97% (#F7131318) |
| Entry animation | OvershootInterpolator, scale 0.8 | DecelerateInterpolator(2f), scale 0.92 |
| Exit animation | scale 0.3 | scale 0.85, AccelerateInterpolator(2f) |
| Fullscreen exit | Instantáneo | Animated scale 1.05 + fade out |
| Close button | stopSelf() (kill service) | Colapsar a bubble (keep alive) |
| Resize | Solo vertical implícito | Horizontal + vertical con handles |
| Layout adaptivo | No existía | Stepper orientation switch at 320dp |
| Texto | English strings | Todo en español |
| Timer badge | No existía en bubble | Pill badge debajo del bubble |

---

## Plan de Implementación

> **Nota:** Las mejoras de opacidad, animaciones, resize adaptivo, close→minimize, y traducciones ya fueron implementadas como parte de los fixes de esta sesión. Este documento sirve como referencia de diseño objetivo para futuras iteraciones.

### Mejoras pendientes (opcionales)
1. **Timer badge en bubble**: Añadir pill con tiempo debajo del círculo colapsado
2. **Exercise chips**: Implementar scroll horizontal de ejercicios en el panel expandido
3. **Stats bar mejorado**: Añadir footer con SETS/VOL/TIEMPO summarizados
4. **Pulse border en bubble**: Animación de borde tertiary pulsante
5. **Drag handle**: Añadir barra visual de arrastre en la parte superior del panel
6. **Feedback al registrar serie**: Animación pulse + texto confirmación temporal
