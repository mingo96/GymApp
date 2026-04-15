# 18 — Onboarding (Selección de Perfil)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `90890afa80e344a58a3e5f60464b3244` |
| Nombre | Onboarding Profile Selection |
| Tipo | Full-screen (transactional flow — sin navbar) |
| Ancho diseño | 780 px (móvil) |
| Fondo body | `#0E0E12` (más oscuro que background estándar) |
| Navegación | Suprimida — flujo onboarding |

---

## Jerarquía Visual

```
Page  bg-[#0E0E12]  min-h-screen  flex-col  center  px-6  pt-12  pb-10
├── DecoBackground  fixed  -z-10  pointer-events-none
│   ├── GlowTopRight  -top-24 -right-24  w-64 h-64  primary/5  blur-[100px]
│   └── GlowBottomLeft  bottom-10 -left-20  w-80 h-80  secondaryContainer/5  blur-[120px]
├── DotGrid  fixed  -z-20  opacity-[0.03]
│   └── radial-gradient(white 0.5px, transparent)  size 24×24
├── Logo  mb-12  center
│   └── LogoContainer  p-4  rounded-xl  bg-surfaceContainerLow  border outlineVariant/10  shadow-2xl
│       └── Text "Kinetic Vault"  2xl  font-black  tracking-tighter  gradient-text(primary→primaryContainer)  headline  uppercase
├── Question  mb-10
│   ├── Title "¿Cómo vas a usar RutinApp?"  3xl  bold  headline  tracking-tight  onSurface
│   └── Subtitle  onSurfaceVariant/80  sm  medium
├── SelectionCards  flex-col  gap-5  mb-auto
│   ├── Card "Entreno solo"
│   │   ├── Container  bg-surfaceContainerLow  p-6  rounded-xl  border outlineVariant/10
│   │   ├── Hover  bg-surfaceContainerHigh
│   │   ├── Press  scale(0.98)
│   │   ├── IconBox  48dp  rounded-lg  bg-primaryContainer/10  primary
│   │   │   ├── Icon "fitness_center"  2xl
│   │   │   └── Hover  bg-primary  onPrimary
│   │   ├── Title  lg  bold  headline  onSurface
│   │   ├── Description  sm  onSurfaceVariant  medium  leading-relaxed
│   │   └── CheckIndicator  absolute top-4 right-4  opacity-0 → focus: opacity-100
│   │       └── Icon "check_circle" FILL=1  tertiary
│   └── Card "Soy entrenador"
│       ├── (misma estructura, pero:)
│       ├── IconBox  bg-secondaryContainer/10  secondary
│       │   ├── Icon "person"  2xl
│       │   └── Hover  bg-secondary  onSecondary
│       └── CheckIndicator  tertiary
└── Footer  mt-12  space-y-6
    ├── HintText "Puedes cambiar esto después en ajustes"  xs  onSurfaceVariant/60  medium  tracking-wide  center
    └── ContinueButton  w-full  py-4  rounded-xl
        ├── Background  gradient(primary → primaryContainer)
        ├── Text  onPrimary  bold  base  headline
        ├── Shadow  primary/10
        ├── Disabled  opacity-50  cursor-not-allowed
        └── Transition  300ms
```

---

## Componentes Detallados

### 1. Logo Container

