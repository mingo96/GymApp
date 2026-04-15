# 01 — Inicio (Home)

## Metadatos Stitch
- **Screen ID**: `04d9db0fe2fa4539b1a1c4f23ddeb989`
- **Título**: Inicio (Home) - New Nav
- **Dimensiones**: 780 × 2494 px
- **Variantes**: 5 variantes "New Nav" + 2 "Rediseño" + 1 original
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Screen (bg-background #131318, pb-32)
├── Header (fixed top, z-50, glass effect)
│   ├── Row: Avatar (32px circle, surfaceContainerHigh border)
│   └── NavTabs: [Inicio* | Entrenar | Perfil]
│
├── Main Content (pt-32, px-6)
│   ├── Section: Fecha + Título
│   │   ├── Date Label ("07/04/2026")
│   │   └── H1: "Plan de hoy"
│   │
│   ├── Section: Tarjeta Objetivo del Día (CTA)
│   │   └── Card con gradiente + contenido
│   │       ├── Left: Etiqueta "Objetivo" + Nombre rutina + Descripción
│   │       └── Right: Icono fitness_center + "Comenzar"
│   │
│   ├── Section: Calendario Semanal
│   │   ├── Header: Título + Fase
│   │   └── Grid (1-col, gap-3)
│   │       ├── DayRow: Completado (tertiary)
│   │       ├── DayRow: En progreso (primary, highlighted)
│   │       ├── DayRow: Mañana (secondary, opacity-80)
│   │       ├── DayRow: Descanso (outline-variant, opacity-60)
│   │       └── DayRow: Sin planear (outline-variant, opacity-40)
│   │
│   └── Section: Stats Rápidas (2-col grid)
│       ├── Card: "Racha" → 12 Días (tertiary)
│       └── Card: "Carga Semanal" → 82% (secondary)
│
└── FAB (fixed bottom-right, z-40)
    └── Button: "+" icon
```

---

## Componentes Detallados

### 1. Header con Glass Effect

```
Posición: fixed top-0, full width, z-50
Fondo: #131318/70 (70% opacity) + backdrop-blur-xl (20px)
Borde inferior: white/5 (1px)
Padding: pt-4 px-6
```

**Avatar (Row 1):**
- Tamaño: 32 × 32 dp
- Forma: Circle (rounded-full)
- Borde: 1px white/10
- Fondo: surfaceContainerHigh (#2A292F)
- Contenido: Foto de perfil del usuario

**Tabs de Navegación (Row 2):**
- Fuente: Space Grotesk, 12sp (text-xs), bold, uppercase
- Tracking: widest (0.1em)
- Padding bottom: 8dp (pb-2)

| Estado | Color texto | Decoración |
|--------|------------|------------|
| Activo | primary (#BAC3FF) | border-bottom 2px primary |
| Inactivo | onSurfaceVariant/60 | ninguna |
| Hover | onSurfaceVariant | transición suave |

> **Nota de implementación:** En la app actual esto ya existe en `RootPager.kt` con un `HorizontalPager` y tabs similares. Verificar que los tokens de color y tipografía coincidan exactamente.

---

### 2. Sección Fecha + Título

```
Margin bottom: 32dp (mb-8)
Layout: Column
```

**Date Label:**
- Texto: Fecha actual formateada "DD/MM/YYYY"
- Color: onSurfaceVariant (#C4C5D7)
- Tamaño: 14sp (text-sm)
- Peso: medium (500)
- Tracking: wide
- Transform: uppercase
- Margin bottom: 4dp

**Título "Plan de hoy":**
- Color: white
- Tamaño: 36sp (text-4xl)
- Peso: bold (700)
- Tracking: tight (-0.025em)
- Fuente: Space Grotesk

> **Mapeo actual:** En `HomePage.kt` ya existen estos elementos. El date label usa 11sp — debe ajustarse a 14sp. El título "Plan de hoy" usa 40sp — debe ajustarse a 36sp.

---

### 3. Tarjeta Objetivo del Día (CTA Principal)

Esta es la tarjeta prominente que invita al usuario a comenzar su entrenamiento planificado.

```
Container externo:
  - Forma: rounded-xl (12dp)
  - Fondo: surfaceContainerLow (#1B1B20)
  - Padding: 4dp (p-1)
  - Overflow: hidden
  - Cursor: pointer (clickable)

Overlay gradiente:
  - Posición: absolute inset-0
  - Gradiente: from primaryContainer/20 (#4361EE33) to transparent
  - Dirección: bottom-right
  - Opacidad: 50%

Card interior:
  - Fondo: surfaceContainer (#1F1F24)
  - Padding: 24dp (p-6)
  - Forma: rounded 11dp
  - Borde: 1px white/5
  - Sombra: shadow-2xl
  - Layout: Row, space-between, items-center
```

**Contenido izquierdo (Column, space-y-1):**

| Elemento | Texto ejemplo | Estilo |
|----------|--------------|--------|
| Etiqueta | "Objetivo" | tertiary (#27E0A9), bold, 10sp, uppercase, tracking-widest |
| Nombre | "Empuje A" | white, 24sp (text-2xl), bold, Space Grotesk |
| Descripción | "Pecho, Hombros y Tríceps • 75 min" | onSurfaceVariant (#C4C5D7), 14sp (text-sm) |

**Contenido derecho (Column, items-end, gap-2):**

| Elemento | Detalle |
|----------|---------|
| Contenedor icono | bg: primary/10 (#BAC3FF1A), border: 1px primary/20, rounded-xl (12dp), padding 12dp |
| Icono | Material: fitness_center, 30sp (text-3xl), color: primary (#BAC3FF) |
| Etiqueta | "Comenzar", onSurfaceVariant, 10sp, bold, uppercase, tracking-tighter |

> **Mapeo actual:** En `HomePage.kt` existe un "Objective Card" similar pero con diseño más simple. Debe rediseñarse con el gradiente, las capas y el estilo visual KP descrito arriba.

---

### 4. Calendario Semanal

```
Margin bottom: 48dp (mb-12)
```

**Header:**
- Layout: Row, space-between, items-center
- Margin bottom: 24dp (mb-6)
- Título: "Calendario Semanal" — 18sp (text-lg), bold, Space Grotesk, uppercase, tracking-tight
- Badge fase: "Fase: Volumen" — 12sp (text-xs), bold, secondary (#D2BBFF), uppercase, tracking-widest

**Grid de Días:**
- Layout: Column (grid-cols-1), gap-3

#### Estados de día:

##### A) Día Completado
```
Container:
  - Fondo: surfaceContainerLow (#1B1B20)
  - Padding: 16dp (p-4)
  - Forma: rounded-xl (12dp)
  - Hover: surfaceContainer (#1F1F24) con transición 300ms
  - Layout: Row, items-center, gap-4

Badge día:
  - Tamaño: 48 × 56 dp (w-12 h-14)
  - Forma: rounded-lg (8dp)
  - Fondo: surfaceContainerHigh (#2A292F)
  - Borde: 1px white/5
  - Día abreviado: 10sp, uppercase, bold, onSurfaceVariant
  - Número: 18sp, bold, Space Grotesk

Indicador:
  - Barra: 4 × 32 dp (w-1 h-8), rounded-full, bg: tertiary (#27E0A9)

Contenido:
  - Nombre rutina: 14sp, bold, white
  - Estado: "Completado" — 12sp, medium, tertiary (#27E0A9)

Icono:
  - check_circle (FILLED), color: tertiary
```

##### B) Día En Progreso (HOY) — Highlighted
```
Container:
  - Fondo: primaryContainer/10 (#4361EE1A)
  - Borde: 1px primary/20 (#BAC3FF33)
  - Sombra: 0 0 20px rgba(67,97,238,0.1) — glow sutil
  - Padding: 16dp
  - Forma: rounded-xl

Badge día:
  - Fondo: primaryContainer (#4361EE)
  - Texto: white
  - Día: 10sp, uppercase, bold, opacity-80
  - Número: 18sp, bold, Space Grotesk

Indicador:
  - Barra: bg primary (#BAC3FF)

Contenido:
  - Nombre: 14sp, bold, white
  - Estado: "En progreso" — 12sp, medium, primaryFixedDim (#BAC3FF)

Icono:
  - radio_button_checked, color: primary, animate-pulse
```

##### C) Día Futuro (Mañana)
```
Container:
  - Fondo: surfaceContainerLowest (#0E0E13)
  - Borde: 1px white/5
  - Opacidad: 80%

Badge día:
  - Fondo: surfaceContainerHigh

Indicador:
  - Barra: bg secondary (#D2BBFF)

Contenido:
  - Nombre: 14sp, bold, white
  - Estado: "Mañana" — 12sp, onSurfaceVariant

Acción:
  - Botón "+": 32×32dp, rounded-full, bg white/5
  - Hover: bg white/10
  - Icono: Material "add", 14sp
```

##### D) Día Descanso
```
Igual que futuro pero:
  - Opacidad: 60%
  - Indicador: bg outlineVariant (#444655)
  - Nombre: "Descanso" — onSurfaceVariant (no white), italic
  - Sin estado adicional
  - Acción: botón "+", icono color outline (#8E8FA1)
```

##### E) Día Sin Planear
```
Igual que descanso pero:
  - Opacidad: 40%
  - Nombre: "Sin planear" — onSurfaceVariant, italic
```

> **Mapeo actual:** En `HomePage.kt` existe un `RutinAppCalendar` que muestra un calendario mensual con fases. El diseño Stitch propone reemplazarlo por una **vista semanal vertical** con rows para cada día. Es un cambio de paradigma visual significativo.

---

### 5. Stats Rápidas

```
Layout: Grid 2 columnas, gap-4
```

**Card individual:**
```
Fondo: surfaceContainerLow (#1B1B20)
Padding: 24dp (p-6)
Forma: rounded-2xl (16dp)
Borde: 1px white/5
```

| Card | Etiqueta | Valor | Unidad | Color acento |
|------|----------|-------|--------|-------------|
| Racha | "Racha" | "12" | "Días" | tertiary (#27E0A9) |
| Carga | "Carga Semanal" | "82" | "%" | secondary (#D2BBFF) |

**Etiqueta:**
- 10sp, bold, uppercase, tracking-widest, onSurfaceVariant
- Margin bottom: 8dp

**Valor:**
- 36sp (text-4xl), bold, Space Grotesk, white

**Unidad:**
- 14sp, bold, uppercase, color acento, margin-bottom 4dp (alineado al bottom del valor)

> **Mapeo actual:** No existe equivalente en `HomePage.kt`. Es un componente nuevo que requiere datos de racha (días consecutivos) y carga semanal (% de ejercicios completados vs planificados).

---

### 6. FAB (Floating Action Button)

```
Posición: fixed, bottom 40dp (bottom-10), right 24dp (right-6), z-40
Tamaño: 56 × 56 dp (w-14 h-14)
Fondo: gradiente from primary (#BAC3FF) to primaryContainer (#4361EE), dirección bottom-right
Forma: rounded-2xl (16dp)
Sombra: 0 8px 24px rgba(67,97,238,0.4) — glow azul
Contenido: Material icon "add", 30sp (text-3xl), color onPrimary (#00218D)
Animación: scale-95 en press, transición transform
```

> **Mapeo actual:** Existe `FABComposable.kt` que tiene un FAB expandible con sub-botones. El diseño Stitch muestra un FAB simple "+" sin expansión visible. Decidir si mantener la funcionalidad expandible del actual o simplificar.

---

## Comportamiento e Interacciones

1. **Header glass**: Se mantiene fijo con blur al hacer scroll. Los contenidos pasan por detrás con transparencia.
2. **Tabs**: Tap para cambiar de página (HorizontalPager swipe). Tab activo tiene underline animado.
3. **Tarjeta Objetivo**: Al hacer tap, inicia el entrenamiento (navega a sheet de Entrenamiento Activo).
4. **Días del calendario**: 
   - Día completado: tap para ver resumen del entrenamiento
   - Día actual: tap para iniciar/continuar entrenamiento
   - Día futuro: tap en "+" para asignar rutina
5. **Stats cards**: No interactivas en el diseño (potencialmente expandibles en futuro).
6. **FAB**: Tap para mostrar opciones rápidas de creación.

---

## Diferencias con Implementación Actual

| Aspecto | Actual (`HomePage.kt`) | Diseño Stitch |
|---------|----------------------|---------------|
| Layout | LazyColumn con calendario mensual | Column con vista semanal |
| Calendario | Componente `RutinAppCalendar` (mensual, grid) | Lista vertical de 5-7 días con estados |
| Date label | 11sp | 14sp |
| Título | 40sp | 36sp |
| Tarjeta objetivo | Simple con icono | Doble capa con gradiente y glow |
| Stats | No existen | 2 cards: Racha + Carga Semanal |
| Separador fecha | DateRangePicker colapsable | No existe |
| FAB | Expandible con sub-botones | Simple "+" |

---

## Plan de Implementación

### Paso 1: Actualizar Header/Tabs en RootPager.kt
- Verificar tokens de color y tipografía
- Añadir avatar del usuario en la esquina superior izquierda
- Mantener la funcionalidad swipe del HorizontalPager

### Paso 2: Rediseñar la sección superior de HomePage.kt
- Ajustar tamaños de fuente (date 14sp, título 36sp)
- Rediseñar la tarjeta objetivo con la estética de doble capa + gradiente

### Paso 3: Implementar Calendario Semanal
- Crear un nuevo composable `WeeklyCalendar` que:
  - Muestre los próximos 5-7 días
  - Use estados: completado, en progreso, futuro, descanso, sin planear
  - Cada día es un `DayRow` composable con badge, indicador, contenido y acción
- Reemplazar o complementar el calendario mensual existente

### Paso 4: Implementar Stats Cards
- Crear composable `QuickStatsRow` con grid 2 columnas
- Requiere: función para calcular racha de días + porcentaje de carga semanal
- Datos: contar entrenamientos consecutivos, calcular ratio completados/planificados

### Paso 5: Ajustar FAB
- Simplificar a botón "+" con gradiente primary→primaryContainer
- Decidir si mantener la expansión actual como funcionalidad oculta

### Paso 6: Pulir
- Glass effect del header (blur + transparencia)
- Animaciones de transición (pulse en día actual, scale en FAB)
- Glow shadows en tarjeta objetivo y día actual
