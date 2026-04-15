# 09 — Lista de Rutinas (Routine List)

## Metadatos Stitch
- **Screen ID**: `69dc93cd54fc4eefbf5be9f579605684`
- **Título**: Rutinas - Rediseño - Mobile
- **Dimensiones**: 780 × ~1600 px
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Screen (bg-surface #131318)
├── TopAppBar (glass, search icon + "ROUTINES" title + avatar)
│
├── Main Content (pt-24, pb-32, px-6)
│   ├── Title: "Rutinas" (5xl, extrabold, tracking-tighter)
│   ├── SearchBar (surfaceContainerLow, rounded-xl)
│   │
│   ├── TabRow (3 tabs, border-b)
│   │   ├── "MIS RUTINAS" (activo, primary, underline)
│   │   ├── "COMPARTIDAS" (inactivo, outline)
│   │   └── "DEL ENTRENADOR" (inactivo, outline)
│   │
│   ├── Section: PECHO
│   │   └── Horizontal scroll cards (gradient border)
│   │       ├── Card: "Hypertrophy Chest Day" (Classic Pyramid)
│   │       └── Card: "Max Power Bench" (Strength)
│   │
│   ├── Section: PIERNAS
│   │   └── Horizontal scroll cards
│   │       └── Card: "Leg Day Destroyer" (Endurance)
│   │
│   └── Rutinas Sugeridas
│       ├── Header: "Rutinas sugeridas" + "Ver todo"
│       └── Horizontal scroll image cards
│           ├── "Full Body Express" (25 MIN • BEGINNER)
│           ├── "Core Core Core" (15 MIN • PRO)
│           └── "HIIT Blast" (20 MIN • INTENSE)
│
├── FAB (gradient, rounded-full, bottom-24 right-6)
└── BottomNavBar (4 tabs: TRAIN/EXPLORE/STATS/VAULT)
```

---

## Componentes Detallados

### 1. TopAppBar

```
Posición: fixed top-0, full width, z-50
Fondo: #131318/70 + backdrop-blur-xl
Padding: px-6 py-4
Layout: Row, space-between, items-center
```

- Left: Icono search, primary (#BAC3FF), 24sp
- Center: "ROUTINES" — Space Grotesk, 24sp, bold, uppercase, font-black, primary
- Right: Avatar 40×40dp, rounded-full, border-2 primary/20

---

### 2. Header Section

**Título:**
- "Rutinas" — Space Grotesk, 48sp (text-5xl), extrabold, tracking-tighter
- Color: onSurface (#E4E1E9)
- Margin bottom: 24dp (mb-6)

**SearchBar:**
```
Ancho: 100%
Fondo: surfaceContainerLow (#1B1B20)
Borde: none
Forma: rounded-xl (12dp)
Padding: py-4 pl-12 pr-4
Placeholder: "Buscar rutinas..." — onSurfaceVariant
Focus: ring-2 primary/30
Icono: search (absolute left-4, center), color outline
```

---

### 3. TabRow

```
Layout: Row, gap-8 (32dp)
Border bottom: 1px outlineVariant/10
Overflow-x: auto, horizontal scroll (hide-scrollbar)
Margin bottom: 32dp (mb-8)
```

| Tab | Estado | Color texto | Border bottom | Fuente |
|-----|--------|-------------|---------------|--------|
| MIS RUTINAS | **Activo** | primary (#BAC3FF) | 2px primary | Space Grotesk, bold, uppercase, tracking-widest |
| COMPARTIDAS | Inactivo | outline (#8E8FA1) | transparent | Space Grotesk, bold, uppercase, tracking-widest |
| DEL ENTRENADOR | Inactivo | outline | transparent | Space Grotesk, bold, uppercase, tracking-widest |

- Padding bottom: 16dp (pb-4)
- Whitespace: nowrap
- Hover inactivo: text onSurface

---

### 4. Section por Grupo Muscular

**Section header:**
```
Fuente: Space Grotesk, 12sp (text-xs), bold, tracking-[0.2em], uppercase
Color: primary (#BAC3FF)
Margin bottom: 16dp (mb-4)
```

**Horizontal scroll container:**
```
Layout: Row, overflow-x auto, hide-scrollbar
Gap: 16dp (gap-4)
Margin horizontal: -mx-6 px-6 (bleed edges)
```

**Routine Card:**
```
Ancho mínimo: 280dp (min-w-[280px])
Wrapper: p-[1px] rounded-2xl — gradient border effect
  Gradient border: to-br from-{color}/30 to-transparent
  Colores por grupo:
    - Pecho card 1: primary/30
    - Pecho card 2: secondary/30
    - Piernas: tertiary/30
Inner card:
  Fondo: surfaceContainerLowest (#0E0E13)
  Forma: rounded-2xl (16dp)
  Padding: 20dp (p-5)
  Layout: Column, space-between, full height
```

**Card content:**
- **Top row (flex, justify-between, items-start, mb-4):**
  - Tag: Tipo de rutina (ej: "Classic Pyramid", "Strength", "Endurance")
    - Colores por tipo:
      - Classic Pyramid: tertiary/10 bg, tertiary text
      - Strength: primary/10 bg, primary text
      - Endurance: secondary/10 bg, secondary text
    - Padding: px-3 py-1, rounded-full
    - Fuente: 10sp, bold, tracking-widest, uppercase
  - Icono: more_vert, color outline → primary on hover

- **Body (mb-6):**
  - Label: "FOCUS" — 12sp (text-xs), bold, outline
  - Título: Space Grotesk, 20sp (text-xl), bold, onSurface, leading-tight

- **Footer (flex, gap-4):**
  - Ejercicios: fitness_center icon (14sp) + "5 EXERC" — onSurfaceVariant, 12sp, bold, uppercase, tracking-tighter
  - Tiempo: schedule icon (14sp) + "45 MIN" — mismos estilos

---

### 5. Rutinas Sugeridas

**Header (flex, justify-between, items-end, mb-6):**
- Título: "Rutinas sugeridas" — Space Grotesk, 24sp (text-2xl), bold, tracking-tight, onSurface
- Link: "Ver todo" — primary, 14sp, bold, uppercase, tracking-widest

**Horizontal scroll cards:**
```
Min-width: 200dp
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl
Padding: 16dp (p-4)
Layout: Column, gap-3
```

- **Imagen:** Full width, 96dp alto (h-24), rounded-lg, object-cover
- **Título:** Space Grotesk, 14sp (text-sm), bold, onSurface, truncate
- **Info:** 10sp, bold, outline, uppercase, tracking-widest
  - Formato: "25 MIN • BEGINNER"

---

### 6. FAB

```
Posición: fixed, bottom-24, right-6, z-50
Tamaño: 64×64dp (w-16 h-16)
Forma: rounded-full (no rounded-2xl como en Exercise List)
Fondo: kinetic-gradient (135deg: primary→primaryContainer→secondaryContainer)
Color icono: white
Icono: add, 30sp, bold
Sombra: 0 8px 32px rgba(67,97,238,0.4)
Active: scale-90
```

> **Diferencia:** El FAB de rutinas usa rounded-full y un gradient de 3 colores (primary→primaryContainer→secondaryContainer), diferente al FAB de Exercise List que usa rounded-2xl y gradient de 2 colores.

---

## Comportamiento e Interacciones

1. **TabRow**: Filtra rutinas por categoría (Mis/Compartidas/Del Entrenador)
2. **SearchBar**: Filtrado en tiempo real por nombre
3. **Routine cards**: Tap para abrir detalle de rutina
4. **More menu (more_vert)**: Opciones de editar/duplicar/eliminar
5. **FAB**: Abre Create Routine dialog
6. **Rutinas sugeridas**: Scroll horizontal, tap para ver detalle
7. **"Ver todo"**: Navega a lista completa de sugerencias
8. **Gradient border**: Efecto visual con 1px de gradient usando wrapper p-[1px]

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Vista | Sheet con lista simple | Página completa con secciones |
| Organización | Lista plana | Agrupada por grupo muscular |
| Cards | Cards básicas | Cards con gradient border y tags |
| TabRow | No tiene tabs | 3 tabs: Mis/Compartidas/Entrenador |
| Sugeridas | No existen | Sección con image cards horizontales |
| Search | Posible | SearchBar prominente |
| FAB | Standard | Gradient 3 colores, rounded-full |

---

## Plan de Implementación

### Paso 1: Crear Routine List Screen
- Fullscreen con TopAppBar, tabs, y content
- SearchBar con icono

### Paso 2: TabRow
- 3 tabs con underline del activo
- Conectar con filtrado por tipo de rutina

### Paso 3: Secciones por Grupo Muscular
- Agrupar rutinas por targetedBodyPart
- LazyRow por cada grupo con cards gradient border

### Paso 4: Routine Cards
- Card con gradient border wrapper
- Tag tipo, focus label, título, exercise count + duration

### Paso 5: Rutinas Sugeridas
- Sección con header + "Ver todo"
- LazyRow con image cards

### Paso 6: FAB
- Gradient 3 colores, rounded-full
- Navega a Create Routine
