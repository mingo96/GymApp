# 02 — Entrenar (Train)

## Metadatos Stitch
- **Screen ID**: `dea9d00bdc314023873b87ccfd43cd0f`
- **Título**: Entrenar (Train) - New Nav
- **Dimensiones**: 780 × 2024 px
- **Variantes**: 4 variantes "New Nav" + 2 "Rediseño"
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Screen (bg-background #131318, pb-12)
├── Header (fixed top, z-50, glass effect)
│   ├── Row: Avatar (32px circle, border primary/20)
│   └── NavTabs: [Inicio | Entrenar* | Perfil]
│       └── Indicador: Dot (h-1 w-4 bg-primary rounded-full) bajo tab activo
│
├── Main Content (pt-32, px-6)
│   ├── Section: Headline
│   │   ├── H2: "Entrenar" (text-5xl, extrabold, tracking-tighter)
│   │   └── Accent bar (h-1 w-12 bg-primary rounded-full)
│   │
│   ├── Section: CTA — Entrenamiento Libre
│   │   └── Button (full width, gradient primaryContainer→primary)
│   │       ├── Left: "Quick Start" label + "Entrenamiento libre" con play_circle
│   │       ├── Right: chevron_right (40px, onPrimary/40)
│   │       └── Decorative blob (white/10, blur-3xl)
│   │
│   ├── Section: Quick Access Bento (2-col grid)
│   │   ├── Card: Ejercicios (icono fitness_center, acento secondary)
│   │   └── Card: Rutinas (icono format_list_bulleted, acento tertiary)
│   │
│   ├── Section: Recientes (Carousel horizontal)
│   │   ├── Header: "Recientes" + "Ver todos" link
│   │   └── Horizontal scroll (gap-4, hide-scrollbar)
│   │       ├── Card: En progreso (border tertiary/20, badge "En progreso")
│   │       ├── Card: Entrenamiento pasado 1 (opacity-40, grayscale)
│   │       └── Card: Entrenamiento pasado 2 (opacity-40, grayscale)
│   │
│   └── Section: Mis Rutinas
│       ├── Header: "Mis Rutinas" + botón "+"
│       └── Grid (1-col, gap-4)
│           ├── RoutineCard con imagen, nombre, frecuencia, tags
│           └── RoutineCard con imagen, nombre, frecuencia, tags
```

---

## Componentes Detallados

### 1. Header (idéntico a Home)

```
Fondo: #131318/90 (90% opacity) + backdrop-blur-xl
Borde inferior: white/5
Altura: 64dp (h-16)
```

**Diferencia con Home:** El indicador de tab activo aquí es un **dot** (`h-1 w-4 bg-primary rounded-full mt-1`) en vez de border-bottom. Verificar consistencia.

**Tab activo "ENTRENAR":**
- Fuente: Space Grotesk, 12sp, font-black (900), tracking-widest
- Color: primary (#BAC3FF)
- Dot debajo: 4×16dp, primary, rounded-full

**Tabs inactivos:**
- Color: onSurface/40
- Hover: onSurface transition

---

### 2. Headline "Entrenar"

```
Margin bottom: 32dp (mb-8)
```

**Título:**
- Texto: "Entrenar"
- Tamaño: 48sp (text-5xl)
- Peso: extrabold (800)
- Tracking: tighter (-0.05em)
- Color: onBackground (#E4E1E9)
- Fuente: Space Grotesk

**Accent bar:**
- Tamaño: 4×48dp (h-1 w-12)
- Color: primary (#BAC3FF)
- Forma: rounded-full
- Margin top: 8dp

---

### 3. CTA — Entrenamiento Libre (Botón Principal)

```
Margin bottom: 40dp (mb-10)
Ancho: 100% (full width)
Forma: rounded-xl (12dp)
Fondo: gradiente from primaryContainer (#4361EE) to primary (#BAC3FF), dirección bottom-right
Padding: 24dp (p-6)
Sombra: shadow-lg
Animación: active:scale-95, duración 150ms
Overflow: hidden (para elemento decorativo)
```

**Contenido (Row, space-between, items-center):**

| Posición | Elemento | Estilo |
|----------|----------|--------|
| Left upper | "Quick Start" | Space Grotesk, 14sp, bold, uppercase, tracking-widest, onPrimary/70 |
| Left lower | play_circle (filled) + "Entrenamiento libre" | Space Grotesk, 24sp, font-black (900), onPrimaryContainer (#F4F2FF), Row gap-2, leading-none |
| Right | chevron_right | 40sp (text-4xl), onPrimary/40, hover:translate-x-2 |

**Elemento decorativo:**
```
Posición: absolute top-0 right-0, offset -48dp right, -48dp top
Tamaño: 192×192dp (w-48 h-48)
Fondo: white/10
Forma: circle
Blur: blur-3xl
```

> **Mapeo actual:** En `TrainPage.kt` existe un CTA "Continuar Entrenamiento" cuando hay workout activo, y botones separados para ejercicios/rutinas. El diseño Stitch propone un **único botón grande** para "Entrenamiento Libre" como acción principal — un cambio de paradigma.

---

### 4. Quick Access Bento Grid

```
Layout: Grid 2 columnas, gap-4
Margin bottom: 48dp (mb-12)
```

**Card individual:**
```
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl (12dp)
Padding: 24dp (p-6)
Min height: 140dp
Layout: Column, items-start, justify-between
Hover: surfaceContainerHigh (#2A292F) con transición
Cursor: pointer
```

| Card | Icono | Color icono | Fondo icono | Nombre |
|------|-------|-------------|-------------|--------|
| Ejercicios | fitness_center | secondary (#D2BBFF) | secondary/10 | "Ejercicios" |
| Rutinas | format_list_bulleted | tertiary (#27E0A9) | tertiary/10 | "Rutinas" |

**Contenedor icono:**
- Padding: 12dp (p-3)
- Forma: rounded-lg (8dp)
- Icono: 30sp (text-3xl)
- Hover: scale-110, transition-transform

**Nombre:**
- Fuente: Space Grotesk, 18sp (text-lg), bold
- Color: onSurface (#E4E1E9)

> **Mapeo actual:** En `TrainPage.kt` hay un `TextButton("Ejercicios")` y `TextButton("Rutinas")`. El diseño los convierte en cards visuales tipo bento con iconos prominentes.

---

### 5. Sección Recientes (Carousel)

```
Margin bottom: 48dp (mb-12)
```

**Header:**
- Row, space-between, items-center
- Margin bottom: 16dp (mb-4)
- Título: "Recientes" — Space Grotesk, 20sp (text-xl), bold, tracking-tight
- Link: "Ver todos" — primary (#BAC3FF), 14sp, bold, uppercase, tracking-widest, cursor pointer

**Carousel:**
```
Layout: Row horizontal, overflow-x scroll, hide scrollbar
Gap: 16dp (gap-4)
Bleed: -24dp margins con 24dp padding (para mostrar cards cortadas)
Padding bottom: 16dp para sombras
```

#### Card "En Progreso":
```
Ancho: 288dp (w-72), flex-shrink-0
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl (12dp)
Borde: 1px tertiary/20 (#27E0A933)
Overflow: hidden
```

**Imagen superior:**
- Altura: 128dp (h-32)
- object-cover
- Imagen representativa del entrenamiento

**Badge "En progreso":**
```
Posición: absolute top-3 left-3
Fondo: tertiary (#27E0A9)
Color texto: onTertiary (#003827)
Padding: px-3 py-1
Forma: rounded-full
Fuente: 10sp, font-black (900), uppercase, tracking-tighter
Icono: timer (filled), 12sp
Sombra: shadow-lg
```

**Contenido inferior (p-4):**
- Nombre: Space Grotesk, 18sp, bold, leading-tight
- Meta: slate-400 (≈ onSurfaceVariant), 12sp, medium
  - Formato: "Hace X minutos • Y de Z ejercicios"

#### Card Entrenamiento Pasado:
```
Ancho: 256dp (w-64), flex-shrink-0
Fondo: surfaceContainerLow
Forma: rounded-xl
Hover: surfaceContainerHigh, transition, cursor pointer
```

**Imagen:**
- Altura: 128dp
- surfaceContainerHigh fondo
- opacity-40, grayscale (atenuado para pasados)

**Contenido:**
- Nombre: Space Grotesk, 18sp, bold
- Meta: slate-500 (más tenue), 12sp — "Ayer • 65 min • 420 kcal"

> **Mapeo actual:** No existe un carousel de entrenamientos recientes. `TrainPage.kt` tiene un `ActiveWorkoutNotification` para workout activo y la lista de workouts en `WorkoutsScreen.kt`. Este carousel es un componente **completamente nuevo**.

---

### 6. Sección Mis Rutinas

```
Margin bottom: 48dp (mb-12)
```

**Header:**
- Row, space-between, items-center
- Margin bottom: 16dp
- Título: "Mis Rutinas" — Space Grotesk, 20sp, bold, tracking-tight
- Botón "+": 32×32dp, rounded-full, surfaceContainerHigh, icono add 14sp primary

**Routine Cards:**
```
Fondo: surfaceContainerLow (#1B1B20)
Padding: 20dp (p-5)
Forma: rounded-2xl (16dp)
Layout: Row, items-center, gap-5
Borde: 1px white/5
Hover: border primary/30, transition-all, cursor pointer
```

**Imagen rutina:**
- Tamaño: 80×80dp (w-20 h-20)
- Forma: rounded-xl (12dp)
- Overflow: hidden, flex-shrink-0
- object-cover
- Hover: scale-110, transition-transform

**Contenido:**
- Nombre: Space Grotesk, 18sp, bold — ej. "Hypertrophy Max"
- Meta: slate-400, 14sp — ej. "4 días/semana • Intermedio"
- Tags (Row, gap-1):

**Tag individual:**
```
Padding: px-2 py-0.5
Forma: rounded (4dp)
Fuente: 10sp, bold, uppercase, tracking-widest
```

| Tag | Color texto | Fondo |
|-----|------------|-------|
| Hipertrofia | secondary (#D2BBFF) | secondary/10 |
| Pesas | primary (#BAC3FF) | primary/10 |
| Funcional | tertiary (#27E0A9) | tertiary/10 |
| HIIT | primary (#BAC3FF) | primary/10 |

> **Mapeo actual:** Las rutinas se muestran en `RoutinesScreen.kt` como sheet modal. El diseño Stitch integra un preview de rutinas directamente en la pantalla de entrenar, con imágenes y tags que no existen actualmente en el modelo de datos.

---

## Comportamiento e Interacciones

1. **CTA Entrenamiento Libre**: Tap → Abre sheet de Entrenamiento Activo (workout sin rutina asignada)
2. **Card Ejercicios**: Tap → Abre sheet de Lista de Ejercicios 
3. **Card Rutinas**: Tap → Abre sheet de Lista de Rutinas
4. **Carousel "En progreso"**: Tap → Continúa el entrenamiento activo
5. **Carousel pasados**: Tap → Ver detalle del entrenamiento pasado (historial)
6. **"Ver todos"**: Tap → Abre sheet de Historial de Entrenamientos
7. **Routine cards**: Tap → Abre detalle de la rutina o inicia entrenamiento con ella
8. **Botón "+" rutinas**: Tap → Abre diálogo de crear nueva rutina

---

## Diferencias con Implementación Actual

| Aspecto | Actual (`TrainPage.kt`) | Diseño Stitch |
|---------|------------------------|---------------|
| Título | Pequeño, integrado | Grande "Entrenar" 48sp con accent bar |
| CTA principal | "Continuar Entrenamiento" (solo si hay activo) | "Entrenamiento Libre" siempre visible |
| Acceso ejercicios | TextButton simple | Bento card con icono grande |
| Acceso rutinas | TextButton simple | Bento card con icono grande |
| Recientes | No existe | Carousel horizontal con imágenes |
| Rutinas preview | No existe (solo en sheet) | Cards con imagen, meta y tags |
| Imágenes | No hay imágenes | Imágenes decorativas en múltiples secciones |

---

## Plan de Implementación

### Paso 1: Headline + CTA
- Añadir título "Entrenar" con accent bar
- Crear botón gradiente "Entrenamiento Libre" como CTA principal
- Mantener lógica "Continuar Entrenamiento" cuando hay workout activo

### Paso 2: Quick Access Bento
- Reemplazar TextButtons por cards con iconos (Ejercicios/Rutinas)
- Crear composable `BentoCard` reutilizable

### Paso 3: Sección Recientes
- Crear `LazyRow` con cards de entrenamientos recientes
- Card "En Progreso" con badge y datos del workout activo
- Cards pasadas con opacidad reducida y grayscale
- Datos: últimos 5-10 entrenamientos del historial

### Paso 4: Sección Mis Rutinas
- Listar primeras 2-3 rutinas del usuario
- Cards con imagen (opcional — requiere campo imagen en modelo), nombre, frecuencia, tags
- Tags basados en metadata de la rutina (tipo entrenamiento, etc.)

### Paso 5: Imágenes
- **Decisión requerida:** ¿Añadir soporte de imágenes para rutinas/entrenamientos en el modelo de datos?
- Alternativa: usar iconos/colores temáticos en lugar de imágenes fotográficas
- Otra opción: gradientes/patrones generados basados en el tipo de entrenamiento
