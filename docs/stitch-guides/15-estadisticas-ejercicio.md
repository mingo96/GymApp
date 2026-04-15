# 15 — Estadísticas de Ejercicio Individual (Exercise Stats)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `c56db189a06c4f448e86ae95403977ea` |
| Nombre | Individual Exercise Statistics Sheet |
| Tipo | BottomSheet (obsidian panel, full-height) |
| Ancho diseño | 780 px (móvil) |
| Max-height sheet | 813 px |
| Navegación | BottomNav visible (Analytics activo) |

---

## Jerarquía Visual

```
Overlay  bg-black/60  backdrop-blur-sm
├── TopAppBar  fixed  h-16  bg-[#131318]/70  backdrop-blur-xl
│   ├── Icon "close"  primary  (cierra analíticas)
│   ├── Title "EXERCISE ANALYTICS"  Space Grotesk  uppercase  tracking-widest  bold  primary
│   └── Icon "more_vert"  primary
└── BottomSheet  obsidian-panel  rounded-t-[40px]  border-t outline-variant/20
    ├── DragHandle  w-12 h-1.5  outlineVariant/40  rounded-full
    └── Content  px-6  space-y-8
        ├── Header
        │   ├── Column
        │   │   ├── BodyPartBadge "PECHO"
        │   │   │   └── bg-secondaryContainer/30  tertiary-fixed-dim  10px  bold  tracking-widest  uppercase  rounded-full  px-3 py-1
        │   │   └── ExerciseName "Bench Press"  headline  4xl  bold  tracking-tight
        │   ├── IconBox  p-3  bg-surfaceContainerHigh  rounded-2xl
        │   │   └── Icon "fitness_center"  primary  3xl
        │   └── PeriodSelector  bg-surfaceContainerLow  rounded-2xl  p-1
        │       ├── Tab:Active "Mes"  bg-surfaceContainerHigh  primary  bold
        │       └── Tab:Inactive "3 Meses" / "6 Meses" / "Año"  outline
        ├── PRCard  rounded-24  bg-surfaceContainerLow  border primary/20
        │   ├── GlowDecoration  absolute  -top-24 -right-24  w-48 h-48  primary/10  blur-[80px]
        │   ├── TitleRow
        │   │   ├── Icon "military_tech"  tertiary  FILL=1
        │   │   └── Label "PERSONAL RECORD"  headline  bold  sm  tracking-widest  tertiary
        │   ├── MetricsGrid  2cols  gap-y-6 gap-x-4
        │   │   ├── "Best Weight"  →  "100 kg"  3xl  bold  headline
        │   │   ├── "Best Reps"    →  "8 reps"   3xl
        │   │   ├── "Best Volume"  →  "3,200 kg" 3xl
        │   │   └── "Est. 1RM"     →  "124 kg"   3xl  PRIMARY
        │   └── Footer  border-t outlineVariant/10  mt-6 pt-4
        │       ├── Date "Logrado el 15 Ago 2024"  outlineVariant  10px  italic
        │       └── Icon "trending_up"  outlineVariant
        ├── ProgressChart
        │   ├── Header "Progreso Temporal" + Legend (Peso=primary dot, Volumen=secondary dot)
        │   └── ChartCard  h-56  bg-surfaceContainerLow  rounded-3xl  p-6
        │       ├── SVG chart
        │       │   ├── GridLines  outlineVariant  dasharray=4  opacity-0.1
        │       │   ├── VolumeLine  secondary  opacity-0.3  stroke-2
        │       │   ├── WeightLine  primary  stroke-3
        │       │   ├── ActivePoint  circle  primary  r=5
        │       │   └── Tooltip  bg-surfaceContainerHigh  rounded-6  "95 kg"  white  10px bold
        │       └── TimelineAxis  ENE→MAY  10px  bold  outlineVariant (active=onSurface)
        ├── RepDistribution
        │   ├── Title "Distribución de Repeticiones"  headline  bold  lg
        │   └── BarChart  4cols  gap-3  h-32
        │       ├── Bar "1-5"    h=40%   bg-surfaceContainerHigh
        │       ├── Bar "6-10"   h=90%   kinetic-gradient (primary→primaryContainer)
        │       ├── Bar "11-15"  h=65%   bg-surfaceContainerHigh
        │       └── Bar "15+"    h=20%   bg-surfaceContainerHigh
        ├── RecentHistory
        │   ├── Header "Historial Reciente" + "VER TODO" link
        │   └── List  space-y-1
        │       ├── Row1  bg-surfaceContainerLow  p-4  rounded-2xl
        │       │   ├── Date "13 Abr" + "Martes"
        │       │   ├── Divider  h-8 w-1  outlineVariant/20
        │       │   ├── Sets "4x10 @80kg" + "+5% Vol" (tertiary)
        │       │   └── Volume "3.2k VOL"  font-black
        │       ├── Row2  bg-surfaceContainerLow/50  (fading opacity)
        │       └── Row3  bg-surfaceContainerLow/30  + "PR Peso" badge (primary)
        └── RelatedExercises
            ├── Title "Ejercicios Relacionados"  headline  bold  lg
            └── HorizontalScroll  gap-3
                ├── Card "Press Inclinado"  w-40  bg-surfaceContainerHigh  rounded-2xl
                │   ├── Image  h-24  rounded-xl  object-cover  opacity-60
                │   ├── Name  sm  bold
                │   └── BodyPart "PECHO ALTO"  tertiary-fixed-dim  10px  bold  uppercase
                ├── Card "Dips"
                └── Card "Fly con Cable"
```