| Propiedad | Valor |
|-----------|-------|
| Padding | `p-4` (16 dp) |
| Fondo | `surfaceContainerLow` (#1B1B20) |
| Redondeo | `rounded-xl` (12 dp) |
| Borde | `outlineVariant/10` |
| Sombra | `shadow-2xl` |

**Texto logo:**

| Propiedad | Valor |
|-----------|-------|
| Contenido | "Kinetic Vault" |
| Font | `Space Grotesk`, `font-black` (900), `2xl`, `uppercase`, `tracking-tighter` |
| Color | Gradient text: `primary #BAC3FF → primaryContainer #4361EE` (dirección to-br) |
| Técnica | `text-transparent bg-clip-text bg-gradient-to-br` |

### 2. Selection Cards

| Propiedad | Valor |
|-----------|-------|
| Fondo | `surfaceContainerLow` (#1B1B20) |
| Padding | `p-6` (24 dp) |
| Redondeo | `rounded-xl` (12 dp) |
| Borde | `outlineVariant/10` |
| Hover | `bg-surfaceContainerHigh` (#2A292F), transición 300ms |
| Press | `scale(0.98)` |

**Icon Box:**

| Propiedad | "Entreno solo" | "Soy entrenador" |
|-----------|----------------|-------------------|
| Tamaño | 48×48 dp | 48×48 dp |
| Redondeo | `rounded-lg` (8 dp) | `rounded-lg` |
| Fondo normal | `primaryContainer/10` | `secondaryContainer/10` |
| Icono normal | `primary` (#BAC3FF) | `secondary` (#D2BBFF) |
| Fondo hover | `primary` (#BAC3FF) | `secondary` (#D2BBFF) |
| Icono hover | `onPrimary` (#00218D) | `onSecondary` (#3E008E) |
| Icono | `fitness_center` 2xl | `person` 2xl |

**Check Indicator:**

| Propiedad | Valor |
|-----------|-------|
| Posición | `absolute top-4 right-4` |
| Visibilidad | `opacity-0`, focus→`opacity-100` |
| Icono | `check_circle` FILL=1, `tertiary` (#27E0A9) |

### 3. Continue Button

| Propiedad | Valor |
|-----------|-------|
| Alto | `py-4` (~56 dp) |
| Fondo | `gradient(primary → primaryContainer)` to-br |
| Texto | `onPrimary`, `bold`, `base`, `Space Grotesk` |
| Redondeo | `rounded-xl` (12 dp) |
| Sombra | `primary/10` |
| **Disabled** | `opacity-50`, `cursor-not-allowed` |
| Transición | 300ms |

### 4. Decorative Elements

| Elemento | Posición | Tamaño | Color | Blur |
|----------|----------|--------|-------|------|
| Glow 1 | `-top-24 -right-24` | 256 dp | `primary/5` | `100px` |
| Glow 2 | `bottom-10 -left-20` | 320 dp | `secondaryContainer/5` | `120px` |
| Dot grid | full | 24×24 px | `white 0.5px` | none, `opacity-3%` |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Tap card | Selecciona perfil, muestra check_circle tertiary, habilita botón "Continuar" |
| Solo 1 selección activa | Si seleccionas otro, el anterior se deselecciona |
| Botón disabled | Hasta que se seleccione un perfil |
| Tap "Continuar" | Avanza al siguiente paso del onboarding (o home) |
| Hover icon box | Transición de color: bg-container/10 → bg-solid, icon cambia a onColor |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Onboarding | No hay flujo de selección de perfil | Pantalla dedicada con 2 opciones |
| Background | Standard background | Más oscuro (#0E0E12) + glows decorativos + dot grid |
| Logo | N/A en onboarding | Container con gradient text "Kinetic Vault" |
| Cards | N/A | Bento-style con icon box que cambia en hover |
| Check indicator | N/A | check_circle tertiary aparece en seleccionado |
| Continue button | N/A | Gradient, disabled hasta selección |

---

## Plan de Implementación

1. **Crear `OnboardingProfileScreen` composable** — Scaffold sin navbar, bg `#0E0E12`, decorative Canvas con glows
2. **LogoSection** — Box con surfaceContainerLow + Text con gradient Brush (ShaderBrush)
3. **ProfileSelectionCard** — Composable con 2 variantes (trainee/coach), selected state con check overlay, animated icon box color transition
4. **ContinueButton** — Gradient button (reutilizar patrón), `enabled` ligado a selección de perfil
5. **OnboardingViewModel** — State: profileType (null/Solo/Coach), navega a home cuando se confirma
6. **Integrar en NavGraph** — Mostrar solo en primer uso (flag SharedPreferences/DataStore)
