# 20 — Copia de Seguridad (Backup)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `54c822bb9a00417cb550832195c2f678` |
| Proyecto | `4102766606033320964` |
| Ancho diseño | 780 px (móvil) |
| Tipo | Página completa (no BottomSheet) |

---

## Jerarquía Visual

```
FullScreen (bg-background #131318)
├── TopAppBar (fixed, glass: bg-#131318/70 backdrop-blur-xl, h-20)
│   ├── Logo Row
│   │   ├── Icon fitness_center (primary #BAC3FF)
│   │   └── "Kinetic Vault" (2xl bold headline, gradient from-#BAC3FF to-#4361EE, clip-text)
│   └── SettingsButton (settings icon, primary → tertiary on hover)
│
├── Main Content (pt-28 pb-32 px-6)
│   ├── HeroSection (mb-10)
│   │   ├── Title "Copia de Seguridad" (5xl bold headline on-surface)
│   │   └── AccentBar (h-1.5 w-16 rounded-full bg-#D2BBFF/secondary)
│   │
│   ├── SummaryCard (bg-surfaceContainerLow rounded-2xl p-6 border white/5, mb-8)
│   │   ├── Label "Última sincronización" (10px uppercase tracking-0.2em on-surface-variant bold)
│   │   ├── Value "Hace 2 días" (2xl bold headline primary)
│   │   ├── StatsRow (gap-8 mt-6)
│   │   │   ├── Stat "47" / "Ejercicios" (lg bold headline / 10px uppercase on-surface-variant)
│   │   │   ├── Divider (w-px h-8 outline-variant/30)
│   │   │   ├── Stat "12" / "Rutinas"
│   │   │   ├── Divider
│   │   │   └── Stat "89" / "Entrenamientos"
│   │   └── DecorativeBlur (absolute top-right, primary/5, 32dp, blur-3xl)
│   │
│   ├── ActionButtons (gap-4, mb-10)
│   │   ├── UploadButton (gradient primary→primaryContainer, rounded-2xl, p-5)
│   │   │   ├── IconBox (w-12 h-12 rounded-xl bg-white/20)
│   │   │   │   └── cloud_upload (white 2xl)
│   │   │   ├── Text "Subir Backup" (bold headline lg white) + "Envía tus datos al servidor" (sm white/80)
│   │   │   └── ChevronRight (opacity-0 → 100 on hover, translate-x animation)
│   │   │
│   │   ├── DownloadButton (bg-surfaceContainerLow border white/5, rounded-2xl, p-5)
│   │   │   ├── IconBox (w-12 h-12 rounded-xl bg-tertiary/10)
│   │   │   │   └── cloud_download (tertiary 2xl)
│   │   │   ├── Text "Descargar Backup" (bold headline lg) + "Restaura datos del servidor" (sm on-surface-variant)
│   │   │   └── ChevronRight (hover reveal)
│   │   │
│   │   └── SyncButton (bg-surfaceContainerLow border white/5, rounded-2xl, p-5)
│   │       ├── IconBox (w-12 h-12 rounded-xl bg-secondary/10)
│   │       │   └── sync (secondary 2xl)
│   │       ├── Text "Ponerse al día" (bold headline lg) + "Sincroniza cambios recientes" (sm on-surface-variant)
│   │       └── ChevronRight (hover reveal)
│   │
│   └── ResourceSelection
│       ├── Header "Seleccionar Datos" (10px extrabold uppercase tracking-0.25em on-surface-variant px-1)
│       └── ToggleList (gap-3)
│           ├── ExerciseRow (p-4 surfaceContainerLow rounded-2xl border white/5)
│           │   ├── Icon fitness_center in circle (w-10 h-10 rounded-full bg-surfaceContainer, primary xl)
│           │   ├── Label "Ejercicios" (bold) + "47 elementos" (xs on-surface-variant)
│           │   └── Toggle (w-11 h-6, checked=primaryContainer, thumb=white w-5 h-5)
│           │
│           ├── RoutineRow
│           │   ├── Icon format_list_bulleted in circle (primary)
│           │   ├── Label "Rutinas" + "12 elementos"
│           │   └── Toggle (checked)
│           │
│           └── WorkoutRow
│               ├── Icon sports_martial_arts in circle (primary)
│               ├── Label "Entrenamientos" + "89 elementos"
│               └── Toggle (checked)
│
└── BottomNavBar (5 tabs, "Perfil" active con gradient)
```

---

## Componentes Detallados

### Hero Section

