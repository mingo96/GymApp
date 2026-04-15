# 14 — Historial de Entrenamientos (Workout History)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `8ea15c2f5e9a471aac597c4f03dd1061` |
| Nombre | Workout History |
| Tipo | Page (full screen, scrollable) |
| Ancho diseño | 780 px (móvil) |
| Altura | ~2874 px (scroll largo) |
| Navegación | BottomNav con tab "History" activo |

---

## Jerarquía Visual

```
Page  bg-background  pb-32
├── TopAppBar  fixed  h-16  bg-[#131318]/70  backdrop-blur-xl
│   ├── Row
│   │   ├── Icon "menu"  indigo-200
│   │   └── Title "HISTORIAL"  Space Grotesk  uppercase  tracking-widest  bold  xl  indigo-200
│   └── AvatarCircle  32dp  ring-2 primaryContainer/30
│       └── ProfileImage  object-cover
├── Main  pt-24  px-6
│   ├── HeroHeader  mb-8
│   │   ├── Title "Historial de entrenamientos"  4xl  bold  tracking-tight
│   │   └── Subtitle  onSurfaceVariant/70  sm
│   ├── StatsSummaryRow  grid-3cols  gap-3  mb-10
│   │   ├── GlassCard "Total"  → "124"
│   │   ├── GlassCard "Esta semana"  → "4"
│   │   └── GlassCard "Racha"  → "12 DÍAS"
│   ├── RoutineSuggestions  mb-10
│   │   ├── Header "Nuevas rutinas" + "Ver todas"
│   │   └── HorizontalScroll  gap-4
│   │       ├── RoutineCard "Hypertrophy Max"  gradient CTA
│   │       └── RoutineCard "Pull Power V2"  outlined CTA
│   └── ChronologicalList  space-y-8
│       ├── MonthGroup "ABRIL 2026"
│       │   ├── WorkoutCard:Completed "Empuje A"
│       │   ├── WorkoutCard:InProgress "Pierna Enfoque"
│       │   └── WorkoutCard:Planned "Tracción B"
│       └── MonthGroup "MARZO 2026"
│           └── EmptyState "No se encontraron más sesiones"
└── BottomNav  (History tab activo)
```

---

## Componentes Detallados

### 1. TopAppBar

| Propiedad | Valor |
|-----------|-------|
| Fondo | `#131318/70` + `backdrop-blur-xl` |
| Altura | 64 dp |
| Sombra | `shadow-2xl shadow-black/20` |
| Icono menú | `menu`, `indigo-200` (≈ primary) |
| Título | "HISTORIAL", `Space Grotesk`, `uppercase`, `tracking-widest`, `bold`, `xl`, `indigo-200` |
| Avatar | 32×32 dp, `rounded-full`, `ring-2 ring-primaryContainer/30` |

### 2. Stats Summary (Glass Cards)

