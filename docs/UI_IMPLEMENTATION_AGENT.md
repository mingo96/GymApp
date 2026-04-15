# Guía de Implementación UI — Agente IA

> **Propósito**: Documento autónomo para que un agente de IA implemente paso a paso el rediseño Kinetic Precision (KP) en la app Android RutinApp, usando las guías de Stitch como referencia.

---

## 1. Contexto del Proyecto

| Campo | Valor |
|-------|-------|
| Plataforma | Android nativo |
| Lenguaje | Kotlin |
| UI Framework | Jetpack Compose + Material3 |
| DI | Hilt |
| Arquitectura | MVVM (ViewModels → Use Cases → Repositories → Room/API) |
| Navegación | Sheet stacking (Trade Republic style) via `SheetNavigator` |
| Ruta raíz proyecto | `c:\Users\eloy\Desktop\clase\DIN\RutinApp` |
| Package base | `com.mintocode.rutinapp` |
| Build | `.\gradlew.bat assembleDebug` |
| Tests | `.\gradlew.bat testDebugUnitTest` |

---

## 2. Estado Actual

**Todas las pantallas YA EXISTEN como composables funcionales**. La tarea NO es crear pantallas nuevas — es **rediseñar visualmente** las existentes para coincidir con los diseños de Stitch/KP.

El tema KP (colores, tipografía, shapes) ya está implementado en `ui/theme/`. Los ViewModels, la navegación y la lógica de negocio están completos. Solo se modifica la capa de presentación (Composables).

---

## 3. Arquitectura UI

### Estructura de Archivos

```
app/src/main/java/com/mintocode/rutinapp/
├── ui/
│   ├── theme/                          ← Design system (NO TOCAR excepto si falta token)
│   │   ├── Color.kt                    ← RutinAppColors.Dark / .Light
│   │   ├── Theme.kt                    ← RutinAppTheme(), LocalRutinAppColors
│   │   ├── Type.kt                     ← SpaceGroteskFont + ManropeFont
│   │   ├── Shape.kt                    ← RutinAppShapes (4/8/12/16/28 dp)
│   │   ├── Font.kt                     ← Font families
│   │   ├── ButtonColors.kt             ← rutinAppButtonsColours()
│   │   ├── CardColors.kt               ← rutinAppCardColors()
│   │   ├── TextFieldColors.kt          ← rutinAppTextFieldColors()
│   │   └── DatePickerColors.kt         ← rutinAppDatePickerColors()
│   │
│   ├── navigation/
│   │   ├── SheetDestination.kt         ← sealed class con 20 destinos
│   │   ├── SheetNavigator.kt           ← Stack-based, LocalSheetNavigator
│   │   ├── SheetHost.kt                ← when(destination) → Composable
│   │   └── NavigationRoutes.kt         ← Rutas de raíz (HOME, TRAIN, PROFILE...)
│   │
│   ├── screens/
│   │   ├── root/                       ← Páginas raíz del HorizontalPager
│   │   │   ├── RootPager.kt            ← Pager 3 páginas + tabs
│   │   │   ├── HomePage.kt             ← Inicio
│   │   │   ├── TrainPage.kt            ← Entrenar
│   │   │   └── ProfilePage.kt          ← Perfil
│   │   │
│   │   ├── sheets/                     ← Bottom sheets (19 archivos)
│   │   │   ├── ActiveWorkoutSheet.kt
│   │   │   ├── ExerciseListSheet.kt
│   │   │   ├── ExerciseDetailSheet.kt
│   │   │   ├── ExerciseCreateSheet.kt
│   │   │   ├── ExerciseEditSheet.kt
│   │   │   ├── RoutineListSheet.kt
│   │   │   ├── RoutineDetailSheet.kt
│   │   │   ├── RoutineCreateSheet.kt
│   │   │   ├── RoutineEditSheet.kt
│   │   │   ├── WorkoutHistorySheet.kt
│   │   │   ├── PlanningEditSheet.kt
│   │   │   ├── StatsSheet.kt
│   │   │   ├── ExerciseStatsSheet.kt
│   │   │   ├── SettingsSheet.kt
│   │   │   ├── AppConfigSheet.kt
│   │   │   ├── AuthSheet.kt
│   │   │   ├── NotificationsSheet.kt
│   │   │   ├── TrainerManagementSheet.kt
│   │   │   └── BackupSheet.kt
│   │   │
│   │   ├── WorkoutProgressionSection.kt  ← DigitalWatch, WorkoutProgression
│   │   ├── WorkoutDialogs.kt             ← SetEditionSheet, SetOptionsSheet
│   │   ├── WorkoutComponents.kt          ← WorkoutItem, RoutineItem
│   │   ├── ExerciseDialogs.kt
│   │   └── RoutineDialogs.kt
│   │
│   ├── components/                     ← Componentes reutilizables
│   │   ├── SharedComponents.kt         ← SearchTextField, inputs
│   │   ├── RutinAppTopBar.kt
│   │   ├── RutinAppBottomBar.kt
│   │   └── FABComposable.kt
│   │
│   └── premade/                        ← Componentes avanzados
│       ├── RutinAppCalendar.kt
│       ├── RutinAppLineChart.kt
│       ├── RutinAppPieChart.kt
│       ├── AnimatedItem.kt
│       └── AdjustableText.kt
│
└── viewmodels/                         ← NO TOCAR (ya completos)
```

