# 03 — Perfil (Profile)

## Metadatos Stitch
- **Screen ID**: `296b1363a9c745eab9265804f0631056`
- **Título**: Perfil (Profile) - Mobile
- **Dimensiones**: 780 × ~1600 px
- **Dispositivo**: MOBILE

---

## Jerarquía Visual

```
Screen (bg-surface #131318)
├── TopAppBar (fixed, glass, h-20)
│   ├── Nav: INICIO | ENTRENAR | PERFIL (activo, tertiary + underline)
│   └── Avatar small (32dp, rounded-full, border primary/20)
│
├── Main Content (pt-28, pb-20, px-6)
│   ├── Title Section
│   │   ├── H2: "Perfil" (5xl headline, bold)
│   │   └── Accent bar (h-1, w-12, tertiary, rounded-full)
│   │
│   ├── User Card (surfaceContainerLow)
│   │   ├── Blob decorativo (primary/10, blur-3xl)
│   │   ├── Avatar grande (96dp, rounded-2xl, rotate-3, shadow)
│   │   │   └── Verified badge (tertiary, filled checkmark)
│   │   ├── Nombre + email
│   │   ├── Tags: "Elite Member" (primary) + "32 Day Streak" (tertiary)
│   │   └── Botón "Edit Profile" (gradient primary→primaryContainer)
│   │
│   ├── Settings - Personalización
│   │   ├── Section label
│   │   └── Theme toggle row (dark_mode + switch)
│   │
│   ├── Settings - Cuenta y Seguridad
│   │   ├── Section label
│   │   ├── Ajustes de cuenta (manage_accounts → chevron)
│   │   ├── Inicio de sesión (lock → chevron)
│   │   ├── Notificaciones (notifications → badge "2" + chevron)
│   │   ├── Entrenadores (group → chevron)
│   │   └── Estadísticas (insights → chevron)
│   │
│   └── Logout Section
│       ├── "Cerrar sesión" (error, underline hover)
│       └── Version: "KINETIC VAULT v2.4.0 • RutinApp 2024"
```

---

## Componentes Detallados

### 1. TopAppBar (Shared — igual que Home y Train)

```
Posición: fixed top-0, full width, z-50
Fondo: #131318/80 + backdrop-blur-xl
Borde inferior: white/5
Altura: 80dp (h-20)
Layout: Row, space-between, items-center, px-6
```

**Nav tabs (Row, flex-1, justify-between, ml-8):**

