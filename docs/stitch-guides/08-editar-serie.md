# 08 — Editar Serie (Edit Set Dialog)

## Metadatos Stitch
- **Screen ID**: `83171f991a4e4ec587110ce2bb53c45b`
- **Título**: Registrar Serie / Edit Set - Dialog
- **Tipo**: Dialog (modal, centrado)
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Overlay (bg-black/60, backdrop-blur-sm)
└── Dialog (bg-[#1A1A24], rounded-[24dp], max-w-md, p-8)
    ├── Tonal Glow (primary/10, blur-80, top-right)
    │
    ├── Header
    │   ├── "Registrar serie" (headline, 3xl)
    │   └── "Bench Press" (exercise name, secondary)
    │
    ├── Weight Input (grande, prominente)
    │   ├── Label: "PESO (KG)"
    │   ├── [-] button (56dp, primary)
    │   ├── "82.5" (7xl, font-black, white)
    │   └── [+] button (56dp, primary)
    │
    ├── Reps Input (card surfaceContainerLow)
    │   ├── Label: "REPETICIONES"
    │   ├── [-] button (40dp, secondary)
    │   ├── "8" (4xl, bold)
    │   └── [+] button (40dp, secondary)
    │
    ├── Observations TextField
    │   └── "RPE 8, Máx esfuerzo, ..."
    │
    └── Action Row
        ├── "Cancelar" (outlined)
        └── "Guardar" (gradient)
```

---

## Componentes Detallados

### 1. Dialog Container

```
Overlay: fixed inset-0, bg-black/60, backdrop-blur-sm, z-60
         flex items-center justify-center, p-4

Dialog:
  Fondo: #1A1A24
  Ancho: 100%, max-w-md
  Forma: rounded-[24dp]
  Padding: 32dp (p-8)
  Sombra: shadow-2xl
  Borde: 1px white/5
  Overflow: hidden
  Position: relative
```

**Tonal Glow (decorativo):**
- Position: absolute, -top-24, -right-24
- 192×192dp (w-48 h-48)
- Color: primary/10
- Blur: 80px
- Forma: rounded-full

---

### 2. Header

```
Margin bottom: 32dp (mb-8)
z-index: 10 (sobre el glow)
```

- Título: "Registrar serie"
  - Fuente: Space Grotesk, 30sp (text-3xl), bold, tracking-tight
  - Color: onSurface (#E4E1E9)
- Subtítulo: "Bench Press" (nombre del ejercicio)
  - Fuente: Manrope, medium
  - Color: secondary (#D2BBFF)
  - Margin top: 4dp

---

### 3. Weight Input (Sección Principal)

```
Margin bottom: 40dp (mb-10)
z-index: 10
```

**Label:**
- "PESO (KG)"
- Fuente: 12sp (text-xs), uppercase, tracking-widest, bold
- Color: slate-500 (~outline)
- Margin bottom: 16dp (mb-4)

**Layout: Row, items-center, justify-between, gap-4**

**Botones -/+:**
```
Tamaño: 56×56dp (w-14 h-14)
Forma: rounded-full
Fondo: surfaceContainerHigh (#2A292F)
Borde: 1px white/5
Color icono: primary (#BAC3FF)
Icono: remove / add, 30sp (text-3xl)
Hover: surfaceContainerHighest
Active: scale-90
Transición: all
```

**Valor peso:**
```
Texto: "82.5"
Fuente: Space Grotesk, 72sp (text-7xl), font-black (900)
Color: white (#FFFFFF) — no onSurface, blanco puro
Tracking: tighter (-0.05em)
Alineación: center (flex-col, items-center)
```

---

### 4. Reps Input (Sección Secundaria)

```
Margin bottom: 32dp (mb-8)
z-index: 10
Fondo: surfaceContainerLow/50 (#1B1B20 al 50%)
Padding: 24dp (p-6)
Forma: rounded-2xl (16dp)
Borde: 1px white/5
```

**Label:**
- "REPETICIONES"
- Fuente: 12sp, uppercase, tracking-widest, bold
- Color: slate-500
- Margin bottom: 12dp (mb-3)

**Layout: Row, items-center, justify-between**

**Botones -/+:**
```
Tamaño: 40×40dp (w-10 h-10)
Forma: rounded-full
Fondo: surfaceContainerHigh
Color icono: secondary (#D2BBFF) — nota: secundario, no primario
Active: scale-90
```

**Valor reps:**
```
Texto: "8"
Fuente: Space Grotesk, 36sp (text-4xl), bold
Color: onSurface (#E4E1E9)
```

> **Diferencia intencional:** El peso usa botones grandes (56dp) con color primary y valor en blanco puro 7xl. Las reps usan botones más pequeños (40dp) con color secondary y valor 4xl. Esto enfatiza el peso como dato principal.

---

### 5. Observations TextField

```
Margin bottom: 40dp (mb-10)
z-index: 10
```

**Label:**
- "OBSERVACIONES"
- Fuente: 12sp, uppercase, tracking-widest, bold, slate-500
- Margin bottom: 8dp (mb-2)

**Input:**
```
Ancho: 100%
Fondo: surfaceContainerHigh (#2A292F)
Borde: none
Forma: rounded-xl (12dp)
Padding: py-4 px-5
Color texto: onSurface
Placeholder: "RPE 8, Máx esfuerzo, ..." — slate-600
Focus: ring-2 primary/30
Transición: all
```

---

### 6. Action Row

```
Layout: Row, gap-4
z-index: 10
```

| Botón | Flex | Fondo | Color | Forma | Fuente | Extra |
|-------|------|-------|-------|-------|--------|-------|
| Cancelar | flex-1 | transparent | onSurface | rounded-xl | bold, 14sp, uppercase, tracking-wider | border 1px outlineVariant, hover bg white/5 |
| Guardar | flex-1 | gradient to-br from-primaryContainer to-#2E4EDC | white | rounded-xl | bold, 14sp, uppercase, tracking-wider | shadow-lg primaryContainer/20, active scale-95 |

- Padding: py-4 px-6

---

## Comportamiento e Interacciones

1. **Botones -/+**: Incrementan/decrementan el valor de peso/reps
   - Peso: incremento de 0.5 o 1.0 kg (configurable)
   - Reps: incremento de 1
2. **Valores editables**: Además de los botones, tap en el número permite edición directa con teclado numérico
3. **Observaciones**: Campo opcional para notas como RPE, sensaciones, etc.
4. **Cancelar**: Cierra sin guardar
5. **Guardar**: Persiste el set → actualiza la tabla de sets en Active Workout
6. **Pre-llenado**: En modo edición, los valores vienen del set seleccionado. En nuevo set, se sugieren valores de la última sesión.

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Contenedor | Dialog básico (EditSetDialog) | Dialog con tonal glow y rounded-24 |
| Peso input | TextField numérico | Stepper grande con -/+ (56dp) + valor 7xl |
| Reps input | TextField numérico | Stepper en card con -/+ (40dp) + valor 4xl |
| Observaciones | No existe | TextField con placeholder RPE |
| Jerarquía | Peso y reps iguales | Peso PROMINENTE (blanco, 7xl), reps secundarias |
| Glow | No existe | Tonal glow primary/10 esquina superior derecha |
| Ejercicio name | No visible | Subtítulo en secondary bajo el título |

---

## Plan de Implementación

### Paso 1: Rediseñar EditSetDialog
- Dialog con rounded-24dp y glow decorativo
- Header con título + nombre del ejercicio en secondary

### Paso 2: Weight Stepper (prominente)
- Composable `StepperInput` reutilizable
- Botones circulares grandes (56dp), icono remove/add
- Valor central en font-black 7xl blanco

### Paso 3: Reps Stepper (secundario)
- Reutilizar `StepperInput` con tamaño menor
- Contenido en card surfaceContainerLow
- Botones 40dp, color secondary

### Paso 4: Observations + Actions
- TextField para observaciones
- Cancelar (outlined) + Guardar (gradient)
- Conectar con WorkoutViewModel/SetRepository