### Navegación

```
MainActivity
└── RutinAppTheme
    └── SheetHost (ModalBottomSheet stack)
        └── RootPager (HorizontalPager)
            ├── [0] HomePage
            ├── [1] TrainPage
            └── [2] ProfilePage
```

Abrir sheet: `navigator.open(SheetDestination.ExerciseList)`
Cerrar: `navigator.close()` o swipe-down
Acceso: `val navigator = LocalSheetNavigator.current`

---

## 4. Design System KP — Referencia Rápida

### Colores (dark, los únicos que importan para la app)

```kotlin
// Acceso en Composables:
MaterialTheme.colorScheme.primary          // #BAC3FF
MaterialTheme.colorScheme.primaryContainer // #4361EE
MaterialTheme.colorScheme.onPrimaryContainer // #F4F2FF
MaterialTheme.colorScheme.secondary        // #D2BBFF
MaterialTheme.colorScheme.secondaryContainer // #6800E4
MaterialTheme.colorScheme.tertiary         // #27E0A9
MaterialTheme.colorScheme.tertiaryContainer // #007F5D
MaterialTheme.colorScheme.surface          // #131318
MaterialTheme.colorScheme.surfaceContainer // #1F1F24 (via RutinAppColors.Dark)
MaterialTheme.colorScheme.onSurface        // #E4E1E9
MaterialTheme.colorScheme.onSurfaceVariant // #C4C5D7
MaterialTheme.colorScheme.outline          // #8E8FA1
MaterialTheme.colorScheme.outlineVariant   // #444655
MaterialTheme.colorScheme.error            // #FFB4AB
MaterialTheme.colorScheme.errorContainer   // #93000A

// Acceso directo para tokens no-Material3:
RutinAppColors.Dark.surfaceContainerLow    // #1B1B20
RutinAppColors.Dark.surfaceContainerHigh   // #2A292F
RutinAppColors.Dark.surfaceContainerHighest // #35343A
RutinAppColors.Dark.surfaceContainerLowest // #0E0E13
RutinAppColors.Dark.surfaceBright          // #39393E
```

### Tipografía

```kotlin
// Headlines / Títulos:
fontFamily = SpaceGroteskFont   // Space Grotesk
fontWeight = FontWeight.Bold

// Body / Labels:
fontFamily = ManropeFont        // Manrope (default de MaterialTheme.typography)

// Tamaños frecuentes en diseños Stitch:
// 5xl ≈ 48.sp (títulos hero)
// 4xl ≈ 36.sp (valores grandes)
// 3xl ≈ 30.sp (subtítulos grandes)
// 2xl ≈ 24.sp (headings)
// xl  ≈ 20.sp
// lg  ≈ 18.sp
// base ≈ 16.sp (body)
// sm  ≈ 14.sp
// xs  ≈ 12.sp
// 10px ≈ 10.sp (labels uppercase)
```

### Shapes

```kotlin
MaterialTheme.shapes.extraSmall  // 4.dp
MaterialTheme.shapes.small       // 8.dp
MaterialTheme.shapes.medium      // 12.dp
MaterialTheme.shapes.large       // 16.dp
MaterialTheme.shapes.extraLarge  // 28.dp
RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)  // BottomSheet top
```

### Patrones Visuales Recurrentes

