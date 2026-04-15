# 04 — Entrenamiento Activo (Active Workout)

## Metadatos Stitch
- **Screen ID**: `ef9c18ec5e724cbeba772ef1b12179f0`
- **Título**: Entrenamiento Activo (Training) - Sheet/Mobile
- **Dimensiones**: 780 × 2238 px
- **Variantes**: 5 variantes (Sheet, Mobile, Training)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Screen (bg-surface #131318, fullscreen session)
├── TopAppBar (fixed top, z-50, glass effect)
│   ├── Left: Timer icon + "TRAINING_SESSION" label
│   └── Right: Botón "FINISH" (error colors)
│
├── Main Content (pt-20, pb-32, px-4)
│   ├── Header: Nombre entrenamiento + Timer grande
│   │   ├── H1: "Empuje A (Upper Push)"
│   │   └── Timer: "00:42:15" (primary, digital glow)
│   │
│   ├── Exercise Carousel (horizontal scroll, snap)
│   │   ├── Card: Ejercicio activo (primaryContainer)
│   │   ├── Card: Ejercicio pendiente (surfaceContainerLow, opacity-60)
│   │   └── Card: "+ Add Exercise" (dashed border)
│   │
│   ├── Active Exercise Detail (card grande)
│   │   ├── Header: Nombre + tag grupo muscular + "Last session" info
│   │   ├── Set Table (grid 6 columnas)
│   │   │   ├── Headers: SET | PREVIOUS | KG | REPS | STATUS
│   │   │   ├── Row Completada (tertiary check)
│   │   │   ├── Row Activa (primary, highlighted, ring)
│   │   │   └── Row Pendiente (opacity-40)
│   │   └── Botón "+ Add Set" (dashed border)
│   │
│   └── Background Image (decorativo, grayscale, opacity-20)
│
└── Bottom Action Bar (fixed bottom, z-50, glass effect)
    ├── Rest Timer: dot + "REST TIMER" label + "01:28" countdown
    └── Controls: [PREV] [edit_note button] [NEXT]
```

---

## Componentes Detallados

### 1. TopAppBar (Modo Sesión)

```
Posición: fixed top-0, full width, z-50
Fondo: #131318/70 + backdrop-blur-xl
Altura: 64dp (h-16)
Layout: Row, space-between, items-center, px-6
```

**Lado izquierdo (Row, gap-3):**
- Icono: timer, color primary (#BAC3FF)
- Label: "TRAINING_SESSION"
  - Fuente: Space Grotesk, 14sp, bold, uppercase, tracking-widest
  - Color: primary (#BAC3FF)

**Botón FINISH (Right):**
```
Layout: Row, items-center, gap-1
Fondo: errorContainer/20 (#93000A33)
Padding: px-3 py-1.5
Forma: rounded-lg (8dp)
Color texto: error (#FFB4AB)
Hover: errorContainer/40
Transición: colors
```
- Label: "FINISH" — Space Grotesk, 12sp, bold, tracking-tighter
- Icono: close, 14sp

> **Mapeo actual:** No existe un TopAppBar dedicado para sesiones de entrenamiento. El entrenamiento activo se muestra actualmente dentro del mismo TrainPage sin header especial.

---

### 2. Header & Live Timer

```
Layout: Center, Column, space-y-2
```

**Nombre del entrenamiento:**
- Texto: "Empuje A (Upper Push)"
- Fuente: Space Grotesk, 24sp (text-2xl), bold, tracking-tight
- Color: onSurface (#E4E1E9)
- Alineación: center

**Timer gran formato:**
```
Texto: "00:42:15" (HH:MM:SS)
Fuente: Space Grotesk, 48sp (text-5xl), font-black (900)
Color: primary (#BAC3FF)
Tracking: tighter (-0.05em)
Efecto: text-shadow 0 0 20px rgba(186,195,255,0.3) — "digital glow"
Alineación: center
```

> **Mapeo actual:** En la app actual no existe un timer visible durante el entrenamiento. El tiempo se registra pero no se muestra prominentemente.

---

### 3. Exercise Carousel

```
Layout: Row horizontal, overflow-x scroll, snap-x
Gap: 16dp (gap-4)
Padding bottom: 16dp
```

#### Card Ejercicio Activo:
```
Ancho: 256dp (w-64)
Padding: 16dp (p-4)
Forma: rounded-2xl (16dp)
Fondo: primaryContainer (#4361EE)
Color texto: onPrimaryContainer (#F4F2FF)
Sombra: shadow-xl
Overflow: hidden
snap-center, flex-shrink-0
```

**Blob decorativo:**
- absolute -right-4 -top-4
- 96×96dp, white/10, rounded-full, blur-2xl
- Hover: white/20

**Contenido (Column, space-between, gap-6):**
- Row superior: Tag grupo muscular + "2/4 sets"
  - Tag: "PECHO" — bg white/20, px-2 py-0.5, rounded, 10sp, font-black, uppercase
  - Sets: 12sp, medium, opacity-80
- Nombre: Space Grotesk, 20sp, bold, leading-tight

#### Card Ejercicio Pendiente:
```
Ancho: 256dp
Padding: 16dp
Forma: rounded-2xl
Fondo: surfaceContainerLow (#1B1B20)
Borde: 1px outlineVariant/10
Opacidad: 60%
snap-center, flex-shrink-0
```

- Tag: surfaceContainerHighest bg, onSurfaceVariant text
- Sets: "0/3 sets" — onSurfaceVariant
- Nombre: onSurface

#### Card "+ Add Exercise":
```
Ancho: 192dp (w-48)
Padding: 16dp
Forma: rounded-2xl
Borde: 2px dashed outlineVariant/30
Layout: Column, center, gap-3
Hover: surfaceContainerLow, transition
```

- Botón circular: 40×40dp, surfaceContainerHigh, icono "add" primary
  - Hover: scale-110
- Label: "Add Exercise" — 12sp, bold, uppercase, tracking-widest, color outline

---

### 4. Active Exercise Detail

```
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-3xl (24dp)
Padding: 24dp (p-6)
Content: Column, space-y-6
```

**Header (Row, space-between, items-end):**

| Posición | Elemento | Estilo |
|----------|----------|--------|
| Left | Nombre: "Bench Press" | Space Grotesk, 30sp (text-3xl), bold, onSurface |
| Left | Tag: "PECHO" | tertiaryContainer/30 bg, tertiaryFixed text, 10sp, font-black, uppercase |
| Left | "Last session: 100kg x 8 reps" | 14sp, onSurfaceVariant; icono history 14sp; valor bold onSurface |
| Right | Botón info | 40dp, surfaceContainerHigh, rounded-xl, icono info onSurfaceVariant |

#### Set Logging Table (Grid 6 columnas):

**Headers:**
```
Grid: 6 columnas, gap-2, px-2
Fuente: 10sp, font-black, uppercase, tracking-widest
Color: outline (#8E8FA1)
Columnas: SET (1) | PREVIOUS (2) | KG (1) | REPS (1) | STATUS (1)
```

**Row Completada:**
```
Grid: 6 columnas, gap-2, items-center
Fondo: surfaceContainerLowest/50
Padding: 12dp (p-3)
Forma: rounded-2xl
Borde: 1px white/2%
```
- SET: font-headline, bold, onSurfaceVariant
- PREVIOUS: "95kg x 8" — 12sp, medium, outline
- KG input: 48×36dp, surfaceContainerHigh, no border, rounded-lg, center, 14sp, bold, **tertiary** (#27E0A9)
- REPS input: igual, tertiary
- STATUS: 32×32dp circle, tertiary/20 bg, check_circle (filled) tertiary

**Row Activa:**
```
Fondo: surfaceContainerHigh (#2A292F)
Borde: 2px primary/20
Ring: 4px primary/5 (glow sutil)
```
- SET: **primary** color
- KG/REPS inputs: surfaceContainerHighest bg, **primary** color, focus ring-2 primary
- STATUS: 32×32dp, border 2px primary/30, icono radio_button_unchecked primary
  - Hover: bg primary/10

**Row Pendiente:**
```
Opacidad: 40%
Sin fondo
```
- KG/REPS: surfaceContainerLowest bg, placeholder "---", focus ring outline
- STATUS: 32×32dp, border 2px outlineVariant/30, vacío

**Botón "+ Add Set":**
```
Ancho: 100%
Padding: 16dp (py-4)
Forma: rounded-2xl
Borde: 1px dashed outlineVariant/50
Fuente: 12sp, font-black, uppercase, tracking-widest
Color: outline
Hover: surfaceContainerHigh bg, onSurface text
Layout: Row, center, gap-2
Icono: "add" 14sp
```

---

### 5. Bottom Action Bar

```
Posición: fixed bottom-0, full width, z-50
Fondo: #131318/70 + backdrop-blur-xl
Borde superior: white/5
Padding: px-4 pt-2 pb-8 (safe area)
Sombra: 0 -8px 32px rgba(0,0,0,0.5)
Layout: Column, gap-4
```

**Rest Timer Indicator:**
```
Row, space-between, items-center, px-2
```
- Left: Dot animado (8dp, tertiary, animate-pulse) + "REST TIMER" (Space Grotesk, 10sp, bold, uppercase, tracking-widest, outline)
- Right: "01:28" — Space Grotesk, 18sp, font-black, tertiary, tracking-tighter

**Controls (Row, space-between, gap-4):**

| Posición | Elemento | Estilo |
|----------|----------|--------|
| Left (flex-1) | PREV | Column, center. Icono: chevron_left. Label: 10sp, medium, tracking-tight. Color: slate-500 → white on hover |
| Center | edit_note button | 56×56dp (w-14 h-14), surfaceContainerHigh, rounded-2xl, icono edit_note onSurface. Hover: surfaceContainerHighest. Active: scale-95 |
| Right (flex-1) | NEXT | Column, center. **Fondo: primaryContainer/20 (#4361EE33)**. Text: primary (#BAC3FF). rounded-xl, py-2. Active: scale-95 |

---

## Comportamiento e Interacciones

1. **Timer**: Se actualiza cada segundo. Digital glow crea sensación de cronómetro digital.
2. **Exercise Carousel**: Swipe horizontal entre ejercicios. El activo está resaltado en primaryContainer.
3. **Set rows**: 
   - Tap en input de KG/REPS para editar
   - Tap en botón de STATUS (radio_button) para marcar como completado → cambia a check_circle tertiary
   - La row siguiente se activa automáticamente
4. **PREV/NEXT**: Navega entre ejercicios del carousel
5. **edit_note**: Abre opciones de edición (notas, modificar ejercicio)
6. **Rest Timer**: Cuenta regresiva entre sets. Se activa automáticamente al completar un set.
7. **FINISH**: Muestra diálogo de confirmación → finaliza el entrenamiento
8. **Add Exercise**: Abre sheet de Lista de Ejercicios para añadir al workout
9. **Add Set**: Añade una fila vacía al final de la tabla de sets

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Vista | Integrada en TrainPage | Fullscreen dedicado |
| Timer | No visible | Prominente, digital glow |
| Ejercicios | Lista vertical | Carousel horizontal con snap |
| Sets | Diálogo (EditSetDialog) | Tabla inline editable |
| Rest timer | No existe | Barra fija inferior |
| Navegación | Scroll | PREV/NEXT con controles |
| Add Exercise | Desde sheet separado | Botón inline en carousel |
| TopAppBar | Estándar | Modo sesión con FINISH |

---

## Plan de Implementación

### Paso 1: Crear Activity/Screen dedicada
- Crear `ActiveWorkoutScreen.kt` como composable fullscreen
- Se navega desde TrainPage cuando se inicia un entrenamiento
- Implementar TopAppBar con timer icon y botón FINISH

### Paso 2: Implementar Timer
- Crear `WorkoutTimer` composable con cronómetro en vivo
- Efecto digital glow con `shadow` en Compose
- Formato HH:MM:SS

### Paso 3: Exercise Carousel
- `LazyRow` con `snap` (flingBehavior)
- Cards de ejercicio: activo (primaryContainer) vs pendiente (surfaceContainerLow)
- Card "Add Exercise" al final

### Paso 4: Set Logging Table
- Grid composable con 6 columnas
- TextFields editables para KG y REPS
- 3 estados por row: completada (tertiary), activa (primary ring), pendiente (opacity-40)
- Botón status para marcar completado
- Botón "Add Set"

### Paso 5: Bottom Action Bar
- Rest timer con countdown
- Controles PREV/NEXT/Edit
- Glass effect con blur

### Paso 6: Integración
- Conectar con WorkoutViewModel existente
- Persistir sets al editar (Room DB)
- Auto-advance al completar set
- Manejar lifecycle (workout persiste si app se cierra)