| Tab | Estado | Color | Extra |
|-----|--------|-------|-------|
| INICIO | Inactivo | outline (#8E8FA1) | — |
| ENTRENAR | Inactivo | outline (#8E8FA1) | — |
| PERFIL | **Activo** | **tertiary (#27E0A9)** | Underline: h-0.5, w-4, tertiary |

- Fuente tabs: Space Grotesk, 12sp (text-xs), bold, tracking-[0.2em], uppercase
- Hover: text-onSurface, transition

**Avatar small (right, ml-6):**
- 32×32dp, rounded-full
- Borde: 1px primary/20
- Imagen de perfil

> **Nota:** En Home/Train la tab activa usa primary (#BAC3FF), pero en Profile usa tertiary (#27E0A9). Esto es intencional para diferenciar las secciones.

---

### 2. Title Section

```
Margin bottom: 40dp (mb-10)
```

- **Título:** "Perfil"
  - Fuente: Space Grotesk, 48sp (text-5xl), bold, tracking-tight
  - Color: onSurface (#E4E1E9)
- **Accent bar:**
  - 4dp alto (h-1), 48dp ancho (w-12)
  - Color: tertiary (#27E0A9)
  - Forma: rounded-full
  - Margin top: 8dp

---

### 3. User Card

```
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl (12dp)
Padding: 32dp (p-8)
Layout: Column (mobile), Row (md+), items-center, gap-6
Overflow: hidden
Position: relative
Margin bottom: 48dp (mb-12)
```

**Blob decorativo:**
- Position: absolute, top-0, right-0
- 128×128dp (w-32 h-32)
- Color: primary/10
- rounded-full, blur-3xl
- Offset: -mr-16, -mt-16

**Avatar grande:**
```
Container: relative
Imagen: 96×96dp (w-24 h-24)
Forma: rounded-2xl (16dp)
Rotación: rotate-3 (≈3°)
Sombra: shadow-2xl
Overflow: hidden
```

**Verified badge (absolute):**
- Position: -bottom-2, -right-2
- 32×32dp (w-8 h-8)
- Fondo: tertiary (#27E0A9)
- Color icono: onTertiary (#003827)
- Icono: verified (FILL 1), text-sm
- Forma: rounded-full
- Sombra: shadow-lg

**Info (flex-1, text-center en mobile, text-left en md+):**
- Nombre: "Juan Perez" — Space Grotesk, 24sp (text-2xl), bold, onSurface
- Email: "juan.perez@rutinapp.com" — Manrope, medium, onSurfaceVariant

**Tags (mt-4, flex-wrap, gap-2):**

| Tag | Fondo | Color | Fuente |
|-----|-------|-------|--------|
| Elite Member | surfaceContainerHighest (#35343A) | primary (#BAC3FF) | 12sp (text-xs), bold, tracking-widest, uppercase |
| 32 Day Streak | tertiary/10 | tertiary (#27E0A9) | 12sp, bold, tracking-widest, uppercase |

- Padding: px-3 py-1, rounded-full

**Botón "Edit Profile":**
```
Fondo: gradient to-br from-primary to-primaryContainer
Color texto: onPrimary (#00218D)
Fuente: bold
Padding: px-6 py-3
Forma: rounded-xl (12dp)
Sombra: shadow-lg
Hover: opacity-90
Active: scale-95
Transición: all
```

---

### 4. Settings — Personalización

**Section label:**
```
Padding: px-2
Margin bottom: 16dp (mb-4)
Fuente: Space Grotesk, 12sp (text-xs), bold, tracking-[0.2em], uppercase
Color: outline (#8E8FA1), opacity-60
```

**Theme Toggle Row:**
```
Fondo: surfaceContainerLow (#1B1B20)
Hover: surfaceContainerHigh (#2A292F)
Padding: 20dp (p-5)
Forma: rounded-xl (12dp)
Layout: Row, items-center, space-between
Margin bottom: 24dp (mb-6)
Transición: colors
```

- Left: Row, gap-4
  - Icono container: 40×40dp, rounded-lg (8dp), surfaceContainerHighest bg, primary color
  - Icono: dark_mode
  - Label: "Tema oscuro" — Manrope, medium, 18sp (text-lg)
- Right: Toggle switch
  - Track: 48×24dp (w-12 h-6), primary bg, rounded-full
  - Thumb: 16×16dp (w-4 h-4), onPrimary, rounded-full, ml-auto, shadow-md

---

### 5. Settings — Cuenta y Seguridad

**Section label:** Igual que Personalización, con pt-4

**List items (space-y-1):**

Cada row comparte esta estructura:
```
Fondo: surfaceContainerLow (#1B1B20)
Hover: surfaceContainerHigh (#2A292F)
Padding: 20dp (p-5)
Forma: rounded-xl (12dp)
Layout: Row, items-center, space-between
Cursor: pointer
Transición: colors
```

| Row | Icono | Label | Extra |
|-----|-------|-------|-------|
| 1 | manage_accounts | Ajustes de cuenta | chevron_right |
| 2 | lock | Inicio de sesión | chevron_right |
| 3 | notifications | Notificaciones | Badge "2" (error) + chevron |
| 4 | group | Entrenadores | chevron_right |
| 5 | insights | Estadísticas | chevron_right |

**Icono container:**
- 40×40dp, rounded-lg, surfaceContainerHighest bg
- Color: onSurfaceVariant → **primary on hover** (group-hover)

**Chevron:**
- Material icon: chevron_right
- Color: outline
- Hover: translate-x-1 (group-hover)

**Badge (Notificaciones):**
```
Fondo: errorContainer (#93000A)
Color: onErrorContainer (#FFD6DA)
Fuente: 10sp (text-[10px]), font-black
Padding: px-1.5 py-0.5
Forma: rounded (4dp)
```

---

### 6. Logout Section

```
Margin top: 48dp (mt-12)
Text align: center
Padding bottom: 48dp (pb-12)
```

**Botón "Cerrar sesión":**
- Color: error (#FFB4AB)
- Fuente: bold, tracking-widest, uppercase, 14sp (text-sm)
- Borde inferior: 2px transparent → error on hover
- Padding: py-1
- Transición: all

**Versión:**
- Texto: "KINETIC VAULT v2.4.0 • RutinApp 2024"
- Color: outline (#8E8FA1)
- Fuente: 12sp (text-xs)
- Opacity: 40%
- Margin top: 24dp (mt-6)

---

## Comportamiento e Interacciones

1. **Edit Profile**: Abre formulario de edición de perfil (nombre, email, avatar)
2. **Theme toggle**: Alterna modo claro/oscuro (animación del thumb)
3. **Settings rows**: Tap abre la sección correspondiente (navegación)
4. **Badge notificaciones**: Muestra número de notificaciones sin leer
5. **Cerrar sesión**: Diálogo de confirmación → logout + navegar a login
6. **Avatar rotate-3**: Efecto visual sutil de inclinación (3 grados)

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Layout | Lista vertical simple | User card prominente + secciones agrupadas |
| Avatar | Icono genérico / no existe | Foto real, rounded-2xl, rotación 3°, verified badge |
| Tags | No existen | "Elite Member" + "Day Streak" |
| Edit button | No existe / text button | Gradient button prominente |
| Theme toggle | Sí existe (AppConfigSheet) | Toggle inline en lista |
| Settings | Sin iconos | Iconos en containers surfaceContainerHighest |
| Sections | Sin agrupación visual | Agrupados con section labels |
| Logout | Sin estilo | Error color, uppercase, underline |

---

## Plan de Implementación

### Paso 1: Rediseñar User Card
- Crear `ProfileUserCard` composable con avatar rotado, verified badge, tags
- Gradient "Edit Profile" button
- Blob decorativo

### Paso 2: Reorganizar Settings
- Agrupar en secciones con section labels (Space Grotesk, uppercase)
- Cada row con icono container → label → chevron
- Theme toggle inline

### Paso 3: Notification Badge
- Mostrar badge con número dinámico en row de Notificaciones

### Paso 4: Logout Section
- Estilizar en error color con uppercase tracking

### Paso 5: Integración
- Migrar lógica actual de ProfilePage.kt y AppConfigSheet.kt
- Reutilizar el FAB existente si se mantiene en esta vista
- Conectar avatar con datos de usuario (Google, email)