| Patrón | Implementación |
|--------|---------------|
| Glass header | `Modifier.background(Color(0xFF131318).copy(alpha = 0.7f))` + no hay blur nativo, usar `Surface` con alpha |
| Gradient button | `Brush.linearGradient(listOf(primaryContainer, primary))` con `Box(modifier = Modifier.background(brush))` |
| Glow effect | `Modifier.shadow(elevation, shape, ambientColor = color.copy(alpha = 0.3f))` |
| Card KP | `Surface(color = RutinAppColors.Dark.surfaceContainerLow, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)))` |
| Accent bar | `Box(Modifier.height(6.dp).width(64.dp).background(secondary, RoundedCornerShape(50)))` |
| Icon circle | `Box(Modifier.size(40.dp).background(surfaceContainer, CircleShape))` con `Icon(tint = primary)` |
| Section label | `Text(uppercase, 10.sp, FontWeight.ExtraBold, letterSpacing = 2.sp, color = onSurfaceVariant)` |
| Toggle switch | `Switch(colors = SwitchDefaults.colors(checkedTrackColor = primaryContainer, checkedThumbColor = Color.White))` |
| Scale on press | `Modifier.clickable(interactionSource, indication) { }` + `animateFloatAsState` en `graphicsLayer(scaleX, scaleY)` para 0.98f |

---

## 5. Guías de Diseño por Pantalla

Cada guía está en `docs/stitch-guides/XX-nombre.md` y contiene:
- **Jerarquía visual** completa (árbol de componentes con tokens exactos)
- **Tabla de componentes** (color, tamaño, fuente, spacing de cada elemento)
- **Comportamiento e interacciones**
- **Diferencias con implementación actual** (lo que hay que cambiar)
- **Plan de implementación** (pasos sugeridos)

### Mapeo Guía → Archivo a Modificar

| # | Guía | Archivo(s) a modificar |
|---|------|----------------------|
| 01 | [01-inicio-home.md](stitch-guides/01-inicio-home.md) | `ui/screens/root/HomePage.kt` |
| 02 | [02-entrenar-train.md](stitch-guides/02-entrenar-train.md) | `ui/screens/root/TrainPage.kt` |
| 03 | [03-perfil-profile.md](stitch-guides/03-perfil-profile.md) | `ui/screens/root/ProfilePage.kt` |
| 04 | [04-entrenamiento-activo.md](stitch-guides/04-entrenamiento-activo.md) | `ui/screens/sheets/ActiveWorkoutSheet.kt`, `ui/screens/WorkoutProgressionSection.kt` |
| 05 | [05-lista-ejercicios.md](stitch-guides/05-lista-ejercicios.md) | `ui/screens/sheets/ExerciseListSheet.kt` |
| 06 | [06-observar-ejercicio.md](stitch-guides/06-observar-ejercicio.md) | `ui/screens/sheets/ExerciseDetailSheet.kt` |
| 07 | [07-crear-ejercicio.md](stitch-guides/07-crear-ejercicio.md) | `ui/screens/sheets/ExerciseCreateSheet.kt` |
| 08 | [08-editar-serie.md](stitch-guides/08-editar-serie.md) | `ui/screens/WorkoutDialogs.kt` (SetEditionSheet) |
| 09 | [09-lista-rutinas.md](stitch-guides/09-lista-rutinas.md) | `ui/screens/sheets/RoutineListSheet.kt` |
| 10 | [10-crear-rutina.md](stitch-guides/10-crear-rutina.md) | `ui/screens/sheets/RoutineCreateSheet.kt`, `RoutineEditSheet.kt` |
| 11 | [11-estadisticas.md](stitch-guides/11-estadisticas.md) | `ui/screens/sheets/StatsSheet.kt` |
| 12 | [12-planificacion.md](stitch-guides/12-planificacion.md) | `ui/screens/sheets/PlanningEditSheet.kt` |
| 13 | [13-notificaciones.md](stitch-guides/13-notificaciones.md) | `ui/screens/sheets/NotificationsSheet.kt` |
| 14 | [14-historial-entrenamientos.md](stitch-guides/14-historial-entrenamientos.md) | `ui/screens/sheets/WorkoutHistorySheet.kt` |
| 15 | [15-estadisticas-ejercicio.md](stitch-guides/15-estadisticas-ejercicio.md) | `ui/screens/sheets/ExerciseStatsSheet.kt` |
| 16 | [16-overlays-entrenamiento.md](stitch-guides/16-overlays-entrenamiento.md) | `ui/floating/` (FloatingWorkoutService) |
| 17 | [17-autenticacion.md](stitch-guides/17-autenticacion.md) | `ui/screens/sheets/AuthSheet.kt` |
| 18 | [18-onboarding.md](stitch-guides/18-onboarding.md) | Crear nuevo: `ui/screens/OnboardingScreen.kt` |
| 19 | [19-entrenadores.md](stitch-guides/19-entrenadores.md) | `ui/screens/sheets/TrainerManagementSheet.kt` |
| 20 | [20-backup.md](stitch-guides/20-backup.md) | `ui/screens/sheets/BackupSheet.kt` |

