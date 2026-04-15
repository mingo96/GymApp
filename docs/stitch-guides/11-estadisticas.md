# 11 — Estadísticas (Statistics Overview)

## Metadatos Stitch
- **Screen ID**: `717b11abf3a84354bfc77798638e321d`
- **Título**: Estadísticas (Performance Vault) - Mobile
- **Dimensiones**: 780 × ~4294 px (pantalla larga con scroll)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Screen (bg-surface #131318)
├── TopAppBar (glass, "PERFORMANCE VAULT" title)
│
├── Background Image (gym, grayscale, opacity-40, gradient fade)
│
├── Statistics Sheet (surfaceContainerLow, rounded-t-32, -mt-12, z-10)
│   ├── Drag Handle
│   │
│   ├── Header
│   │   ├── "Estadísticas" (headline, 3xl)
│   │   └── Period Chips: Semana* | Mes | 3 Meses | Año | Todo
│   │
│   ├── KPI Row (horizontal scroll, 4 cards)
│   │   ├── Entrenamientos: 124 (+12%)
│   │   ├── Volumen Total: 8.4k kg (mini bar chart)
│   │   ├── Duración Media: 75 min
│   │   └── Racha Activa: 12 Días
│   │
│   ├── Charts Section
│   │   ├── Volumen Semanal (area chart, polyline)
│   │   ├── Frecuencia de Entrenamiento (bar chart, L-D)
│   │   └── Distribución Muscular (progress bars)
│   │
│   ├── Top Ejercicios (lista ranked 1-3)
│   │   ├── 1. Bench Press — PR 100kg
│   │   ├── 2. Squat — PR 140kg
│   │   └── 3. Pull-up — PR +20kg
│   │
│   └── Records Personales (grid 2 cols)
│       ├── Deadlift: 180kg x1 (¡Ayer!)
│       └── Military Press: 65kg x5 (12 Abr)
│
└── BottomNavBar (5 tabs: LIFT/STATS*/VAULT/GOALS/GEAR)
```

---

## Componentes Detallados

### 1. Header + Period Filter

```
Padding: px-6 pt-4 pb-6
```

- Título: "Estadísticas" — Space Grotesk, 30sp (text-3xl), bold, onSurface

**Period Chips (horizontal scroll, gap-2, mt-6):**

| Chip | Estado | Fondo | Color | Fuente |
|------|--------|-------|-------|--------|
| Semana | **Activo** | primaryContainer (#4361EE) | onPrimaryContainer (#F4F2FF) | semibold, 14sp |
| Mes | Inactivo | surfaceContainerHighest | onSurfaceVariant | medium, 14sp |
| 3 Meses | Inactivo | surfaceContainerHighest | onSurfaceVariant | medium, 14sp |
| Año | Inactivo | surfaceContainerHighest | onSurfaceVariant | medium, 14sp |
| Todo | Inactivo | surfaceContainerHighest | onSurfaceVariant | medium, 14sp |

```
Padding: px-5 py-2
Forma: rounded-full
Whitespace: nowrap
Hover inactivo: surfaceVariant
Active: scale-95
```

---

### 2. KPI Row

```
Layout: Row, horizontal scroll, gap-4, px-6
Margin bottom: 32dp (mb-8)
```

**KPI Card:**
```
Min-width: 160dp
Fondo: white/5 + backdrop-blur-md (glass)
Forma: rounded-2xl (16dp)
Padding: 16dp (p-4)
Borde: 1px white/10
```

| KPI | Icono | Color icono | Valor | Label | Extra |
|-----|-------|-------------|-------|-------|-------|
| Entrenamientos | fitness_center | primary | "124" | "ENTRENAMIENTOS" | "+12%" tertiary, bold |
| Volumen Total | weight | primary | "8.4k kg" | "VOLUMEN TOTAL" | Mini bar chart (3 bars, primary) |
| Duración Media | schedule | primary | "75 min" | "DURACIÓN MEDIA" | — |
| Racha Activa | local_fire_department (FILL) | tertiary | "12 Días" | "RACHA ACTIVA" | — |

**Valor:** Space Grotesk, 24sp (text-2xl), bold, onSurface
**Label:** 10sp, uppercase, tracking-wider, bold, outline
**Icono:** 20sp (text-xl)

---

### 3. Charts

Todos los gráficos usan:
```
Container: surfaceContainer (#1F1F24), rounded-2xl, p-6
Height: 192dp (h-48)
```

#### Volumen Semanal (Area Chart)
- **Header:** "Volumen Semanal" (18sp, headline, bold) + "Últimos 7 días" (12sp, primary, bold)
- **Chart:** SVG polyline stroke primaryContainer (#4361EE), stroke-width 3
- **Area fill:** gradient primary to transparent, opacity 10%
- **Peak label:** "Peak: 1,240 kg" — primaryContainer/20 bg, primary text, 10sp, bold, rounded, absolute top-4 right-4
- **Active dot:** circle r=4, fill primaryContainer

#### Frecuencia de Entrenamiento (Bar Chart vertical)
- **Header:** "Frecuencia de Entrenamiento" — headline, 18sp, bold
- **Bars (7, L-D):**
  ```
  Container: surfaceContainerHighest, rounded-full, h-24
  Fill: primaryContainer (active days) / surfaceVariant (rest days)
  ```
  - L: 60%, M: 80%, X: 20%, J: 95%, V: 70%, S: 10%, D: 15%
- **Labels:** 10sp, bold, outline (L, M, X, J, V, S, D)

#### Distribución Muscular (Progress Bars)
- **Header:** "Distribución Muscular" — headline, 18sp, bold

| Músculo | Porcentaje | Color barra | Ancho |
|---------|-----------|-------------|-------|
| Pecho | 45% | primaryContainer (#4361EE) | 45% |
| Espalda | 30% | secondaryContainer (#6800E4) | 30% |
| Piernas | 20% | tertiaryContainer (#007F5D) | 20% |
| Core | 5% | errorContainer (#93000A) | 5% |

```
Track: h-2, surfaceContainerHighest, rounded-full
Fill: h-full, color correspondiente, rounded-full
Label: 12sp, bold, row space-between
```

---

### 4. Top Ejercicios

**Lista (space-y-3):**

**Exercise Row:**
```
Fondo: surfaceContainer (#1F1F24)
Forma: rounded-2xl
Padding: 16dp (p-4)
Borde: 1px white/5
Layout: Row, items-center, space-between
```

| Rank | Fondo rank | Color rank | Nombre | Series | PR |
|------|-----------|-----------|--------|--------|-----|
| 1 | primaryContainer/20 | primary | Bench Press | 42 series | 100kg (primary) |
| 2 | surfaceContainerHighest | onSurface | Squat | 38 series | 140kg |
| 3 | surfaceContainerHighest | onSurface | Pull-up | 27 series | +20kg |

**Rank badge:** 48×48dp (h-12 w-12), rounded-xl, headline font, bold
**Right side:** "PR" label (10sp, uppercase, tracking-tighter, outline) + valor (18sp, headline, bold)

---

### 5. Records Personales

```
Layout: Grid 2 columnas, gap-4
Padding bottom: 48dp (pb-12)
```

**PR Card:**
```
Fondo: surfaceContainer (#1F1F24)
Padding: 20dp (p-5)
Forma: rounded-2xl
Borde: tertiary/20 (si reciente) o white/5 (normal)
```

- Icono: emoji_events (FILL 1), tertiary (reciente) / secondary (normal), mb-3
- Nombre: 14sp, bold, onSurfaceVariant
- Valor: Space Grotesk, 20sp (text-xl), extrabold + "x1" (10sp, outline, bold)
- Fecha: 10sp, uppercase, tracking-widest, bold
  - Reciente: tertiary ("¡Ayer!")
  - Normal: outline ("12 Abr")

---

## Comportamiento e Interacciones

1. **Period chips**: Cambia el rango de datos mostrados (Semana/Mes/3 Meses/Año/Todo)
2. **KPI scroll**: Horizontal scroll entre 4+ KPIs
3. **Charts**: Visualización estática (no interactiva en el diseño, pero podría ser tap para detalle)
4. **Top ejercicios**: Tap para ir a detalle estadístico del ejercicio
5. **PRs**: Borde tertiary resalta records recientes
6. **Background image**: Decorativa, grayscale + gradient fade

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Vista | No existe / básica | Pantalla completa con sheet rounded-t-32 |
| KPIs | No existen | 4 KPI cards glass con iconos y trends |
| Charts | No existen | Area chart, bar chart, progress bars |
| Period filter | No existe | 5 chips (Semana, Mes, 3M, Año, Todo) |
| Top exercises | No existe | Lista ranked con PR destacado |
| PRs | No existe | Grid de cards con fecha y emoji_events |
| Background | No existe | Imagen gym grayscale con gradient |

---

## Plan de Implementación

### Paso 1: Crear Statistics Screen
- Pantalla completa accesible desde Profile → Estadísticas
- Background image + sheet con drag handle

### Paso 2: Period Filter + KPIs
- Chip group para periodo
- LazyRow de KPI cards glass

### Paso 3: Charts
- Librería de gráficos (ej: Vico, YCharts)
- Area chart para volumen
- Bar chart para frecuencia
- Linear progress bars para distribución muscular

### Paso 4: Top Exercises + PRs
- Lista ranked de ejercicios más usados
- Grid de PRs recientes

### Paso 5: Datos
- Conectar con WorkoutRepository para calcular estadísticas
- Filtrado por periodo
