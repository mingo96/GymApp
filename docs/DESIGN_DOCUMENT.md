# RutinApp — Documento de Diseño Completo

> Documento de referencia para el rediseño de la interfaz de usuario.
> Contiene TODOS los flujos, pantallas, modelos de datos e interacciones de la aplicación.
> **El sistema de navegación por sheets se mantiene al 100%.**

---

## Tabla de Contenidos

1. [Resumen del Sistema](#1-resumen-del-sistema)
2. [Sistema de Navegación (Sheets)](#2-sistema-de-navegación-sheets)
3. [Páginas Raíz (Root Pager)](#3-páginas-raíz-root-pager)
4. [Sheets — Pantallas Completas](#4-sheets--pantallas-completas)
5. [Diálogos y Modales](#5-diálogos-y-modales)
6. [Flujos de Usuario Detallados](#6-flujos-de-usuario-detallados)
7. [Modelos de Datos](#7-modelos-de-datos)
8. [Estados de UI (State Management)](#8-estados-de-ui-state-management)
9. [Sistema de Temas](#9-sistema-de-temas)
10. [Componentes Reutilizables](#10-componentes-reutilizables)
11. [API y Sincronización](#11-api-y-sincronización)
12. [Resumen de Pantallas](#12-resumen-de-pantallas)

---

## 1. Resumen del Sistema

**RutinApp** es una aplicación móvil de fitness en Android construida con Jetpack Compose y Material3. Permite a los usuarios:

- **Gestionar ejercicios** — crear, editar, buscar, filtrar y vincular ejercicios equivalentes
- **Crear rutinas** — agrupar ejercicios en plantillas reutilizables
- **Entrenar en tiempo real** — cronómetro, registro de series (peso + repeticiones), intercambio de ejercicios
- **Planificar entrenamientos** — asignar rutinas o partes del cuerpo a fechas del calendario
- **Ver estadísticas** — peso máximo (PR), frecuencia, progresión temporal, distribución por días
- **Sistema de entrenadores** — vincular entrenador → recibir planificaciones, otorgar acceso a entrenamientos
- **Notificaciones push** — recordatorios, actualizaciones de entrenador, logros
- **Autenticación** — email + contraseña, Google OAuth, sincronización con backend

### Stack Técnico

| Componente | Tecnología |
|---|---|
| UI | Jetpack Compose + Material3 |
| Navegación | Sheet Stack custom (Trade Republic-style) |
| Estado | ViewModel + LiveData + StateFlow |
| Persistencia local | Room Database v10 (10 entidades) |
| API remota | Retrofit v2 (REST, ~30 endpoints) |
| Sincronización | Bidireccional (local ↔ servidor) |
| DI | Hilt |
| Auth | Firebase (Google) + JWT (email) |
| Push | Firebase Cloud Messaging (FCM) |
| Tema | Dual light/dark, WCAG AA |

---

## 2. Sistema de Navegación (Sheets)

> **ESTE SISTEMA SE MANTIENE 100% EN EL REDISEÑO.**

### 2.1 Arquitectura

La navegación se basa en un **Root Pager horizontal** (3 pestañas fijas) + una **pila de sheets modales** que se apilan verticalmente. No hay navegación tradicional por Activities ni NavGraph.

```
┌──────────────────────────────────────────────────┐
│                 SheetHost                         │  ← Renderiza la pila de sheets
│  ┌────────────────────────────────────────────┐  │
│  │          Sheet N (más reciente)            │  │  ← ModalBottomSheet más arriba
│  │  ┌─────────────────────────────────────┐   │  │
│  │  │       Sheet N-1                     │   │  │
│  │  │  ┌──────────────────────────────┐   │   │  │
│  │  │  │     Sheet 1 (base)           │   │   │  │
│  │  │  │  ┌───────────────────────┐   │   │   │  │
│  │  │  │  │   Root Pager          │   │   │   │  │  ← HorizontalPager (3 tabs)
│  │  │  │  │  [Inicio|Entrenar|…]  │   │   │   │  │
│  │  │  │  └───────────────────────┘   │   │   │  │
│  │  │  └──────────────────────────────┘   │   │  │
│  │  └─────────────────────────────────────┘   │  │
│  └────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

### 2.2 SheetNavigator

Clase que gestiona la pila de sheets con protección contra duplicados.

| Método | Comportamiento |
|---|---|
| `open(destination)` | Apila un nuevo sheet. Si ya está en el tope → ignora (anti-duplicado) |
| `close()` | Cierra el sheet superior (pop) |
| `closeAll()` | Limpia toda la pila → vuelve al root pager |
| `replace(destination)` | Pop + push en una operación |

Propiedades:
- `stack: List<SheetDestination>` — pila de abajo a arriba
- `hasSheets: Boolean` — si hay algún sheet abierto

### 2.3 SheetHost (Renderizado)

Cada sheet en la pila se renderiza como un `ModalBottomSheet` independiente con:

- **Back button** (← ArrowBack) en la esquina superior izquierda
- **Drag handle** centrado
- **Safe area** — `statusBarsPadding()` para evitar overlap con notch/cámara
- **Profundidad visual**:
  - `containerColor = MaterialTheme.colorScheme.surfaceContainer`
  - `scrimColor = Color.Black.copy(alpha = 0.5f)`
  - Borde inferior sutil en el header

### 2.4 SheetDestinations (23 destinos)

#### Gestión de Ejercicios
| Destino | Descripción |
|---|---|
| `ExerciseList` | Directorio completo de ejercicios con búsqueda y filtro |
| `ExerciseDetail(exerciseId)` | Vista de solo lectura de un ejercicio |
| `ExerciseCreate` | Formulario para crear nuevo ejercicio |
| `ExerciseEdit(exerciseId)` | Formulario para editar ejercicio existente |

#### Gestión de Rutinas
| Destino | Descripción |
|---|---|
| `RoutineList` | Lista de rutinas agrupadas por parte del cuerpo |
| `RoutineDetail(routineId)` | Vista de ejercicios de una rutina |
| `RoutineCreate` | Formulario para crear rutina |
| `RoutineEdit(routineId)` | Formulario para editar rutina |

#### Entrenamiento
| Destino | Descripción |
|---|---|
| `WorkoutHistory` | Historial de entrenamientos completados |
| `WorkoutDetail(workoutId)` | Detalle de un entrenamiento finalizado |
| `ActiveWorkout(workoutId)` | Entrenamiento activo con cronómetro y series |
| `StartWorkout(routineId?)` | Iniciar entrenamiento (desde rutina o vacío) |

#### Planificación y Calendario
| Destino | Descripción |
|---|---|
| `PlanningEdit(dateMillis)` | Planificar ejercicio/rutina para una fecha |

#### Usuario y Ajustes
| Destino | Descripción |
|---|---|
| `Settings` | Edición de perfil (nombre, código secreto) |
| `Auth` | Login / registro (email + Google) |
| `Notifications` | Lista de notificaciones con filtros |
| `TrainerManagement` | Gestión de entrenadores vinculados |

#### Estadísticas
| Destino | Descripción |
|---|---|
| `StatsOverview` | Vista general de estadísticas (lista de ejercicios) |
| `ExerciseStats` | Estadísticas detalladas de un ejercicio específico |

---

## 3. Páginas Raíz (Root Pager)

`HorizontalPager` con 3 páginas fijas, barra de navegación en la parte superior.

| Índice | Tab | Icono | Composable |
|---|---|---|---|
| 0 | **Inicio** | `Icons.Outlined.Home` | `HomePage` |
| 1 | **Entrenar** | `Icons.Outlined.FitnessCenter` | `TrainPage` |
| 2 | **Perfil** | `Icons.Outlined.Person` | `ProfilePage` |

Configuración: `beyondViewportPageCount = 1` (precarga páginas adyacentes).

Navegación: swipe horizontal o tap en indicador de pestaña. Animación Spring.

---

### 3.1 Inicio (HomePage)

**Propósito**: Mostrar el plan del día y el calendario semanal.

**Layout actual (emulador)**:
```
┌──────────────────────────────────────┐
│ [🏠 Inicio] [💪 Entrenar] [👤 Perfil] │  ← Indicadores de tab
├──────────────────────────────────────┤
│ Plan de hoy                          │  ← Título (headlineLarge)
│ 07/04/2026                           │  ← Fecha actual (bodyMedium, gris)
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Objetivo                    ›  │   │  ← Card clickable → PlanningEdit
│ │ Nada planeado                  │   │     (o nombre de rutina/parte cuerpo)
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Rango de fechas            ▼   │   │  ← Dropdown expandible
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ Calendario 06/Apr - 13/Apr          │  ← Título calendario semanal
│                                      │
│ 06/Apr │█ Día no planeado       │    │  ← Hoy (sin botón +)
│ 07/Apr │█ Día no planeado    [+]│    │  ← Futuro (con botón +)
│ 08/Apr │█ Día no planeado    [+]│    │
│ 09/Apr │█ Día no planeado    [+]│    │
│ 10/Apr │█ Día no planeado    [+]│    │
│ 11/Apr │█ Día no planeado    [+]│    │
│ 12/Apr │█ Día no planeado    [+]│    │
└──────────────────────────────────────┘
```

**Elementos del día planificado** (cuando tiene contenido):
- Nombre de rutina o parte del cuerpo objetivo
- Icono de entrenador (👤) si fue creado por un entrenador
- Lista de ejercicios planificados con expectativas ("4x8 @ 80kg")

**Interacciones**:
- Tap en "Objetivo" card → Abre `PlanningEdit` sheet para la fecha del día
- Tap en botón [+] de un día → Abre `PlanningEdit` sheet para ese día
- Tap en un día con plan → Abre `PlanningEdit` sheet (edición)
- Dropdown "Rango de fechas" → Cambia la vista del calendario

**Fases del calendario**:
- Periodos nombrados (ej: "Volumen", "Definición") con colores
- Se muestran como franjas de color de fondo en el calendario
- Pueden ser creados por el usuario o asignados por entrenador

---

### 3.2 Entrenar (TrainPage)

**Propósito**: Hub principal para iniciar entrenamientos, ver historial y rutinas.

**Layout actual (emulador)**:
```
┌──────────────────────────────────────┐
│ Entrenar                             │  ← Título (headlineLarge)
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │  ▷  Entrenamiento libre        │   │  ← Botón principal (primary, full-width)
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌──────────────┐ ┌──────────────┐   │
│ │ 💪 Ejercicios │ │ 📋 Rutinas   │   │  ← Cards de acceso rápido
│ └──────────────┘ └──────────────┘   │
├──────────────────────────────────────┤
│ Recientes                       [›] │  ← Sección con "Ver todo" →
│ ┌────────────┐ ┌────────────┐       │     WorkoutHistory sheet
│ │ Sin rutina │ │ Sin rutina │       │
│ │ 06/04/2026 │ │ 06/04/2026 │       │  ← Carousel horizontal
│ │ En progreso│ │ En progreso│       │     de últimos entrenamientos
│ └────────────┘ └────────────┘       │
├──────────────────────────────────────┤
│ Rutinas                         [›] │  ← Sección con "Ver todo" →
│ ┌────────────────────────────────┐   │     RoutineList sheet
│ │        Sin rutinas             │   │
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Interacciones**:
- "Entrenamiento libre" → Crea nuevo `WorkoutModel` vacío → Abre `ActiveWorkout` sheet
- "Ejercicios" card → Abre `ExerciseList` sheet
- "Rutinas" card → Abre `RoutineList` sheet
- Tap en workout reciente:
  - Si `En progreso` → Muestra anuncio → Abre `ActiveWorkout` para continuar
  - Si finalizado → Abre `WorkoutDetail`
- "Ver todo" en Recientes → Abre `WorkoutHistory` sheet
- "Ver todo" en Rutinas → Abre `RoutineList` sheet
- Tap en rutina del carousel → Inicia entrenamiento con esa rutina pre-cargada

**Notas sobre la carga de datos**:
- La sección "Recientes" muestra los últimos 10 entrenamientos
- La sección "Rutinas" muestra rutinas del usuario (no vacías, no públicas ajenas)
- El workout se marca como "En progreso" (verde) si `isFinished = false`

---

### 3.3 Perfil (ProfilePage)

**Propósito**: Acceso a configuración, autenticación y funciones secundarias.

**Layout actual (emulador)**:
```
┌──────────────────────────────────────┐
│ Perfil                               │  ← Título
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ 👤  Usuario                    │   │  ← Avatar + nombre
│ │     @gmail.com                 │   │  ← Email (si autenticado)
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ 🌙 Tema oscuro         [●───] │   │  ← Toggle dark/light theme
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ ⚙️  Ajustes de cuenta       › │   │  → Settings sheet
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ 🔐 Inicio de sesión         › │   │  → Auth sheet
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ 🔔 Notificaciones           › │   │  → Notifications sheet
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ 🏋️ Entrenadores              › │   │  → TrainerManagement sheet
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ 📊 Estadísticas              › │   │  → StatsOverview sheet
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Interacciones**:
- Tema oscuro toggle → Guarda preferencia en DataStore → Aplica inmediatamente
- Cada card → Abre el sheet correspondiente
- Si no hay token → "Inicio de sesión" se muestra. Si autenticado → podría mostrar "Cerrar sesión"

---

## 4. Sheets — Pantallas Completas

### 4.1 Ejercicios (ExerciseListSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │  ← Header del sheet (SheetHost)
├──────────────────────────────────────┤
│ Ejercicios                           │  ← Título
├──────────────────────────────────────┤
│ ┌────────────────────────────── 🔍┐  │
│ │ Buscar ejercicio...             │  │  ← SearchTextField
│ └─────────────────────────────────┘  │
├──────────────────────────────────────┤
│ [Mis ✓] [De otros]            [↻]   │  ← Filtro de propiedad + sync
├──────────────────────────────────────┤
│                                      │
│ ┌────────────────────────────────┐   │
│ │ Plancha isometrica             │   │  ← Card de ejercicio
│ │ Core • Descripción breve      │   │     Tap → ObserveDialog
│ └────────────────────────────────┘   │     LongPress → ModifyDialog
│                                      │
│ ┌────────────────────────────────┐   │
│ │ Bench Press                    │   │
│ │ Pecho • 4x8                   │   │
│ └────────────────────────────────┘   │
│ ...                                  │
│                                      │
│ ┌────────────────────────────────┐   │
│ │  +  Crear ejercicio            │   │  ← Botón principal (primary)
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Comportamiento de filtros**:
- **[Mis]** — Ejercicios creados por el usuario (`isFromThisUser = true`)
- **[De otros]** — Ejercicios públicos de otros usuarios
- **[↻]** — Fuerza sincronización con servidor
- **Búsqueda** — Filtra por nombre o parte del cuerpo (case-insensitive)

**Interacciones con ejercicio**:
- **Tap** → `ObserveExerciseDialog` (vista lectura + botón "Editar")
- **Long-press** → `ModifyExerciseDialog` (edición directa)
- **"Crear ejercicio"** → `CreateExerciseDialog`

---

### 4.2 Rutinas (RoutineListSheet)

**Estructura visual** (idéntica a Ejercicios):
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Rutinas                              │
├──────────────────────────────────────┤
│ ┌────────────────────────────── 🔍┐  │
│ │ Buscar rutina...                │  │
│ └─────────────────────────────────┘  │
├──────────────────────────────────────┤
│ [Mis ✓] [De otros]            [↻]   │
├──────────────────────────────────────┤
│                                      │
│ No tienes rutinas aún                │  ← Estado vacío
│                                      │
│ ┌────────────────────────────────┐   │
│ │  +  Crear rutina               │   │
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Cuando hay rutinas** — se agrupan por parte del cuerpo:
```
│ Pecho                     [Ver todo] │
│ ┌────────────┐ ┌────────────┐       │
│ │ Classic    │ │ Pyramid    │       │  ← Carousel por grupo
│ │ (5 ejerc)  │ │ (4 ejerc)  │       │
│ └────────────┘ └────────────┘       │
│                                      │
│ Espalda                   [Ver todo] │
│ ┌────────────┐ ┌────────────┐       │
│ │ Hipertrofia│ │ Crossfit   │       │
│ └────────────┘ └────────────┘       │
```

**Interacciones con rutina**:
- **Tap** → `ObserveRoutineDialog` (ver ejercicios que la componen)
- **Long-press** → `EditRoutineDialog` (editar nombre, ejercicios)
- **"Crear rutina"** → `CreateRoutineDialog`

---

### 4.3 Entrenamiento Activo (ActiveWorkoutSheet)

**Propósito**: Pantalla principal durante un entrenamiento. Cronómetro + registro de series.

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│                                      │
│           18:36:00                   │  ← DigitalWatch (HH:MM:SS)
│                                      │     Tiempo transcurrido en tiempo real
├──────────────────────────────────────┤
│ Ejercicios disponibles               │
│ ┌────────────────────────────────┐   │
│ │ Plancha isometrica          [+]│   │  ← Ejercicio sin series todavía
│ └────────────────────────────────┘   │     [+] = Añadir serie
│                                      │
├──────────────────────────────────────┤  
│ (Si hay ejercicios con series:)      │
│ ┌────────────────────────────────┐   │
│ │ ▼ Bench Press (3 series)       │   │  ← Expandible
│ │   ├ 80kg × 8  "RPE 7"         │   │  ← Serie registrada
│ │   ├ 85kg × 6  "Máx esfuerzo"  │   │
│ │   └ 80kg × 8  ""              │   │
│ │   [+ Añadir serie]            │   │
│ │   [🔄 Intercambiar]           │   │
│ └────────────────────────────────┘   │
│                                      │
│ ┌────────────────────────────────┐   │
│ │  Finalizar entrenamiento       │   │  ← Botón (primary, full-width)
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Secciones de ejercicios**:
1. **Rutina base** — Ejercicios que venían de la rutina seleccionada (si aplica)
2. **Otros ejercicios** — Ejercicios añadidos manualmente durante el entrenamiento
3. **Ejercicios disponibles** — Ejercicios que pueden añadirse (aún sin series)

**Interacciones**:
- **[+] Añadir serie** → Abre `SetEditionDialog` (peso, reps, notas)
- **Tap en serie** → Editar serie existente
- **Swipe/delete en serie** → Eliminar serie
- **[🔄 Intercambiar]** → Abre diálogo de selección para reemplazar ejercicio
- **"Finalizar entrenamiento"** → `isFinished = true` → sync → cierra sheet

---

### 4.4 Historial de Entrenamientos (WorkoutHistorySheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Historial                            │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Sin rutina                     │   │  ← Card de entrenamiento
│ │ 06/04/2026                     │   │
│ │ En progreso                    │   │  ← Estado (verde = en progreso)
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ Pecho + Tríceps                │   │
│ │ 02/04/2026                     │   │
│ │ Finalizado                     │   │
│ └────────────────────────────────┘   │
│ ...                                  │
│                                      │
│ ┌────────────────────────────────┐   │
│ │  ▷  Entrenar sin rutina        │   │  ← Inicia nuevo workout vacío
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Interacciones**:
- Tap en workout "En progreso" → Muestra anuncio → Abre `ActiveWorkout`
- Tap en workout "Finalizado" → Abre `WorkoutDetail`
- "Entrenar sin rutina" → Crea workout vacío → Abre `ActiveWorkout`

---

### 4.5 Planificación (PlanningEditSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Planificación                        │
│ Objetivo el 06/04/2026              │  ← Fecha del plan
├──────────────────────────────────────┤
│ ¿Qué quieres planificar?            │
│                                      │
│    🧍 Parte del cuerpo     📋 Rutina │  ← Dos opciones con iconos
│                                      │
├──────────────────────────────────────┤
│ (Si seleccionó "Parte del cuerpo":) │
│ [Seleccionar parte ▼]               │  ← Dropdown (Pecho, Espalda, etc.)
│                                      │
│ (Si seleccionó "Rutina":)           │
│ [Seleccionar rutina ▼]              │  ← Dropdown de rutinas del user
│                                      │
│ (Opcional:)                          │
│ [Recordatorio a las __:__]          │  ← Time picker
│                                      │
│ ┌────────────────────────────────┐   │
│ │         Cerrar                 │   │  ← Botón (primary, full-width)
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Comportamiento**:
- Si la planificación fue creada por un **entrenador** → Solo lectura (no editable)
- Si la creó el **usuario** → Editable
- Al guardar → Se sincroniza con el servidor
- El "Cerrar" actúa como "Guardar y cerrar" si hubo cambios

**Planificación de entrenador con ejercicios**:
```
│ Ejercicios del plan:                 │
│ 1. Bench Press — "4x10 @ 80kg"      │
│ 2. Incline Press — "3x12"           │
│ 3. Cable Fly — "3x15 RPE 8"         │
```

---

### 4.6 Ajustes de Cuenta (SettingsSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Ajustes de cuenta                    │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Nombre                         │   │
│ │ [____________________]         │   │  ← TextField editable
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ Código secreto                 │   │
│ │ [____________________]         │   │  ← Para invitaciones de entrenador
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │         Guardar                │   │  ← Botón primary
│ └────────────────────────────────┘   │
│                                      │
│ Cambiar tema         [●───]          │  ← Toggle dark/light
└──────────────────────────────────────┘
```

---

### 4.7 Autenticación (AuthSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Inicio de sesión                     │  ← (o "Registro" si toggle)
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Correo                         │   │
│ │ [usuario@gmail.com___________] │   │  ← Email validado
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ Contraseña                     │   │
│ │ [************************]     │   │  ← Password (min 5 chars registro)
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────┐  [↔]                  │  ← Swap login ↔ registro
│ │ Iniciar    │                       │
│ │ sesión     │                       │
│ └────────────┘                       │
│ ┌────────────────────────────────┐   │
│ │  G  Google                     │   │  ← Google OAuth
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Flujo de autenticación**:
1. Email + contraseña → `POST /auth/login` o `/auth/register`
2. Google → OAuth token → `POST /auth/google`
3. Respuesta: JWT `access_token` + datos de usuario
4. Token guardado en DataStore → Se usa en todas las peticiones API
5. Éxito → Cierra sheet → Perfil se actualiza automáticamente

---

### 4.8 Notificaciones (NotificationsSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Notificaciones                       │
├──────────────────────────────────────┤
│ [Todas] [No leídas (0)] [Leídas]    │  ← Filtros como chips
├──────────────────────────────────────┤
│                                      │
│           🔔                         │  ← Icono campana (empty state)
│    No tienes notificaciones          │
│                                      │
│ (Con notificaciones:)                │
│ ┌────────────────────────────────┐   │
│ │ Nuevo plan de tu entrenador    │   │  ← Título notificación
│ │ Tu entrenador ha planificado...│   │  ← Cuerpo
│ │ hace 2 horas                   │   │  ← Timestamp
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Interacciones**:
- Filtros: `ALL | UNREAD | READ`
- "Marcar todas como leídas" (botón contextual)
- Swipe para eliminar notificación individual
- Tap para marcar como leída
- Sync automático al abrir

---

### 4.9 Entrenadores (TrainerManagementSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Entrenadores                         │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ ✓ Notificaciones activadas     │   │  ← Estado de permisos de push
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Canjea un código de invitación │   │
│ │ para vincular a tu entrenador  │   │
│ │                                │   │
│ │ Código de invitación           │   │
│ │ [____________________]         │   │  ← TextField para código
│ │                                │   │
│ │ ┌──────────────────────────┐   │   │
│ │ │        Canjear           │   │   │  ← Botón primary
│ │ └──────────────────────────┘   │   │
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ No hay entrenadores vinculados       │  ← Empty state
│                                      │
│ (Con entrenadores:)                  │
│ ┌────────────────────────────────┐   │
│ │ Juan García (Entrenador)       │   │
│ │ Estado: Aprobado               │   │
│ │ [Bloquear] [Revocar]          │   │
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

---

### 4.10 Estadísticas General (StatsOverviewSheet)

**Estructura visual**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Estadísticas                         │
├──────────────────────────────────────┤
│ ┌────────────────────────────── 🔍┐  │
│ │ Buscar ejercicio...             │  │
│ └─────────────────────────────────┘  │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Bench Press                    │   │  ← Tap → ExerciseStats sheet
│ │ Pecho • 42 series              │   │
│ └────────────────────────────────┘   │
│ ┌────────────────────────────────┐   │
│ │ Squat                          │   │
│ │ Piernas • 38 series            │   │
│ └────────────────────────────────┘   │
│ ...                                  │
└──────────────────────────────────────┘
```

**Interacciones**:
- Búsqueda por nombre de ejercicio
- Tap en ejercicio → Abre `ExerciseStats` sheet con detalles

---

### 4.11 Estadísticas de Ejercicio (ExerciseStatsSheet)

**Estado con datos**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Bench Press                          │  ← Nombre del ejercicio
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │ Peso máximo:  100 kg           │   │  ← PR day + notes
│ │ Fecha: 5 ago 2024              │   │
│ │ Peso medio:   75.5 kg          │   │
│ │ Veces hecho:  42 series        │   │
│ │ Última vez:   3/Abr 14:30      │   │
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │   [Gráfico de Línea]          │   │  ← Progresión de peso (timeline)
│ │   80─────85───90──100          │   │     X = fecha, Y = peso
│ └────────────────────────────────┘   │
├──────────────────────────────────────┤
│ ┌────────────────────────────────┐   │
│ │   [Gráfico Circular]          │   │  ← Distribución por día
│ │   Lunes 28%  Miércoles 32%    │   │
│ │   Viernes 25%  Sábado 15%     │   │
│ └────────────────────────────────┘   │
└──────────────────────────────────────┘
```

**Estado vacío (ejercicio sin series)**:
```
┌──────────────────────────────────────┐
│ [←]  ─────  (drag handle)           │
├──────────────────────────────────────┤
│ Plancha isometrica                   │  ← Siempre muestra nombre
├──────────────────────────────────────┤
│                                      │
│            🏋️                        │  ← Icono FitnessCenter (64dp, 0.4 alpha)
│                                      │
│  Aún no has hecho este ejercicio     │  ← bodyLarge, onSurfaceVariant
│                                      │
│  Las estadísticas aparecerán cuando  │  ← bodyMedium, más sutil
│  lo incluyas en un entrenamiento     │
│                                      │
└──────────────────────────────────────┘
```

---

## 5. Diálogos y Modales

### 5.1 Crear Ejercicio (CreateExerciseDialog)

**Campos**:
- Nombre del ejercicio (obligatorio)
- Descripción (opcional)
- Parte del cuerpo objetivo — dropdown (Pecho, Espalda, Piernas, Hombros, Brazos, Core, Cardio, Full Body)
- Sets y reps (texto libre, ej: "4x8")
- Observaciones (notas adicionales)
- Tipo de repeticiones — "base" | "drop" | "rpe"
- Tipo de peso — "base" | "progressive" | "regressive"

**Botones**: [Cancelar] [Crear]

---

### 5.2 Observar Ejercicio (ObserveExerciseDialog)

**Muestra** (solo lectura):
- Nombre, descripción, parte del cuerpo
- Observaciones
- Ejercicios equivalentes vinculados
- Botón [Editar] → Abre `ModifyExerciseDialog`

---

### 5.3 Modificar Ejercicio (ModifyExerciseDialog)

Mismos campos que `CreateExerciseDialog` pero pre-rellenados. Incluye:
- Botón [Guardar cambios]
- Botón [Eliminar relación] para desvincular ejercicios equivalentes
- Botón [+ Añadir relación] → Abre `AddRelationsDialog`

---

### 5.4 Añadir Relaciones (AddRelationsDialog)

- Lista de ejercicios que comparten la misma parte del cuerpo
- Multi-selección
- Al confirmar → crea vínculos bidireccionales

---

### 5.5 Crear/Editar Rutina (CreateRoutineDialog / EditRoutineDialog)

**Campos**:
- Nombre de la rutina
- Parte del cuerpo principal
- Lista de ejercicios (añadir/quitar de la lista de ejercicios del usuario)

---

### 5.6 Edición de Serie (SetEditionDialog)

**Campos**:
- Peso (kg) — numérico con decimales
- Repeticiones — numérico entero
- Observaciones — texto libre ("RPE 8", "Máx esfuerzo", etc.)

**Botones**: [Cancelar] [Guardar]

---

### 5.7 Intercambio de Ejercicio (ExerciseSwapDialog)

- Lista de ejercicios disponibles (no incluidos ya en el workout)
- Tap → reemplaza el ejercicio actual en el workout
- Mantiene las series ya registradas (re-asignadas al nuevo ejercicio)

---

## 6. Flujos de Usuario Detallados

### 6.1 Flujo: Empezar un Entrenamiento Libre

```
1. Usuario está en "Entrenar"
2. Tap "Entrenamiento libre"
3. ViewModel crea nuevo WorkoutModel (isFinished=false, vacío)
4. Se abre ActiveWorkout sheet
5. Cronómetro empieza a contar
6. Usuario ve la lista de "Ejercicios disponibles"
7. Tap [+] en un ejercicio → SetEditionDialog
8. Introduce peso + reps + notas → Guardar
9. Serie aparece bajo el ejercicio
10. Repite para más series / ejercicios
11. Tap "Finalizar entrenamiento"
12. isFinished = true → Sync con servidor
13. Sheet se cierra → Vuelve a TrainPage
14. Workout aparece en "Recientes" como "Finalizado"
```

### 6.2 Flujo: Empezar Entrenamiento con Rutina

```
1. Usuario está en "Entrenar"
2. Tap en card de rutina en el carousel → (o TrainPage > Rutina)
3. ViewModel crea WorkoutModel con baseRoutine = rutina seleccionada
4. ActiveWorkout sheet muestra ejercicios de la rutina pre-cargados
5. Usuario registra series para cada ejercicio
6. Puede añadir ejercicios extra no incluidos en la rutina
7. Puede intercambiar ejercicios con equivalentes
8. Finaliza entrenamiento
```

### 6.3 Flujo: Continuar Entrenamiento en Progreso

```
1. Usuario ve workout "En progreso" en Recientes (TrainPage)
2. Tap en el workout
3. Se muestra un anuncio (AdMob)
4. Tras cerrar anuncio → Se abre ActiveWorkout
5. ViewModel carga el workout existente con todas sus series
6. Cronómetro continúa desde la fecha de inicio
7. Usuario puede seguir añadiendo series
8. Finaliza cuando termina
```

### 6.4 Flujo: Crear un Ejercicio

```
1. Ejercicios sheet → "Crear ejercicio"
2. Se abre CreateExerciseDialog
3. Rellena: nombre, descripción, parte del cuerpo
4. Opcionalmente: sets/reps, tipo reps, tipo peso
5. "Crear" → Se guarda en Room + se marca como dirty
6. En próximo sync → Se sube al servidor
7. Aparece en la lista de ejercicios del usuario
```

### 6.5 Flujo: Planificar un Día

```
1. HomePage → Tap [+] en un día del calendario
2. Se abre PlanningEditSheet para esa fecha
3. "¿Qué quieres planificar?"
4. Opción A: "Parte del cuerpo" → Selecciona (Pecho, Espalda, etc.)
5. Opción B: "Rutina" → Selecciona una rutina existente
6. Opcionalmente: configura recordatorio
7. "Guardar" → Se almacena en Room + sync
8. El día en el calendario muestra el plan
9. El card "Objetivo" del día muestra la rutina/parte del cuerpo
```

### 6.6 Flujo: Vincular Entrenador

```
1. Perfil → "Entrenadores"
2. TrainerManagementSheet se abre
3. Introduce código de invitación del entrenador
4. "Canjear" → POST /trainers/redeem-invite/{code}
5. Se crea TrainerRelation (status: "pending")
6. El entrenador recibe notificación y aprueba/rechaza
7. Si aprobado → Entrenador aparece en la lista
8. El entrenador puede ahora:
   a. Crear planificaciones para el usuario
   b. Ver resultados de entrenamientos (si se otorga permiso)
9. Las planificaciones del entrenador aparecen en el calendario con icono 👤
10. El usuario NO puede editar planificaciones del entrenador
```

### 6.7 Flujo: Ver Estadísticas de un Ejercicio

```
1. Perfil → "Estadísticas"
2. StatsOverviewSheet muestra lista de ejercicios con buscador
3. Tap en "Bench Press"
4. ExerciseStatsSheet calcula:
   - Peso máximo (PR) con fecha y notas
   - Veces hecho (total de series)
   - Peso medio
   - Última vez realizado
   - Gráfico de progresión temporal (línea)
   - Distribución por día de la semana (circular)
5. Si no tiene series → Muestra estado vacío con icono y mensaje
```

### 6.8 Flujo: Autenticación

```
A) Email + Contraseña:
1. Perfil → "Inicio de sesión"
2. AuthSheet se abre
3. Introduce email (validación Pattern.EMAIL)
4. Introduce contraseña
5. Si "Iniciar sesión" → POST /auth/login
6. Si "Registrarse" (toggle con ↔) → POST /auth/register (contraseña min 5 chars)
7. Respuesta: access_token + datos user
8. Token → DataStore → Se usa en todas las API calls
9. Sheet se cierra → Perfil actualizado

B) Google:
1. Tap "Google"
2. Google Sign-In → OAuth token
3. POST /auth/google con ID token
4. Backend valida con Firebase → Emite JWT
5. JWT guardado → App autenticada
```

### 6.9 Flujo: Sincronización Bidireccional

```
Para cada entidad (ejercicios, rutinas, workouts, planning):

SUBIDA:
1. Al crear/editar localmente → isDirty = true
2. En autoSync() o sync manual:
   a. Recopilar entidades dirty
   b. POST /sync/{entity} con { created[], updated[], deleted_ids[] }
   c. Servidor devuelve { created_mappings, server_updates, confirmed_deletions }
   d. Actualizar IDs locales con server IDs
   e. isDirty = false

DESCARGA:
1. GET /{entities} con timestamp del último sync
2. Insertar/actualizar entidades nuevas del servidor
3. Resolver conflictos (server wins por defecto)
```

---

## 7. Modelos de Datos

### 7.1 ExerciseModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | ID local (Room) |
| `realId` | Long | ID del servidor |
| `name` | String | Nombre del ejercicio |
| `description` | String | Descripción detallada |
| `targetedBodyPart` | String | Parte del cuerpo ("Pecho", "Piernas", etc.) |
| `equivalentExercises` | List\<ExerciseModel\> | Ejercicios equivalentes vinculados |
| `setsAndReps` | String | Texto libre (ej: "4x8") |
| `observations` | String | Notas adicionales |
| `isFromThisUser` | Boolean | Propio vs público |
| `repsType` | String | "base" \| "drop" \| "rpe" |
| `weightType` | String | "base" \| "progressive" \| "regressive" |

### 7.2 WorkoutModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Int | ID local (Room autoGenerate) |
| `realId` | Long | ID del servidor |
| `baseRoutine` | RoutineModel? | Rutina usada (null si libre) |
| `exercisesAndSets` | MutableList\<Pair\<ExerciseModel, MutableList\<SetModel\>\>\> | Ejercicios con series |
| `date` | Date | Fecha del entrenamiento |
| `title` | String | Título (nombre rutina o "Sin rutina") |
| `isFinished` | Boolean | Si está completado |

### 7.3 SetModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Int | ID local |
| `weight` | Double | Peso en kg |
| `exercise` | ExerciseModel? | Ejercicio asociado |
| `reps` | Int | Repeticiones |
| `date` | Date | Fecha/hora de la serie |
| `observations` | String | Notas ("RPE 8", "Máx esfuerzo") |

### 7.4 RoutineModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Int | ID local |
| `realId` | Int | ID del servidor |
| `name` | String | Nombre de la rutina |
| `targetedBodyPart` | String | Parte del cuerpo principal |
| `exercises` | MutableList\<ExerciseModel\> | Ejercicios que componen la rutina |
| `isFromThisUser` | Boolean | Propia o pública |

### 7.5 PlanningModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Int | ID local |
| `realId` | Long | ID del servidor |
| `date` | Date | Fecha planificada |
| `statedRoutine` | RoutineModel? | Rutina planificada (o null) |
| `statedBodyPart` | String? | Parte del cuerpo (o null) |
| `reminderTime` | String? | Hora de recordatorio ("HH:mm") |
| `planningExercises` | List\<PlanningExerciseModel\> | Ejercicios específicos (entrenador) |
| `createdByUserId` | Long? | 0 = usuario, otro = ID entrenador |
| `isFromTrainer` | Boolean | Derivado de createdByUserId |

### 7.6 PlanningExerciseModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | ID |
| `exerciseId` | Long | ID del ejercicio |
| `exerciseName` | String | Nombre para display |
| `expectationText` | String? | Expectativa ("4x10 @ 80kg") |
| `position` | Int | Orden en el plan |
| `notes` | String? | Notas del entrenador |

### 7.7 CalendarPhaseModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Int | ID local |
| `serverId` | Long | ID servidor |
| `name` | String | Nombre de la fase ("Volumen", "Definición") |
| `color` | String | Color hex ("#FF5733") |
| `startDate` | Date | Inicio de la fase |
| `endDate` | Date | Fin de la fase |
| `notes` | String? | Notas |
| `visibility` | String | "private" \| "shared_with_trainers" |
| `createdByUserId` | Long? | Creador |

### 7.8 TrainerRelationModel

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | ID |
| `trainerUserId` | Long | ID usuario entrenador |
| `clientUserId` | Long | ID usuario cliente |
| `status` | String | "pending" \| "approved" \| "blocked" \| "revoked" |
| `notes` | String? | Notas |

### 7.9 Grants (Permisos)

**PlanningGrantModel**: Permiso para que el entrenador vea/edite planificaciones.
- `accessType`: "view" | "edit"
- `dateFrom` / `dateTo`: Rango de fechas con acceso
- `isActive`: Si el permiso está vigente

**WorkoutVisibilityGrantModel**: Permiso para que el entrenador vea resultados.
- `canViewResults`: Boolean
- Rango de fechas y estado activo

### 7.10 AppNotificationEntity

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Int | ID local |
| `serverId` | Long | ID servidor |
| `title` | String | Título de la notificación |
| `body` | String | Cuerpo/contenido |
| `type` | String | "trainer_planning" \| "coach_invitation" etc. |
| `readAt` | String? | ISO timestamp (null = no leída) |
| `createdAt` | String | ISO timestamp |

---

## 8. Estados de UI (State Management)

Cada ViewModel expone sealed interfaces/classes para sus estados de UI:

### 8.1 MainScreenState
```
Observation         → Vista normal del calendario
PlanningOnMainFocus → Editando una planificación (con campo y rutinas disponibles)
```

### 8.2 WorkoutsScreenState
```
Observe           → Vista normal (con planning del día si existe)
WorkoutStarted    → Entrenamiento activo:
                     - workout: WorkoutModel completo
                     - otherExercises: ejercicios disponibles para añadir
                     - setBeingCreated: SetState? (diálogo de serie abierto)
                     - exerciseBeingSwapped: ExerciseModel? (diálogo de swap)
```

### 8.3 ExercisesState
```
Observe               → Lista de ejercicios (con posible ejercicio seleccionado)
Creating              → Diálogo de creación abierto
Modifying             → Diálogo de edición abierto
AddingRelations       → Diálogo de vinculación abierto
SearchingForExercise  → Modo búsqueda con resultados
ExploringExercises    → Modo exploración pública
```

### 8.4 StatsScreenState
```
Observation      → Lista de ejercicios para elegir
StatsOfExercise  → Estadísticas calculadas:
                    - hasBeenDone: Boolean
                    - exercise: ExerciseModel
                    - highestWeight: Triple<Double, Date, String>
                    - timesDone: Int
                    - averageWeight: Double
                    - lastTimeDone: String
                    - weights: List<Double> (timeline)
                    - daysDone: List<Pair<String, Double>> (distribución)
```

### 8.5 SettingsScreenState
```
UserData → Datos del usuario cargados (editable)
LogIn    → Formulario de login/registro visible
```

---

## 9. Sistema de Temas

### 9.1 Paleta de Colores

#### Tema Oscuro (por defecto — ambiente gym)
| Rol | Color | Hex |
|---|---|---|
| Primary | Azul | `#4361EE` |
| Secondary | Púrpura | `#7B2FF7` |
| Tertiary | Verde/Teal | `#06D6A0` |
| Background | Casi negro | `#0F0F14` |
| Surface | Gris muy oscuro | `#1A1A24` |
| SurfaceContainer | Gris oscuro elevado | (un poco más claro que surface) |
| Error | Rojo | `#EF476F` |
| Warning | Ámbar | `#FFB703` |
| Success | Verde | `#06D6A0` |
| Info | Cyan | `#4CC9F0` |

#### Tema Claro
| Rol | Color | Hex |
|---|---|---|
| Primary | Azul oscuro | `#3B52CC` |
| Secondary | Púrpura oscuro | `#6519CF` |
| Tertiary | Verde oscuro | `#05B384` |
| Background | Gris muy claro | `#F5F5FA` |
| Surface | Blanco | `#FFFFFF` |
| Error | Rojo oscuro | `#BA1A40` |
| Warning | Ámbar oscuro | `#E5A400` |
| Success | Verde oscuro | `#05B384` |

### 9.2 Cumplimiento WCAG
- Contraste AA (4.5:1) para texto sobre fondo
- Contraste AAA (3:1) para texto grande

### 9.3 Tipografía
Material3 estándar: displayLarge → labelSmall

### 9.4 Formas
- Small: 8dp radius
- Medium: 12dp radius
- Large: 16dp radius

---

## 10. Componentes Reutilizables

### 10.1 Componentes de Datos
| Componente | Descripción |
|---|---|
| `RutinAppCalendar` | Calendario mensual con fases coloreadas |
| `RutinAppLineChart` | Gráfico de línea (progresión de peso) |
| `RutinAppPieChart` | Gráfico circular (distribución por día) |
| `DigitalWatch` | Cronómetro en tiempo real (HH:MM:SS) |

### 10.2 Componentes de UI
| Componente | Descripción |
|---|---|
| `RutinAppBottomBar` | Barra de navegación superior (3 tabs) |
| `SearchTextField` | Input de búsqueda con icono y clear |
| `OwnershipFilterRow` | Chips [Mis] [De otros] con toggle |
| `TextFieldWithTitle` | TextField con etiqueta superior |
| `EmptyStateMessage` | Placeholder "No hay resultados" |
| `LoadingIndicator` | Spinner circular de carga |
| `AnimatedItem` | Animación staggered de aparición |
| `AdjustableText` | Texto con sizing responsive |
| `DialogContainer` | Wrapper estilizado para diálogos |
| `FABComposable` | Floating action buttons |

---

## 11. API y Sincronización

### 11.1 Endpoints Principales

**Base URL**: `https://rutynapp.com/api/v2/`

| Área | Endpoints clave |
|---|---|
| **Auth** | `POST login`, `POST register`, `POST google`, `GET me`, `POST logout` |
| **Ejercicios** | `GET/POST/PUT/DELETE /exercises`, `POST /sync/exercises` |
| **Rutinas** | `GET/POST/PUT/DELETE /routines`, `POST /sync/routines` |
| **Workouts** | `GET/POST/PUT/DELETE /workouts`, `POST /sync/workouts` |
| **Planning** | `GET/POST/PUT/DELETE /planning` |
| **Entrenadores** | `POST redeem-invite`, `GET my-relations`, `POST approve/block` |
| **Notificaciones** | `GET notifications`, `POST read`, `DELETE`, `POST fcm/register-token` |

### 11.2 Patrón de Sincronización

```
Cliente → Servidor:
{
  "client_timestamp": "...",
  "created": [...],
  "updated": [...],
  "deleted_ids": [...]
}

Servidor → Cliente:
{
  "data": {
    "created_mappings": [{"localId": ..., "serverId": ...}],
    "server_updates": [...],
    "server_created": [...],
    "confirmed_deletions": [...]
  }
}
```

### 11.3 Autenticación API
- Bearer token JWT en header `Authorization`
- Automático via `AuthInterceptor` (OkHttp)
- Timeout: 120 segundos (read/write/connect)

---

## 12. Resumen de Pantallas

| # | Pantalla | Tipo | ViewModel | Acciones principales |
|---|---|---|---|---|
| 1 | **Inicio** | Root Tab | MainScreenViewModel | Ver plan del día, calendario, abrir planning |
| 2 | **Entrenar** | Root Tab | WorkoutsViewModel | Iniciar workout, ver recientes, acceso a ejercicios/rutinas |
| 3 | **Perfil** | Root Tab | SettingsViewModel | Toggle tema, acceso a ajustes/auth/stats/trainers |
| 4 | **Ejercicios** | Sheet | ExercisesViewModel | CRUD ejercicios, buscar, filtrar, vincular equivalentes |
| 5 | **Rutinas** | Sheet | RoutinesViewModel | CRUD rutinas, buscar, filtrar, componer con ejercicios |
| 6 | **Entrenamiento Activo** | Sheet | WorkoutsViewModel | Cronómetro, registrar series, añadir/intercambiar ejercicios |
| 7 | **Historial** | Sheet | WorkoutsViewModel | Ver entrenamientos pasados, continuar en progreso |
| 8 | **Planificación** | Sheet | MainScreenViewModel | Asignar rutina/parte cuerpo a fecha, recordatorio |
| 9 | **Ajustes** | Sheet | SettingsViewModel | Editar nombre, código, tema |
| 10 | **Auth** | Sheet | SettingsViewModel | Login/registro email, Google OAuth |
| 11 | **Notificaciones** | Sheet | NotificationsViewModel | Ver, filtrar, marcar como leídas, eliminar |
| 12 | **Entrenadores** | Sheet | SettingsViewModel | Canjear código, ver entrenadores, gestionar permisos |
| 13 | **Estadísticas** | Sheet | StatsViewModel | Buscar ejercicio, ver lista con totales |
| 14 | **Stats Ejercicio** | Sheet | StatsViewModel | PR, media, frecuencia, gráficos línea/circular |

---

## Notas para el Diseño

1. **Sistema de sheets INTOCABLE** — La navegación por pila de ModalBottomSheet se mantiene. Cada sheet tiene: back button (←), drag handle centrado, safe area, scrim oscuro.

2. **3 tabs fijas** — Inicio, Entrenar, Perfil. Swipe horizontal + indicador superior.

3. **Patrón de listado consistente** — Ejercicios y Rutinas comparten: búsqueda + filtros [Mis/De otros] + sync + lista + botón crear.

4. **Diálogos dentro de sheets** — Los formularios de crear/editar/observar se abren como diálogos modales DENTRO del sheet, no como nuevos sheets.

5. **Entrenamiento como experiencia central** — El cronómetro DigitalWatch y el registro de series son el flujo más crítico. Debe ser fluido y rápido.

6. **Planificación dual** — El usuario puede planificar por rutina O por parte del cuerpo. El entrenador puede asignar ejercicios específicos con expectativas.

7. **Estadísticas visuales** — Gráfico de línea (progresión temporal) + gráfico circular (distribución por día).

8. **Tema dual** — Oscuro por defecto (gym). Claro disponible. WCAG AA obligatorio.

9. **Anuncios** — AdMob se muestra antes de continuar un workout en progreso.

10. **Estados vacíos** — Todas las pantallas manejan estado vacío con icono + mensaje descriptivo.