---

## 6. Orden de Implementación

### Fase 1 — Raíz (impacto visual máximo)

```
1.1  HomePage.kt            ← Guía 01
1.2  TrainPage.kt            ← Guía 02
1.3  ProfilePage.kt          ← Guía 03
1.4  RootPager.kt            ← Tab bar y header glass
```

**Razón**: Son las pantallas que el usuario ve primero. Cualquier cambio aquí tiene el mayor impacto visual.

### Fase 2 — Flujo de Entrenamiento (funcionalidad core)

```
2.1  ActiveWorkoutSheet.kt   ← Guía 04 (timer, exercise carousel, set table)
2.2  WorkoutProgressionSection.kt ← Guía 04 (DigitalWatch, WorkoutProgression)
2.3  WorkoutDialogs.kt       ← Guía 08 (SetEditionSheet: weight 7xl, reps 4xl)
2.4  WorkoutHistorySheet.kt  ← Guía 14
```

**Razón**: El entrenamiento activo es la funcionalidad principal de la app.

### Fase 3 — Gestión de Ejercicios y Rutinas

```
3.1  ExerciseListSheet.kt    ← Guía 05
3.2  ExerciseDetailSheet.kt  ← Guía 06
3.3  ExerciseCreateSheet.kt  ← Guía 07
3.4  RoutineListSheet.kt     ← Guía 09
3.5  RoutineCreateSheet.kt   ← Guía 10
3.6  RoutineEditSheet.kt     ← Guía 10
```

### Fase 4 — Estadísticas y Planificación

```
4.1  StatsSheet.kt           ← Guía 11 (KPI cards, charts, personal records)
4.2  ExerciseStatsSheet.kt   ← Guía 15 (PR card, progress chart, rep distribution)
4.3  PlanningEditSheet.kt    ← Guía 12
```

### Fase 5 — Secundarias

```
5.1  NotificationsSheet.kt       ← Guía 13
5.2  AuthSheet.kt                ← Guía 17
5.3  TrainerManagementSheet.kt   ← Guía 19
5.4  BackupSheet.kt              ← Guía 20
5.5  OnboardingScreen.kt (nuevo) ← Guía 18
5.6  FloatingWorkoutService      ← Guía 16 (overlays)
```

---

## 7. Workflow por Pantalla

Para **cada** pantalla, seguir este proceso exacto:

### Paso 1: Leer la guía
```
read_file("docs/stitch-guides/XX-nombre.md")
```
Entender la jerarquía visual completa, tokens, y el plan de implementación sugerido.

### Paso 2: Leer el archivo actual
```
read_file("app/src/main/java/.../ArchivoActual.kt")
```
Entender la estructura actual: qué ViewModel usa, qué datos consume, qué acciones ejecuta.

### Paso 3: Identificar diferencias
Comparar la sección "Diferencias con Implementación Actual" de la guía contra el código real. Listar los cambios concretos necesarios.

### Paso 4: Implementar cambios
Modificar el composable para coincidir con el diseño de Stitch. Reglas:
- **NO cambiar lógica de negocio** — solo presentación
- **NO cambiar firmas de funciones** ni parámetros de ViewModels
- **NO crear archivos nuevos** a menos que la guía lo pida explícitamente
- **Reutilizar tokens del tema** (`MaterialTheme.colorScheme.xxx`, `RutinAppColors.Dark.xxx`)
- **Reutilizar componentes existentes** (`rutinAppButtonsColours()`, `rutinAppCardColors()`, etc.)
- **Mantener funcionalidad** — si algo funciona, no romperlo
- Si un composable nuevo es necesario y se usará en 2+ pantallas, ponerlo en `ui/components/`
- Si es específico de una pantalla, dejarlo como función privada en el mismo archivo

### Paso 5: Compilar
```bash
.\gradlew.bat assembleDebug
```
Corregir errores hasta que compile.

### Paso 6: Verificar visualmente
Ejecutar en emulador o dispositivo. Comparar con el diseño de Stitch screen (usar `mcp_stitch_fetch_screen_image` con el Screen ID del metadato de la guía).

### Paso 7: Confirmar test
```bash
.\gradlew.bat testDebugUnitTest
```
No debe haber regresiones.