| Elemento | Propiedad | Valor |
|----------|-----------|-------|
| Título | texto | "Copia de Seguridad" |
| Título | tamaño | 5xl (~48sp) |
| Título | peso | bold |
| Título | fuente | Space Grotesk (headline) |
| Título | color | on-surface (#E4E1E9) |
| Barra acento | alto × ancho | 1.5×16 (6dp × 64dp) |
| Barra acento | color | #D2BBFF (secondary) |
| Barra acento | forma | rounded-full |
| Barra acento | margen | mt-4 mb-2 |

### Summary Card

| Elemento | Propiedad | Valor |
|----------|-----------|-------|
| Contenedor | fondo | surfaceContainerLow (#1B1B20) |
| Contenedor | forma | rounded-2xl (16dp) |
| Contenedor | padding | 6 (24dp) |
| Contenedor | borde | 1px white/5 |
| Contenedor | margen inferior | mb-8 (32dp) |
| Contenedor | overflow | hidden (para blur decorativo) |
| Label | texto | "Última sincronización" |
| Label | tamaño | 10px |
| Label | estilo | uppercase, tracking-[0.2em], bold |
| Label | color | on-surface-variant (#C4C5D7) |
| Valor principal | texto | "Hace 2 días" (dinámico) |
| Valor principal | tamaño | 2xl (~24sp) |
| Valor principal | peso | bold |
| Valor principal | fuente | headline (Space Grotesk) |
| Valor principal | color | primary (#BAC3FF) |
| Stats row | margen superior | mt-6 (24dp) |
| Stats row | gap | 8 (32dp) |
| Stat número | tamaño | lg (~18sp) |
| Stat número | peso | bold |
| Stat número | fuente | headline |
| Stat label | tamaño | 10px |
| Stat label | color | on-surface-variant |
| Stat label | estilo | uppercase, tracking-wider |
| Divider | tamaño | w-px h-8 |
| Divider | color | outline-variant/30 |
| Blur decorativo | posición | absolute top-0 right-0 |
| Blur decorativo | tamaño | w-32 h-32 (128dp) |
| Blur decorativo | color | primary/5 |
| Blur decorativo | offset | -mr-16 -mt-16 |
| Blur decorativo | efecto | blur-3xl |

### Action Buttons

#### Upload (Subir Backup) — CTA Principal

| Elemento | Propiedad | Valor |
|----------|-----------|-------|
| Contenedor | fondo | gradient from-#4361EE to-#BAC3FF (primaryContainer → primary) |
| Contenedor | forma | rounded-2xl (16dp) |
| Contenedor | padding | p-5 (20dp) |
| Contenedor | texto | on-primary-container (#F4F2FF) |
| Contenedor | animación | active:scale-[0.98], transition-all 300ms |
| Contenedor | layout | flex items-center justify-between |
| Icon box | tamaño | w-12 h-12 (48dp) |
| Icon box | forma | rounded-xl (12dp) |
| Icon box | fondo | white/20 |
| Icono | nombre | cloud_upload |
| Icono | color | white |
| Icono | tamaño | 2xl (24dp) |
| Título | texto | "Subir Backup" |
| Título | estilo | bold headline lg, white |
| Subtítulo | texto | "Envía tus datos al servidor" |
| Subtítulo | estilo | sm, white/80 |
| Chevron | estado | opacity-0, translate-x-4 (oculto) |
| Chevron | hover | opacity-100, translate-x-0 (desliza desde derecha) |

#### Download (Descargar Backup)

| Elemento | Propiedad | Valor |
|----------|-----------|-------|
| Contenedor | fondo | surfaceContainerLow (#1B1B20) |
| Contenedor | borde | 1px white/5 |
| Contenedor | hover | bg-surfaceContainer (#1F1F24) |
| Icon box | fondo | tertiary/10 |
| Icono | nombre | cloud_download |
| Icono | color | tertiary (#27E0A9) |
| Título | texto | "Descargar Backup" |
| Subtítulo | texto | "Restaura datos del servidor" |
| Subtítulo | color | on-surface-variant |

#### Sync (Ponerse al día)

| Elemento | Propiedad | Valor |
|----------|-----------|-------|
| Contenedor | fondo | surfaceContainerLow (#1B1B20) |
| Contenedor | borde | 1px white/5 |
| Contenedor | hover | bg-surfaceContainer (#1F1F24) |
| Icon box | fondo | secondary/10 |
| Icono | nombre | sync |
| Icono | color | secondary (#D2BBFF) |
| Título | texto | "Ponerse al día" |
| Subtítulo | texto | "Sincroniza cambios recientes" |

### Resource Selection Toggles

| Elemento | Propiedad | Valor |
|----------|-----------|-------|
| Header label | texto | "Seleccionar Datos" |
| Header label | tamaño | 10px |
| Header label | estilo | extrabold uppercase tracking-[0.25em] |
| Header label | color | on-surface-variant |
| Row container | fondo | surfaceContainerLow |
| Row container | forma | rounded-2xl (16dp) |
| Row container | padding | p-4 (16dp) |
| Row container | borde | 1px white/5 |
| Row container | layout | flex items-center justify-between |
| List gap | valor | 3 (12dp) |
| Icon circle | tamaño | w-10 h-10 (40dp) |
| Icon circle | forma | rounded-full |
| Icon circle | fondo | surfaceContainer (#1F1F24) |
| Icon | color | primary (#BAC3FF) |
| Icon | tamaño | xl (20dp) |
| Toggle switch | tamaño | w-11 h-6 (44×24dp) |
| Toggle switch | fondo off | surfaceContainerHighest (#35343A) |
| Toggle switch | fondo on | primaryContainer (#4361EE) |
| Toggle thumb | color | white |
| Toggle thumb | tamaño | w-5 h-5 (20dp) |
| Toggle thumb | forma | rounded-full |
| Toggle thumb | transición | translate-x-full al activar |

#### Iconos por recurso

| Recurso | Icono | Cantidad ejemplo |
|---------|-------|-----------------|
| Ejercicios | fitness_center | 47 |
| Rutinas | format_list_bulleted | 12 |
| Entrenamientos | sports_martial_arts | 89 |

---

## Comportamiento e Interacciones

| Acción | Resultado |
|--------|-----------|
| Tap "Subir Backup" | Sube los recursos seleccionados al servidor. Mostrar progreso/loading |
| Tap "Descargar Backup" | Descarga y restaura datos del servidor. Confirmar antes (diálogo) |
| Tap "Ponerse al día" | Sincronización incremental de cambios recientes |
| Toggle recurso ON/OFF | Incluye/excluye ese tipo de dato de la operación de backup |
| Todos toggles OFF | Deshabilitar botones de acción (no hay datos seleccionados) |
| Upload exitoso | Actualizar "Última sincronización" a "Ahora" |
| Error de red | Mostrar snackbar/toast con error y opción de reintentar |
| Upload btn: active:scale-[0.98] | Feedback táctil sutil al pulsar |
| Download/Sync btn: hover bg change | surfaceContainerLow → surfaceContainer |
| Chevron icon hover | Aparece con slide desde derecha (opacity + translate-x) |

---

## Diferencias con Implementación Actual

| Aspecto | Diseño Stitch | Estado actual (Android) |
|---------|---------------|------------------------|
| Layout | Página completa dedicada con hero | Probable settings section simple |
| Summary card | Card glass con última sincronización + contadores | Posiblemente inexistente |
| Accent bar | Barra secondary debajo del título | No existe |
| Upload CTA | Botón gradient prominente con icon box | Botón simple |
| Download/Sync | Botones secundarios con icon boxes coloreadas | Botones genéricos o no separados |
| Resource toggles | Toggle switches individuales por tipo de dato | Probablemente todo-o-nada |
| Decorative blur | Elemento decorativo subtle en summary card | No existe |
| Icon circles | Círculos surfaceContainer con icons primary | No existe |
| Chevron animation | Reveal en hover con translate-x | No existe |

---

## Tokens de Color Específicos

| Uso | Token | Hex |
|-----|-------|-----|
| Título hero | on-surface | #E4E1E9 |
| Barra acento | secondary | #D2BBFF |
| Valor sincronización | primary | #BAC3FF |
| Upload gradient start | primaryContainer | #4361EE |
| Upload gradient end | primary | #BAC3FF |
| Upload icon box | white/20 | rgba(255,255,255,0.20) |
| Upload textos | white / white/80 | #FFFFFF / rgba(255,255,255,0.80) |
| Download icon bg | tertiary/10 | rgba(39,224,169,0.10) |
| Download icon | tertiary | #27E0A9 |
| Sync icon bg | secondary/10 | rgba(210,187,255,0.10) |
| Sync icon | secondary | #D2BBFF |
| Toggle ON | primaryContainer | #4361EE |
| Toggle OFF | surfaceContainerHighest | #35343A |
| Toggle thumb | white | #FFFFFF |
| Card bg | surfaceContainerLow | #1B1B20 |
| Card border | white/5 | rgba(255,255,255,0.05) |
| Icon circle bg | surfaceContainer | #1F1F24 |
| Resource icons | primary | #BAC3FF |
| Labels | on-surface-variant | #C4C5D7 |
| Decorative blur | primary/5 | rgba(186,195,255,0.05) |

---

## Plan de Implementación

1. **Crear `BackupPage.kt`** — Composable de página completa con `LazyColumn`
2. **Hero section** — Título 36sp (ajustar de 5xl web) + barra accent secondary
3. **SummaryCard composable** — Card glass con última sincronización y contadores dinámicos desde Room/ViewModel
4. **ActionButton composable reutilizable** — Variantes: gradient (upload), tonal-tertiary (download), tonal-secondary (sync). Parámetros: gradient/bg, iconTint, iconBg, icon, title, subtitle
5. **ResourceToggleRow composable** — Row con circle icon + label + count + Switch (Material3 con primaryContainer colors)
6. **BackupViewModel** — Estado: lastSync, resourceCounts, selectedResources, isLoading. Acciones: upload, download, sync
7. **Integrar con navegación** — Accesible desde ProfilePage (settings) o BottomNav si se añade tab "Sync"
8. **Animaciones** — Scale on press (0.98f), switch toggle transition
9. **Manejo de errores** — Snackbar para errores de red, diálogo confirmación para restaurar
