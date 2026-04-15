# Guías de Implementación UI — Stitch Screens

Este directorio contiene documentación detallada de cada pantalla diseñada en Stitch (Google) para la app RutinApp. Cada documento sirve como **guía de implementación** para trasladar los diseños a Jetpack Compose.

## Design System: KP (Kinetic Precision)

- **Fuentes**: Space Grotesk (headlines/display), Manrope (body/label)
- **Esquina**: extraSmall=4dp, small=8dp, medium=12dp, large=16dp, extraLarge=28dp
- **Modo**: Dark-only

### Colores Principales

| Token | Valor |
|-------|-------|
| primary | #BAC3FF |
| primaryContainer | #4361EE |
| onPrimaryContainer | #F4F2FF |
| secondary | #D2BBFF |
| secondaryContainer | #6800E4 |
| tertiary | #27E0A9 |
| tertiaryContainer | #007F5D |
| surface | #131318 |
| surfaceContainer | #1F1F24 |
| surfaceContainerLow | #1B1B20 |
| surfaceContainerHigh | #2A292F |
| surfaceContainerHighest | #35343A |
| onSurface | #E4E1E9 |
| onSurfaceVariant | #C4C5D7 |
| outline | #8E8FA1 |
| outlineVariant | #444655 |
| error | #FFB4AB |
| surfaceBright | #39393E |
| surfaceContainerLowest | #0E0E13 |

## Índice de Pantallas

### Pantallas Raíz (Root Pager)
- [01 — Inicio (Home)](./01-inicio-home.md)
- [02 — Entrenar (Train)](./02-entrenar-train.md)
- [03 — Perfil (Profile)](./03-perfil-profile.md)

### Entrenamiento Activo y Ejercicios
- [04 — Entrenamiento Activo](./04-entrenamiento-activo.md)
- [05 — Lista de Ejercicios](./05-lista-ejercicios.md)
- [06 — Observar Ejercicio](./06-observar-ejercicio.md)
- [07 — Crear Ejercicio](./07-crear-ejercicio.md)
- [08 — Editar Serie](./08-editar-serie.md)

### Rutinas
- [09 — Lista de Rutinas](./09-lista-rutinas.md)
- [10 — Crear/Editar Rutina](./10-crear-rutina.md)

### Estadísticas e Historial
- [11 — Estadísticas Generales](./11-estadisticas.md)
- [14 — Historial de Entrenamientos](./14-historial-entrenamientos.md)
- [15 — Estadísticas de Ejercicio](./15-estadisticas-ejercicio.md)

### Planificación y Notificaciones
- [12 — Planificación](./12-planificacion.md)
- [13 — Notificaciones](./13-notificaciones.md)

### Overlays de Entrenamiento
- [16 — Overlays: Bubble + Panel](./16-overlays-entrenamiento.md)

### Autenticación y Onboarding
- [17 — Autenticación](./17-autenticacion.md)
- [18 — Onboarding](./18-onboarding.md)

### Gestión y Backup
- [19 — Entrenadores](./19-entrenadores.md)
- [20 — Copia de Seguridad](./20-backup.md)

## Convención de Documentación

Cada documento incluye:
1. **Metadatos** — ID de Stitch, dimensiones, variantes
2. **Jerarquía visual** — Layout tree con componentes
3. **Tokens de diseño** — Colores, tipografía, espaciado, bordes
4. **Comportamiento** — Interacciones, animaciones, estados
5. **Mapeo a código actual** — Qué archivos existen y qué falta
6. **Plan de implementación** — Pasos concretos para implementar