---

## 8. Reglas de Implementación

### Cosas que SÍ hacer

1. **Usar `MaterialTheme.colorScheme`** para todos los colores estándar Material3
2. **Usar `RutinAppColors.Dark`** para surface containers y tokens extendidos
3. **Usar `SpaceGroteskFont`** para títulos/headlines, `ManropeFont` para body/labels (ya configurado en MaterialTheme.typography, solo especificar fontFamily cuando se necesite sobreescribir)
4. **Usar `RoundedCornerShape(Xdp)`** (no `CircleShape` para rectángulos redondeados)
5. **Mantener paddings consistentes**: px-6 = 24.dp, px-4 = 16.dp, content padding vertical 16-24.dp
6. **Gradientes con `Brush.linearGradient`** para botones CTA y decoraciones
7. **`BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))`** para bordes sutiles de cards
8. **`Modifier.clip()` ANTES de `.background()`** para que el fondo respete la forma
9. **Animar interacciones**: `animateFloatAsState` para escala, `animateColorAsState` para transiciones de color

### Cosas que NO hacer

1. **NO crear nuevos ViewModels** — los 10 existentes cubren todo
2. **NO modificar `SheetDestination.kt`** — las 20 rutas ya existen
3. **NO añadir dependencias** nuevas al `build.gradle.kts`
4. **NO usar `blur()`** en Compose — no tiene soporte nativo robusto. Simular glass effects con alpha y colores elevados
5. **NO hardcodear colores** — siempre usar tokens del tema
6. **NO cambiar la arquitectura de navegación** — funciona correctamente
7. **NO tocar `ui/theme/`** salvo para agregar un token nuevo que falte explícitamente
8. **NO romper funcionalidad existente** — solo cambios visuales

### Equivalencias Web CSS → Compose

| CSS (Tailwind en guías) | Compose |
|-------------------------|---------|
| `rounded-2xl` | `RoundedCornerShape(16.dp)` |
| `rounded-xl` | `RoundedCornerShape(12.dp)` |
| `rounded-full` | `CircleShape` o `RoundedCornerShape(50)` |
| `p-6` / `px-6` | `.padding(24.dp)` / `.padding(horizontal = 24.dp)` |
| `gap-4` | `Arrangement.spacedBy(16.dp)` |
| `backdrop-blur-xl` | No nativo — usar `Surface(color = X.copy(alpha = 0.7f))` |
| `bg-gradient-to-br from-X to-Y` | `Brush.linearGradient(listOf(X, Y), start = Offset(0,0), end = Offset(Float.MAX, Float.MAX))` |
| `text-5xl` | `fontSize = 48.sp` |
| `text-2xl` | `fontSize = 24.sp` |
| `tracking-[0.2em]` | `letterSpacing = 3.2.sp` (0.2 × 16) |
| `tracking-wider` | `letterSpacing = 1.sp` |
| `font-extrabold` / `font-black` | `FontWeight.ExtraBold` / `FontWeight.Black` |
| `opacity-40` | `.alpha(0.4f)` |
| `border-l-4` | `Modifier.drawBehind { drawLine(...) }` o `Box` lateral |
| `shadow-lg shadow-primary/20` | `.shadow(8.dp, shape, ambientColor = primary.copy(0.2f))` |
| `active:scale-[0.98]` | `graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)` |
| `transition-all 300ms` | `animateFloatAsState(animationSpec = tween(300))` |
| `overflow-hidden` | `Modifier.clip(shape)` |

---

## 9. Componentes Reutilizables a Crear (si no existen)

Durante la implementación, es probable que se necesiten estos componentes compartidos. Crearlos en `ui/components/` solo cuando se usen en 2+ pantallas:

### `GlassCard`
```kotlin
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = RutinAppColors.Dark.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        content = { Column(modifier = Modifier.padding(24.dp), content = content) }
    )
}
```

### `SectionLabel`
```kotlin
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = ManropeFont,
        letterSpacing = 2.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
```

### `GradientButton`
```kotlin
@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f)

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
```

### `AccentBar`
```kotlin
@Composable
fun AccentBar(
    color: Color = MaterialTheme.colorScheme.secondary,
    width: Dp = 64.dp
) {
    Box(
        modifier = Modifier
            .height(6.dp)
            .width(width)
            .background(color, RoundedCornerShape(50))
    )
}
```

### `IconCircle`
```kotlin
@Composable
fun IconCircle(
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    size: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}
```

---

## 10. Checklist Global