| Propiedad | Valor |
|-----------|-------|
| Layout | Grid 3 columnas, `gap-3` |
| Card fondo | `glass-card`: `rgba(27,27,32,0.6)` + `backdrop-filter: blur(12px)` |
| Card alto | 112 dp (h-28) |
| Card redondeo | `rounded-2xl` (16 dp) |
| Card padding | `p-4` (16 dp) |
| Card borde | `white/5` |
| Label | `10px`, `bold`, `uppercase`, `tracking-widest`, `onSurfaceVariant` |
| Valor | `3xl` (~30sp), `bold`, `tertiary-fixed-dim` (≈ tertiary #27E0A9), `Space Grotesk` |
| Unidad (Racha) | `10px`, `tertiary-fixed-dim/60`, `bold`, `uppercase` |

### 3. Routine Suggestion Cards

| Propiedad | Valor |
|-----------|-------|
| Layout | HorizontalScroll con `overflow-x-auto`, padding extendido `-mx-6 px-6` |
| Card ancho mín | 240 dp |
| Card fondo | `surfaceContainerLow` (#1B1B20) |
| Card redondeo | `rounded-2xl` |
| Card padding | `p-5` (20 dp) |
| Card borde | `white/5` |
| Icono decorativo | Absoluto `-right-4 -top-4`, `opacity-10`, hover→`opacity-20`, `text-8xl` |
| Título | `lg`, `bold`, `Space Grotesk` |
| Duración | `xs`, `onSurfaceVariant`, con icono `timer` 14sp |
| **CTA Primary** | `gradient(primary→primaryContainer)`, `onPrimary`, `bold`, `rounded-xl`, `xs uppercase tracking-widest` |
| **CTA Secondary** | `bg-surfaceContainerHighest`, `primary` texto, mismas props |

### 4. Workout Cards (3 variantes)

**Variante: COMPLETADO**

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainer` (#1F1F24) |
| Redondeo | `rounded-2xl` |
| Padding | `p-5` (20 dp) |
| Hover | `bg-surfaceContainerHigh` |
| Título | `xl` (~20sp), `bold`, `Space Grotesk`, `primary` (#BAC3FF) |
| Fecha | `xs`, `onSurfaceVariant`, `medium` |
| Badge | `bg-tertiary/10`, `tertiary` texto, `10px`, `font-black`, `tracking-widest`, `rounded-full`, `border tertiary/20` |
| Métricas | 3 columnas: Duración/Series/Volumen |
| Métrica label | `10px`, `bold`, `uppercase`, `tracking-tighter`, `onSurfaceVariant` |
| Métrica valor | `base`, `bold`, `Space Grotesk` |

**Variante: EN CURSO**

| Propiedad | Valor |
|-----------|-------|
| Border-left | `4px solid secondaryContainer` (#6800E4) |
| Título | `xl`, `bold`, `onSurface` (no primary) |
| Badge | `bg-secondaryContainer/20`, `secondary` texto, `border secondaryContainer/30` |
| Métricas | 2 columnas: Actual/Series restantes |

**Variante: PLANIFICADO**

| Propiedad | Valor |
|-----------|-------|
| Opacidad | `0.7` |
| Título | `xl`, `bold`, `onSurface` |
| Badge | `bg-transparent`, `onSurfaceVariant` texto, `border outlineVariant` |
| Métricas | 2 columnas: Estimado/Ejercicios |

### 5. Month Group Header

| Propiedad | Valor |
|-----------|-------|
| Texto | "ABRIL 2026", `xs`, `font-black` (900), `tracking-[0.2em]`, `onSurfaceVariant/40`, `uppercase` |
| Margin bottom | `mb-4` (16 dp) |

### 6. Empty State

| Propiedad | Valor |
|-----------|-------|
| Container | `surfaceContainerLowest` (#0E0E13), `border white/5`, `rounded-2xl`, `p-8`, centrado |
| Texto | `onSurfaceVariant`, `sm`, `medium` |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Tap workout card | Navega al detalle del entrenamiento |
| Tap "Iniciar" en routine card | Inicia sesión de entrenamiento con esa rutina |
| Tap "Ver todas" | Navega a la lista completa de rutinas |
| Scroll vertical | Lista cronológica agrupada por mes, lazy loading |
| Hover workout card | Transición `surfaceContainer` → `surfaceContainerHigh` |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Historial | Lista simple de workouts | Página completa con hero, stats, sugerencias, cronología |
| Stats summary | No hay resumen KPI | 3 glass cards (Total, Semana, Racha) |
| Routine suggestions | No integrado en historial | Horizontal scroll con CTA gradient |
| Workout card variantes | Una sola | 3 variantes: Completado (primary), En Curso (secondary border-left), Planificado (opacity 0.7) |
| Badges de estado | Texto simple | Chips con colores semánticos y bordes per tipo |
| Agrupación por mes | No existe | Headers de mes con tracking-[0.2em] |
| Empty state | N/A | Card minimalista con mensaje centrado |
| TopAppBar | Standard | Blur glass con avatar + menú hamburger |

---

## Plan de Implementación

1. **Crear `WorkoutHistoryPage` composable** — Scaffold con glass TopAppBar (menú + avatar), LazyColumn con secciones
2. **StatsSummaryRow** — Row de 3 glass cards con `Modifier.background(brush, shape)` + backdrop blur simulado
3. **RoutineSuggestionCarousel** — LazyRow con cards mín 240dp, icono decorativo `alpha(0.1f)`, CTA gradient
4. **WorkoutHistoryCard** — Componente con sealed class para 3 estados (`Completed`, `InProgress`, `Planned`), badge con colores semánticos
5. **MonthGroupHeader** — Section header sticky o simple Text con formato fecha mes/año, uppercase
6. **WorkoutHistoryViewModel** — Paginación por mes, estados de filtro, carga lazy de meses anteriores
