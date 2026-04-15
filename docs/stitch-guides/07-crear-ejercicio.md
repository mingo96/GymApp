# 07 — Crear/Editar Ejercicio (Create Exercise Dialog)

## Metadatos Stitch
- **Screen ID**: `55a8f438304a4aadaa9c278c309f82d6`
- **Título**: Crear Ejercicio - Dialog
- **Tipo**: Dialog (modal, centrado)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Overlay (bg-black/80, backdrop-blur-sm)
└── Dialog (bg-[#1A1A24], rounded-2xl, max-w-lg)
    ├── Header (px-8 pt-8 pb-4)
    │   ├── "Nuevo ejercicio" (headline, 2xl)
    │   └── "Configura los parámetros técnicos de tu set." (subtitle)
    │
    ├── Form Body (px-8 py-4, scrollable, space-y-6)
    │   ├── TextField: Nombre del ejercicio *
    │   ├── TextArea: Descripción
    │   ├── Grid 2 cols:
    │   │   ├── Select: Parte del cuerpo *
    │   │   └── TextField: Sets y reps (mono, "4x8")
    │   ├── Chip Group: Tipo de reps (Base | Drop | RPE)
    │   └── Chip Group: Tipo de peso (Base | Progresivo | Regresivo)
    │
    └── Footer (p-8, border-t)
        ├── "Cancelar" (outlined)
        └── "Crear" (gradient)
```

---

## Componentes Detallados

### 1. Dialog Container

```
Overlay: fixed inset-0, bg-black/80, backdrop-blur-sm, z-60
         flex items-center justify-center, p-4

Dialog:
  Fondo: #1A1A24
  Ancho: 100%, max-w-lg
  Forma: rounded-2xl (16dp)
  Sombra: shadow-2xl
  Borde: 1px white/5
  Layout: Column
  Overflow: hidden
```

---

### 2. Header

```
Padding: px-8 pt-8 pb-4
```

- Título: "Nuevo ejercicio"
  - Fuente: Space Grotesk, 24sp (text-2xl), bold, tracking-tight
  - Color: onSurface (#E4E1E9)
- Subtítulo: "Configura los parámetros técnicos de tu set."
  - Fuente: Manrope, 14sp (text-sm), regular
  - Color: onSurfaceVariant (#C4C5D7)
  - Margin top: 4dp

---

### 3. Form Body

```
Padding: px-8 py-4
Layout: Column, space-y-6 (24dp)
Overflow-y: auto
Max height: 618px (max-h-[618px])
```

#### TextField: Nombre del ejercicio *
- **Label:** "NOMBRE DEL EJERCICIO *"
  - Fuente: Space Grotesk, 12sp, bold, uppercase, tracking-widest
  - Color: primary/70 (#BAC3FF con 70% opacity)
  - Margin bottom: 8dp
- **Input:**
  ```
  Ancho: 100%
  Fondo: surfaceContainerHigh (#2A292F)
  Borde: none
  Forma: rounded-xl (12dp)
  Padding: px-4 py-3
  Color texto: onSurface (#E4E1E9)
  Placeholder: "Ej: Press de banca" — onSurfaceVariant/40
  Focus: ring-2 primary/30
  ```

#### TextArea: Descripción
- **Label:** "DESCRIPCIÓN" — onSurfaceVariant (sin asterisco, no requerido)
- **Input:** Igual que TextField pero `<textarea>` con rows=3, resize-none

#### Grid 2 columnas (gap-4):

**Select: Parte del cuerpo ***
- **Label:** "PARTE DEL CUERPO *" — primary/70
- **Select:**
  ```
  Fondo: surfaceContainerHigh
  Forma: rounded-xl
  Padding: px-4 py-3
  appearance: none
  Icono: expand_more (absolute right-3, center)
  Focus: ring-2 primary/30
  ```
- Opciones: Pecho, Espalda, Pierna, Hombros, Brazos

**TextField: Sets y reps**
- **Label:** "SETS Y REPS" — onSurfaceVariant (opcional)
- **Input:** text-center, font-mono, tracking-widest
- Placeholder: "4x8"

#### Chip Group: Tipo de reps

**Label:** "TIPO DE REPS" — onSurfaceVariant

**Chips (flex-wrap, gap-2):**

| Chip | Estado | Fondo | Color | Forma |
|------|--------|-------|-------|-------|
| Base | **Activo** | primary (#BAC3FF) | onPrimary (#00218D) | rounded-full |
| Drop | Inactivo | surfaceContainerHighest (#35343A) | onSurfaceVariant | rounded-full |
| RPE | Inactivo | surfaceContainerHighest (#35343A) | onSurfaceVariant | rounded-full |

```
Padding: px-5 py-2
Fuente: 12sp (text-xs), bold, uppercase, tracking-tighter
Hover activo: opacity-90
Hover inactivo: bg surfaceVariant
```

#### Chip Group: Tipo de peso

Misma estructura que Tipo de reps:

| Chip | Estado | Fondo | Color |
|------|--------|-------|-------|
| Base | Inactivo | surfaceContainerHighest | onSurfaceVariant |
| Progresivo | **Activo** | primary | onPrimary |
| Regresivo | Inactivo | surfaceContainerHighest | onSurfaceVariant |

---

### 4. Footer

```
Padding: p-8
Border top: 1px white/5
Layout: Row, gap-3
Margin top: 16dp (mt-4)
```

| Botón | Flex | Fondo | Color | Forma | Fuente | Extra |
|-------|------|-------|-------|-------|--------|-------|
| Cancelar | flex-1 | transparent | onSurfaceVariant | rounded-xl | bold, 14sp, uppercase, tracking-widest | border 1px outlineVariant/30, hover bg white/5 |
| Crear | flex-1 | gradient primary→primaryContainer | onPrimaryFixed (#001159) | rounded-xl | font-black, 14sp, uppercase, tracking-widest | shadow 0 8px 20px rgba(67,97,238,0.3), active scale-95 |

- Padding: py-3.5

---

## Comportamiento e Interacciones

1. **Campos requeridos**: Marcados con `*` — label en primary/70 (vs onSurfaceVariant para opcionales)
2. **Chip selection**: Solo uno activo por grupo (radio behavior)
3. **Select dropdown**: Nativo con icono expand_more
4. **Sets y reps**: Input libre con formato sugerido "4x8"
5. **Cancelar**: Cierra el diálogo sin guardar
6. **Crear**: Valida campos requeridos (nombre, parte del cuerpo) → crea ejercicio → cierra
7. **Scroll**: El form body tiene scroll si excede max-height (618px)
8. **En modo edición**: Título cambia a "Editar ejercicio", botón a "Guardar", campos pre-llenan

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Contenedor | Sheet / pantalla separada | Dialog modal centrado |
| Campos | Básicos (nombre, parte cuerpo) | Nombre, descripción, parte cuerpo, sets/reps, tipo reps, tipo peso |
| Tipo reps | No existe | Chip group: Base/Drop/RPE |
| Tipo peso | No existe | Chip group: Base/Progresivo/Regresivo |
| Labels | Estándar | Uppercase tracking-widest, required vs optional coloreados |
| Acciones | Botón simple | Cancelar (outlined) + Crear (gradient) |

---

## Plan de Implementación

### Paso 1: Crear Dialog composable
- `CreateExerciseDialog` reutilizable para crear y editar

### Paso 2: Form Fields
- TextField para nombre (required) y descripción
- DropdownMenu para parte del cuerpo (required)
- TextField mono para sets/reps

### Paso 3: Chip Groups
- `SingleSelectChipGroup` composable reutilizable
- Tipo de reps: Base, Drop, RPE
- Tipo de peso: Base, Progresivo, Regresivo

### Paso 4: Footer + Validación
- Cancelar + Crear/Guardar buttons
- Validación de campos requeridos
- Conectar con ExerciseViewModel