---

## Componentes Detallados

### 1. Sheet Container (Obsidian Panel)

| Propiedad | Valor |
|-----------|-------|
| Fondo | `#131318` (background puro) |
| Esquinas | `rounded-t-[40px]` — 40 dp (más grande que standard 32) |
| Borde top | `outlineVariant/20` |
| Sombra | `0 -8px 32px rgba(0,0,0,0.6)` |
| Max-height | 813 px |
| Scroll | `overflow-y-auto` interno |

### 2. Body Part Badge

| Propiedad | Valor |
|-----------|-------|
| Fondo | `secondaryContainer/30` (~#6800E4 al 30%) |
| Texto | `tertiary-fixed-dim` (≈ #27E0A9), `10px`, `bold`, `tracking-widest`, `uppercase` |
| Padding | `px-3 py-1` |
| Redondeo | `rounded-full` |

### 3. Period Selector (TabRow)

| Propiedad | Valor |
|-----------|-------|
| Container | `bg-surfaceContainerLow`, `rounded-2xl`, `p-1` |
| Tab activo | `bg-surfaceContainerHigh`, `primary` texto, `xs`, `bold`, `uppercase`, `tracking-tighter`, `rounded-xl` |
| Tab inactivo | Sin fondo, `outline` texto, hover→`onSurface` |
| Opciones | Mes / 3 Meses / 6 Meses / Año |

### 4. PR Card

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerLow` (#1B1B20) |
| Redondeo | 24 dp |
| Borde | `primary/20` |
| Padding | `p-6` (24 dp) |
| Glow decorativo | Absoluto `-top-24 -right-24`, `w-48 h-48`, `primary/10`, `blur-[80px]`, `rounded-full` |

**Métricas grid (2 cols):**

| Métrica | Valor | Color especial |
|---------|-------|----------------|
| Best Weight | 100 kg | `onSurface` |
| Best Reps | 8 reps | `onSurface` |
| Best Volume | 3,200 kg | `onSurface` |
| **Est. 1RM** | **124 kg** | **`primary`** (#BAC3FF) |

| Propiedad métrica | Valor |
|-------------------|-------|
| Label | `10px`, `bold`, `uppercase`, `tracking-widest`, `outline` |
| Valor | `Space Grotesk`, `3xl` (~30sp), `bold` |
| Unidad | `sm`, `medium`, `outline` |

**Footer:**

| Propiedad | Valor |
|-----------|-------|
| Borde superior | `outlineVariant/10` |
| Texto fecha | `10px`, `medium`, `outlineVariant`, `italic` |
| Icono | `trending_up`, `outlineVariant`, `sm` |

### 5. Progress Chart

| Propiedad | Valor |
|-----------|-------|
| Card fondo | `surfaceContainerLow` |
| Card alto | 224 dp (h-56) |
| Card redondeo | `rounded-3xl` (24 dp) |
| Card padding | `p-6` |
| Grid lines | `outlineVariant`, dasharray=4, opacity-0.1 |
| Weight line | `primary` (#BAC3FF), stroke-3 |
| Volume line | `secondary` (#D2BBFF), stroke-2, opacity-0.3 |
| Active point | Círculo primary r=5 |
| Tooltip | `bg-surfaceContainerHigh`, rounded-6, texto blanco 10px bold |
| Legend dots | primary (Peso) y secondary (Volumen), `10px bold outline uppercase tracking-widest` |
| Timeline axis | `10px`, `bold`, `outlineVariant` (activo: `onSurface`) |

### 6. Rep Distribution (Bar Chart)

| Propiedad | Valor |
|-----------|-------|
| Layout | Grid 4 cols, `gap-3`, height 128dp (h-32) |
| Barra normal | `bg-surfaceContainerHigh`, `rounded-t-lg` |
| **Barra destacada (6-10)** | `kinetic-gradient` = `gradient(135deg, #BAC3FF 0%, #4361EE 100%)` |
| Label normal | `10px`, `bold`, `outline` |
| **Label destacada** | `10px`, `bold`, `primary` |
| Alturas ejemplo | 1-5:40%, **6-10:90%**, 11-15:65%, 15+:20% |

### 7. Recent History Rows

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerLow` con opacidad decreciente (100%, 50%, 30%) |
| Redondeo | `rounded-2xl` (16 dp) |
| Padding | `p-4` (16 dp) |
| Divider | `h-8 w-[1px] bg-outlineVariant/20` |
| Fecha | `xs`, `bold` / día `10px`, `outline`, `uppercase`, `medium` |
| Sets | `sm`, `bold` + peso `xs`, `outline`, `medium` |
| Progreso tag | `10px`, `bold`, `uppercase`, `tracking-tighter` — color varía: `tertiary-fixed-dim` (+% Vol), `outline` (Mantenido), `primary-fixed-dim` (PR Peso) |
| Volumen | `sm`, `font-black` (900), `tracking-tight` + unidad `10px`, `outline`, `bold` |

### 8. Related Exercise Cards

| Propiedad | Valor |
|-----------|-------|
| Ancho | 160 dp (w-40) |
| Fondo | `surfaceContainerHigh` (#2A292F) |
| Redondeo | `rounded-2xl` |
| Padding | `p-4` |
| Imagen | h-24, `rounded-xl`, `object-cover`, `opacity-60` |
| Nombre | `sm`, `bold`, `leading-tight` |
| Body part | `10px`, `bold`, `tertiary-fixed-dim`, `uppercase` |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Tap period tab | Cambia periodo del chart y métricas (Mes/3M/6M/Año) |
| Tap punto del chart | Muestra tooltip con valor exacto |
| Tap "Ver todo" historial | Navega a historial completo de ese ejercicio |
| Tap related exercise card | Abre estadísticas de ese ejercicio |
| Tap "close" (TopAppBar) | Cierra la vista de analíticas |
| Tap "more_vert" | Menú con opciones (exportar datos, comparar, etc.) |
| Scroll vertical | Sheet scrolleable internamente |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Exercise stats | No existe como pantalla dedicada | BottomSheet obsidian con corner 40dp |
| PR card | N/A | Card con glow decorativo, 4 métricas en grid 2×2, Est. 1RM en primary |
| Period selector | N/A | SegmentedButton (surfaceContainerLow → surfaceContainerHigh activo) |
| Progress chart | N/A | SVG dual-line (weight=primary, volume=secondary), tooltip interactivo |
| Rep distribution | N/A | Barchart 4 cols, barra destacada con kinetic-gradient |
| History rows | N/A | Opacidad decreciente (100→50→30%), tags de progreso colorizados |
| Related exercises | N/A | Horizontal scroll con image cards |
| Body part badge | N/A | secondaryContainer/30 con tertiary text |

---

## Plan de Implementación

1. **Crear `ExerciseStatsSheet` composable** — ModalBottomSheet con RoundedCornerShape(40.dp), bg=background, shadow obsidian
2. **ExerciseStatsHeader** — BodyPart badge + nombre del ejercicio + icon box + PeriodSelector (SegmentedButton M3)
3. **PRCard** — Card con glow decoration (Box + primary.copy(alpha=0.1f) blur), grid 2×2 de métricas, footer con fecha
4. **ProgressChart** — Canvas composable con 2 líneas (primary weight, secondary volume), tooltip composable posicionado dinámicamente, grid lines, timeline axis
5. **RepDistributionChart** — Row con 4 barras verticales (Box con weight=height, Modifier.fillMaxHeight(fraction)), barra destacada con Brush.linearGradient
6. **RecentHistoryList** — LazyColumn con rows cuya opacidad decrece, divider vertical, tags colorizados según progreso
7. **RelatedExercisesCarousel** — LazyRow con cards de 160dp, imagen con `alpha(0.6f)`, nombre + body part tag
8. **ExerciseStatsViewModel** — Carga datos por ejercicioId, permite cambio de periodo, calcula PRs, 1RM estimado, distribución de reps
