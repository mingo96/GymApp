# 10 — Crear/Editar Rutina (Create Routine Dialog)

## Metadatos Stitch
- **Screen ID**: `962ea00e35d040c6a4072a6afc6caab9`
- **Título**: Crear Rutina - Dialog
- **Tipo**: Dialog (modal, centrado)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Overlay (bg-background/80, backdrop-blur-sm)
└── Dialog (bg-[#1A1A24], rounded-xl, max-w-lg)
    ├── Header (p-6 pb-2)
    │   ├── "Nueva rutina" (headline, 2xl)
    │   └── Subtitle descripción
    │
    ├── Form Content (p-6, space-y-6)
    │   ├── TextField: Nombre de la rutina *
    │   ├── Dropdown: Parte del cuerpo principal
    │   └── Exercise List (scrollable, max-h-56)
    │       ├── Item checked: Bench Press ✓ (drag_handle)
    │       ├── Item checked: Press inclinado ✓ (drag_handle)
    │       ├── Item unchecked: Cable Fly (add icon)
    │       ├── Item unchecked: Squat (add icon)
    │       └── Item faded: Shoulder Press (scroll hint)
    │
    └── Footer (surfaceContainerLowest/50, border-t)
        ├── "Cancelar" (outlined)
        └── "Crear rutina" (gradient)
```

---

## Componentes Detallados

### 1. Dialog Container

```
Overlay: fixed inset-0, bg-background/80, backdrop-blur-sm, z-50
         flex items-center justify-center, p-4

Dialog:
  Fondo: #1A1A24
  Ancho: 100%, max-w-lg
  Forma: rounded-xl (12dp)
  Sombra: 0 32px 64px -12px rgba(0,0,0,0.8)
  Borde: 1px outlineVariant/15
  Overflow: hidden
```

---

### 2. Header

```
Padding: p-6 pb-2
```

- Título: "Nueva rutina"
  - Fuente: Space Grotesk, 24sp (text-2xl), bold, tracking-tight
  - Color: onSurface (#E4E1E9)
- Subtítulo: "Configura tu próximo bloque de entrenamiento de alto rendimiento."
  - Fuente: Manrope, 14sp (text-sm), regular
  - Color: onSurfaceVariant (#C4C5D7)
  - Margin top: 4dp

---

### 3. Form Content

```
Padding: p-6
Layout: Column, space-y-6 (24dp)
```

#### TextField: Nombre de la rutina *
- **Label:** "NOMBRE DE LA RUTINA *"
  - Fuente: Manrope, 12sp, uppercase, tracking-widest, bold
  - Color: primary (#BAC3FF)
- **Input:**
  ```
  Fondo: surfaceContainerHigh (#2A292F)
  Borde: none
  Forma: rounded-md (4dp — más sutil que rounded-xl)
  Padding: px-4 py-3
  Placeholder: "Ej: Empuje A" — outline
  Focus: ring-2 primary/30
  ```

#### Dropdown: Parte del cuerpo principal
- **Label:** "PARTE DEL CUERPO PRINCIPAL" — primary, bold
- **Selector:**
  ```
  Fondo: surfaceContainerHigh
  Forma: rounded-md
  Padding: px-4 py-3
  Layout: Row, space-between
  Cursor: pointer
  Hover: surfaceContainerHighest
  ```
  - Valor: "Pecho" — onSurface
  - Icono: expand_more — outline

#### Exercise List (Selector de ejercicios)

**Header row:**
- Label: "ELIGE LOS EJERCICIOS" — primary, bold, uppercase
- Hint: "Scroll para ver más" — 10sp, outline, italic

**Container:**
```
Max height: 224dp (max-h-56)
Overflow-y: auto
Layout: Column, space-y-2
Padding right: 4dp (pr-1, para scrollbar)
Custom scrollbar: 4px, outlineVariant thumb
```

**Item Seleccionado (checked):**
```
Fondo: surfaceContainerLow (#1B1B20)
Hover: surfaceContainerHigh
Forma: rounded-md
Padding: 12dp (p-3)
Layout: Row, items-center, space-between
Cursor: pointer
```
- Left (Row, gap-3):
  - Checkbox: 24×24dp (w-6 h-6), rounded (4dp), **bg primary, text onPrimary**
    - Icono: check, 14sp, bold
  - Info (Column):
    - Nombre: 14sp, bold, onSurface
    - Grupo: 12sp, onSurfaceVariant
- Right: drag_handle — outline → primary on hover

**Item No Seleccionado (unchecked):**
```
Fondo: surfaceContainerLowest (#0E0E13)
Hover: surfaceContainerHigh
Borde: 1px outlineVariant/10
```
- Checkbox: 24×24dp, rounded, border-2 outlineVariant, **vacío**
- Nombre: medium (no bold), onSurfaceVariant
- Grupo: outline
- Right: add icon — outline/30

**Item Faded (scroll hint):**
- Opacity: 50% — indica más items disponibles

---

### 4. Footer

```
Padding: p-6
Fondo: surfaceContainerLowest/50
Border top: 1px outlineVariant/10
Layout: Row, items-center, justify-end, gap-3
```

| Botón | Fondo | Color | Forma | Fuente | Extra |
|-------|-------|-------|-------|--------|-------|
| Cancelar | transparent | onSurfaceVariant | rounded-lg (8dp) | Space Grotesk, 14sp, bold, uppercase, tracking-wide | border 1px outlineVariant, hover bg white/5 |
| Crear rutina | gradient to-br primary→primaryContainer | onPrimaryFixed | rounded-lg | Space Grotesk, 14sp, font-black, uppercase, tracking-widest | hover opacity-90, active scale-95 |

- Padding: px-6 py-2.5

---

## Comportamiento e Interacciones

1. **Nombre requerido**: Campo marcado con `*`, label en primary
2. **Dropdown**: Tap abre lista de opciones de grupo muscular
3. **Exercise checkbox**: Tap alterna seleccionado/no seleccionado
4. **Drag handle**: Permitir reordenar ejercicios con drag & drop
5. **Add icon (unchecked)**: Mismo efecto que checkbox — selecciona el ejercicio
6. **Scroll hint**: Último item faded indica más contenido
7. **Cancelar**: Cierra sin guardar
8. **Crear rutina**: Valida nombre → crea rutina con ejercicios seleccionados → cierra
9. **Modo edición**: Título "Editar rutina", botón "Guardar", datos pre-cargados

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Contenedor | Sheet / pantalla | Dialog modal |
| Ejercicios | Selector separado | Lista inline con checkboxes |
| Reordenación | No existe | drag_handle para reordenar |
| Checkbox | Standard Material | Custom: primary bg + check icon |
| Dropdown | DropdownMenu | Custom selector con expand_more |
| Footer | Botón simple | Footer con fondo surfaceContainerLowest/50 |

---

## Plan de Implementación

### Paso 1: Crear Dialog composable
- `CreateRoutineDialog` con overlay y modal

### Paso 2: Form Fields
- TextField para nombre (required)
- DropdownMenu para parte del cuerpo

### Paso 3: Exercise Selector
- LazyColumn con items seleccionables
- Custom checkbox: primary bg filled vs outlined
- drag_handle para reordenar (LazyColumn + DragAndDrop)

### Paso 4: Footer + Validación
- Cancelar + Crear rutina buttons
- Validar nombre y al menos 1 ejercicio seleccionado
