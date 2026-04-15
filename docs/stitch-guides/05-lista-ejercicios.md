# 05 — Lista de Ejercicios (Exercise List Sheet)

## Metadatos Stitch
- **Screen ID**: `225eb84af91f4b0bb632b930ec560153`
- **Título**: Lista de Ejercicios - Sheet/Mobile
- **Dimensiones**: 780 × ~884 px
- **Tipo**: Bottom Sheet (modal)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Overlay (bg-black/60 detrás)
└── BottomSheet (bg-[#1A1A24], rounded-t-[32dp], top-12)
    ├── Drag Handle (center, 48×6dp, surfaceContainerHighest)
    │
    ├── Header Section (px-6, pb-6)
    │   ├── Row: [←] + "Ejercicios" title + [spacer]
    │   ├── Search Input (surfaceContainerHigh, rounded-xl)
    │   └── Filter Chips: "Mis" (activo) | "De otros" (inactivo)
    │
    ├── Exercise List (scrollable, px-6, pb-32)
    │   ├── Card: Bench Press (Pecho, secondary)
    │   ├── Card: Plancha isométrica (Core, tertiary)
    │   ├── Card: Squat (Piernas, primary)
    │   ├── Card: Pull-up (Espalda, surfaceVariant)
    │   └── Card: Press Militar (Hombros, secondary, faded)
    │
    └── FAB: "+" (gradient primary→primaryContainer, bottom-10 right-6)
```

---

## Componentes Detallados

### 1. Bottom Sheet Container

```
Posición: fixed, inset-x-0, bottom-0, top-12
Fondo: #1A1A24 (ligeramente más claro que surface)
Forma: rounded-t-[32dp]
Borde superior: white/5
Sombra: shadow-2xl
z-index: 10
Layout: Column, overflow-hidden
```

**Overlay detrás:**
- bg-black/60
- fixed inset-0, z-0

**Drag Handle:**
- 48×6dp (w-12 h-1.5)
- Color: surfaceContainerHighest (#35343A)
- Forma: rounded-full
- Center, py-4

---

### 2. Header Section

```
Padding: px-6 pb-6
```

**Top Row (flex, items-center, justify-between, mb-8):**
- Botón back: 40×40dp, rounded-full, surfaceContainerHigh, icono arrow_back, onSurface
  - Active: scale-95
- Título: "Ejercicios" — Space Grotesk, 24sp (text-2xl), bold, tracking-tight
- Spacer: 40dp (para simetría)

**Search Input:**
```
Ancho: 100%
Fondo: surfaceContainerHigh (#2A292F)
Borde: none
Forma: rounded-xl (12dp)
Padding: py-4, pl-12 (para icono), pr-4
Color texto: onSurface (#E4E1E9)
Placeholder: "Buscar ejercicio..." — color outline (#8E8FA1)
Focus: ring-2 primary/30
Transición: all
Fuente: medium
Margin bottom: 24dp (mb-6)
```

**Icono búsqueda (dentro del input):**
- Position: absolute, left-4, top-50%, -translate-y-50%
- Icono: magnification_large (o search)
- Color: outline → primary on focus (group-focus-within)

**Filter Chips (flex, gap-3):**

| Chip | Estado | Fondo | Borde | Color texto | Extra |
|------|--------|-------|-------|-------------|-------|
| Mis | **Activo** | primary (#BAC3FF) | — | onPrimary (#00218D) | shadow-lg shadow-primary/20 |
| De otros | Inactivo | transparent | 1px outline-variant | onSurfaceVariant | hover: bg white/5 |

- Padding: px-6 py-2
- Forma: rounded-full
- Fuente: 14sp (text-sm), bold, tracking-wide

---

### 3. Exercise List

```
Container: flex-1, overflow-y-auto, px-6, pb-32
Custom scrollbar: 4px width, surfaceContainerHighest thumb
Layout: Column, space-y-4
```

**Exercise Card (cada ejercicio):**
```
Fondo: surfaceContainerLow (#1B1B20)
Padding: 16dp (p-4)
Forma: rounded-xl (12dp)
Layout: Row, items-center, space-between
Active: scale-[0.98]
Cursor: pointer
Transición: all
```

**Left side (Column, gap-2):**
- Top row (Row, items-center, gap-3):
  - **Muscle tag:** Texto grupo muscular en uppercase
  - **Nombre:** Space Grotesk, 18sp (text-lg), bold, onSurface

- **Descripción:** 14sp (text-sm), outlineVariant, medium

**Right side:**
- Icono: chevron_right
- Color: outlineVariant → primary on hover (group-hover)

**Colores por grupo muscular (tag):**

| Grupo | Fondo Tag | Color Texto |
|-------|-----------|-------------|
| Pecho | secondaryContainer/30 | secondary (#D2BBFF) |
| Core | tertiaryContainer/30 | tertiary (#27E0A9) |
| Piernas | primaryContainer/30 | primary (#BAC3FF) |
| Espalda | surfaceContainerHighest | onSurfaceVariant (#C4C5D7) |
| Hombros | secondaryContainer/30 | secondary (#D2BBFF) |

**Estilo del tag:**
```
Padding: px-2.5 py-0.5
Forma: rounded-md (4dp)
Fuente: 10sp (text-[10px]), font-black, uppercase, tracking-widest
```

**Último item (faded):**
- Opacity: 60% (indica que hay más scroll)

---

### 4. FAB "Crear Ejercicio"

```
Posición: fixed, bottom-10, right-6, z-20
Tamaño: 64×64dp (w-16 h-16)
Fondo: gradient to-br from-primary to-primaryContainer
Color icono: onPrimary (#00218D)
Forma: rounded-2xl (16dp)
Sombra: 0 12px 40px rgba(67,97,238,0.4)
Active: scale-90
Transición: all
```

- Icono: add, 30sp (text-3xl), wght 600

---

## Comportamiento e Interacciones

1. **Drag handle**: Permite deslizar el sheet hacia abajo para cerrar
2. **Back button**: Cierra el sheet (navega atrás)
3. **Search**: Filtrado en tiempo real por nombre de ejercicio
4. **Filter chips**: Alterna entre ejercicios propios ("Mis") y ejercicios de otros usuarios/públicos
5. **Exercise card tap**: Navega a detalle del ejercicio (Observe Exercise dialog)
6. **FAB tap**: Abre Create Exercise dialog
7. **Scroll**: Lista vertical con scroll, último item faded indica continuidad
8. **Long press card**: Potencial menú contextual (editar/eliminar)

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Contenedor | Sheet con lista simple | Sheet con rounded-t-32, drag handle |
| Búsqueda | Posiblemente sin campo | Search input prominente con icono |
| Filtros | Sin filter chips | "Mis" / "De otros" chips |
| Cards | Lista básica | Cards con muscle tags coloreados por grupo |
| Tags | Sin muscle tags | Tags uppercase con colores por grupo |
| Descripción | Sin descripción visible | Descripción en cada card |
| FAB | Sin FAB en sheet | FAB gradient para crear ejercicio |

---

## Plan de Implementación

### Paso 1: Rediseñar Sheet Container
- Usar ModalBottomSheet de Material3 con shape rounded-t-32
- Drag handle visible
- Back button + título centrado

### Paso 2: Search + Filter Chips
- TextField con leading icon (search)
- Row de FilterChip: "Mis" (selected) / "De otros"
- Conectar con ViewModel para filtrado

### Paso 3: Rediseñar Exercise Cards
- Card con muscle tag coloreado según grupo muscular
- Nombre en headline + descripción
- Chevron derecho
- Escala al presionar (0.98)

### Paso 4: FAB "Crear Ejercicio"
- FAB gradient con shadow prominente
- Navega a Create Exercise dialog

### Paso 5: Integración
- Conectar con ExerciseViewModel existente
- Filtrado por búsqueda (nombre)
- Filtrado por propiedad (mío vs de otros)
