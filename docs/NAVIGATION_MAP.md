# Mapa de Navegación — RutinApp Android

## Arquitectura de Navegación

RutinApp usa un sistema de navegación por **sheets apilados** (Trade Republic-style):

- **3 páginas raíz** en `HorizontalPager`: Home, Entrenar, Perfil
- **18 destinos sheet** (`ModalBottomSheet`) apilados con alturas progresivas (93% → 90% → 87%...)
- Gestión de pila via `SheetNavigator` (stack-based, con manejo de back button)

## Stitch → Android Mapping

| Stitch Screen | Android Destino | ViewModel | Endpoints API | Estado |
|---|---|---|---|---|
| Home/Inicio (70ee9791) | Root Pager Page 0 (`HomePage`) | `MainScreenViewModel` | `GET planning/today`, sync/planning, sync/calendar-phases, stats/summary | Parcial |
| Rutinas list (b0cbf11a) | `SheetDestination.RoutineList` | `RoutinesViewModel` | `GET routines`, sync/routines | Implementado |
| Ejercicios list (d93c8637) | `SheetDestination.ExerciseList` | `ExercisesViewModel` | `GET exercises`, sync/exercises | Implementado |
| Exercise Detail (831adef0) | `SheetDestination.ExerciseDetail(id)` | `ExercisesViewModel` | `GET exercises/{id}`, `GET exercises/{id}/related` | Implementado |
| Exercise Stats (4ced95d1) | `SheetDestination.ExerciseStats` | `StatsViewModel` | `GET exercises/{id}/last-mark`, sets locales | Implementado |
| Profile (296b1363) | Root Pager Page 2 (`ProfilePage`) | `SettingsViewModel` | `GET auth/me`, `POST auth/logout` | Parcial |
| Active Training (190a8afb) | `SheetDestination.ActiveWorkout(id)` | `WorkoutsViewModel` | workouts CRUD, sets CRUD | Parcial |
| Entrenar tab (ad316c4e) | Root Pager Page 1 (`TrainPage`) | `WorkoutsViewModel` + `ExercisesVM` + `RoutinesVM` | workouts, routines, exercises | Implementado |
| Onboarding (a31ce0fb) | `Routes.ONBOARDING` (ruta NavGraph) | `SettingsViewModel` | Ninguno (local) | No implementado |
| Login (e7c91368) | `SheetDestination.Auth` | `SettingsViewModel` | auth/login, auth/register, auth/google | Parcial |

## Destinos Sheet Completos

| Destino | Descripción | Parámetros |
|---------|-------------|------------|
| `ExerciseList` | Lista de ejercicios con búsqueda y filtros | — |
| `ExerciseDetail` | Detalle de un ejercicio | `exerciseId: Long` |
| `ExerciseCreate` | Crear nuevo ejercicio | — |
| `ExerciseEdit` | Editar ejercicio | `exerciseId: Long` |
| `ExerciseStats` | Estadísticas de un ejercicio | — |
| `RoutineList` | Lista de rutinas | — |
| `RoutineDetail` | Detalle de rutina con ejercicios | `routineId: Long` |
| `RoutineCreate` | Crear nueva rutina | — |
| `RoutineEdit` | Editar rutina | `routineId: Long` |
| `WorkoutHistory` | Historial de entrenamientos | — |
| `WorkoutDetail` | Detalle de un entrenamiento | `workoutId: Long` |
| `ActiveWorkout` | Entrenamiento activo | `workoutId: Long` |
| `StartWorkout` | Iniciar entrenamiento | `routineId: Long?` |
| `PlanningEdit` | Editar planificación para fecha | `dateMillis: Long` |
| `Settings` | Configuración general | — |
| `AppConfig` | Configuración de la app | — |
| `Auth` | Login/registro | — |
| `Notifications` | Centro de notificaciones | — |
| `TrainerManagement` | Gestión de entrenador | — |
| `StatsOverview` | Estadísticas generales | — |

## Reglas de Back Stack

1. **Dismiss sheet** = pop del stack (vuelve al sheet anterior o raíz)
2. **Back button** del sistema = dismiss del sheet actual
3. **Profundidad máxima** recomendada: 3 sheets (raíz → lista → detalle)
4. **Navegación lateral** solo en pager raíz (swipe entre Home/Entrenar/Perfil)

## Gaps Identificados vs Stitch

1. **Onboarding/Role Selection**: Ruta definida pero sin UI implementada
2. **Streak/gamificación**: No hay contadores de racha ni badges en Profile
3. **Widgets analíticos en entrenamiento activo**: Falta gráfico de volumen, gauge de intensidad
4. **Forgot password**: No hay endpoint ni UI para recuperación de contraseña
5. **Avatar**: Profile usa icono estático, no hay subida de imagen
6. **Agrupación por músculo en rutinas**: Lista plana, Stitch muestra agrupado