Antes de considerar una pantalla como completada:

- [ ] Jerarquía visual coincide con la guía de Stitch
- [ ] Colores usan tokens del tema (no hardcodeados)
- [ ] Tipografía correcta (Space Grotesk para títulos, Manrope para body)
- [ ] Spacing consistente con la guía (paddings, gaps, margins)
- [ ] Shapes correctos (rounded corners según especificación)
- [ ] Interacciones implementadas (press feedback, transiciones)
- [ ] Funcionalidad original preservada (navegación, acciones, datos)
- [ ] Compila sin errores (`assembleDebug`)
- [ ] Tests pasan (`testDebugUnitTest`)
- [ ] No hay regresiones visuales en otras pantallas

---

## 11. Resolución de Problemas

### "No encuentro un color del diseño"
1. Buscar en `RutinAppColors.Dark` — tiene TODOS los tokens KP
2. Si no existe, es un derivado: `color.copy(alpha = 0.1f)` para tints
3. Solo agregar a `Color.kt` si es un color completamente nuevo del diseño

### "El diseño pide blur/glass"
No hay blur nativo en Compose. Alternativas:
- `color.copy(alpha = 0.7f)` simula glass
- `Surface(tonalElevation = 1.dp)` da elevación tonal
- Gradient overlay sutil para profundidad

### "El diseño tiene una animación compleja"
Priorizar por impacto:
1. **Siempre**: scale on press (0.98f), color transitions
2. **Cuando es fácil**: slide-in items (`AnimatedItem` ya existe)
3. **Diferir**: animaciones de partículas, neon pulse, glow animado

### "Un composable es muy largo"
Extraer sub-composables como `private fun` en el mismo archivo. Solo mover a `ui/components/` si se reutiliza.

### "¿Debo crear un nuevo ViewModel?"
**NO**. Los ViewModels existentes cubren todos los flujos. Si falta un dato, añadirlo al ViewModel existente como un nuevo StateFlow/LiveData.

---

## 12. Referencia de Screen IDs de Stitch

Para consultar visualmente un diseño, usar:
```
mcp_stitch_fetch_screen_image(projectId = "4102766606033320964", screenId = "ID")
```

| Pantalla | Screen ID |
|----------|-----------|
| Inicio (Home) | `04d9db0fe2fa4539b1a1c4f23ddeb989` |
| Entrenar (Train) | `dea9d00bdc314023873b87ccfd43cd0f` |
| Perfil (Profile) | `296b1363a9c745eab9265804f0631056` |
| Entrenamiento Activo | `073e52e9d06041949da03cff2abf3d8c` |
| Lista Ejercicios | `225eb84af91f4b0bb632b930ec560153` |
| Observar Ejercicio | `831adef059c141bda5522c7b34df3029` |
| Crear Ejercicio | `55a8f438304a4aadaa9c278c309f82d6` |
| Editar Serie | `0a092901c30543e8ba818e78a415a395` |
| Lista Rutinas | `5d63f061d2bb46ce8c97570a5e0ff2dc` |
| Crear Rutina | `962ea00e35d040c6a4072a6afc6caab9` |
| Estadísticas | `717b11abf3a84354bfc77798638e321d` |
| Planificación | `682afa729e1545e59eedf067c819ea93` |
| Notificaciones | `820cc7332e9c4b349185dec6234cc6c2` |
| Historial Entrenamientos | `8ea15c2f5e9a471aac597c4f03dd1061` |
| Stats Ejercicio | `c56db189a06c4f448e86ae95403977ea` |
| Overlay Bubble | `d77d56ae78c44d0995e124241b0d29c0` |
| Overlay Panel | `3247c506c4a14d01aa75683dff6bedb5` |
| Autenticación | `e7c913687d5e41f7a7f910b07c3f802b` |
| Onboarding | `90890afa80e344a58a3e5f60464b3244` |
| Entrenadores | `544d9931111b4010b677a4e49e97277c` |
| Backup | `54c822bb9a00417cb550832195c2f678` |

---

## 13. Criterio de Completitud

La implementación se considera completa cuando:

1. Las 20 pantallas coinciden visualmente con sus guías de Stitch
2. `.\gradlew.bat assembleDebug` compila sin errores
3. `.\gradlew.bat testDebugUnitTest` pasa al 100%
4. No hay colores hardcodeados fuera de `ui/theme/`
5. No se ha roto ninguna funcionalidad (navegación, datos, acciones)
6. Los componentes reutilizables están en `ui/components/`, no duplicados
