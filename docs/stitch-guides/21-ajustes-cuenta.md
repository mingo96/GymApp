# 21 — Ajustes de Cuenta (Account Settings)

## Metadatos Stitch
- **Screen ID (Desktop)**: `7a629ffcbb1d404baaabb870f447e9bd`
- **Título**: Ajustes de Cuenta — Account Settings
- **Dispositivo**: MOBILE (diseño principal) + DESKTOP (referencia)
- **Acceso**: Perfil → "Ajustes de cuenta" (manage_accounts → chevron)

---

## Jerarquía Visual

```
Screen (bg-surface #131318)
├── TopBar (fixed, glass, h-14)
│   ├── Back arrow (← navegar a Perfil)
│   └── Título: "Ajustes de cuenta" (headline, bold)
│
├── Main Content (scroll, px-6, pt-20, pb-32)
│   ├── Avatar Section
│   │   ├── Avatar grande (96dp, rounded-2xl, centered)
│   │   ├── Edit overlay icon (camera, bottom-right)
│   │   └── Nombre completo + email debajo
│   │
│   ├── Tab Row (pill selector)
│   │   ├── "Cuenta" (activo, primary bg)
│   │   ├── "Notificaciones"
│   │   └── "Apariencia"
│   │
│   ├── Section: Datos Personales
│   │   ├── TextField "NOMBRE" → "Juan"
│   │   ├── TextField "APELLIDO" → "Pérez"
│   │   └── TextField "EMAIL" → "juan@gmail.com" + Badge "VERIFICADO"
│   │
│   ├── Section: Código Secreto
│   │   ├── Label + info tooltip
│   │   └── PasswordField (dots) con toggle visibilidad
│   │
│   ├── Section: Seguridad y Conexiones
│   │   ├── Botón "Cambiar contraseña" (outlined, icon lock)
│   │   └── Botón "Vincular Google" (outlined, icon G)
│   │
│   ├── Section: Zona de Peligro
│   │   ├── Botón "Cerrar sesión" (error, outlined, icon logout)
│   │   └── Botón "Eliminar cuenta" (error, text, icon delete)
│   │
│   └── Stats KPI Row (centered, 3 items)
│       ├── "12 DÍAS" (racha)
│       ├── "14 ENTRENAMIENTOS"
│       └── "82% ADHERENCIA"
│
└── Bottom: Botón "GUARDAR CAMBIOS →" (gradient primary, full width, fixed)
```

---

## Componentes Detallados

### 1. TopBar

```
Posición: fixed top-0, full width, z-50
Fondo: #131318/80 + backdrop-blur-xl
Borde inferior: white/5
Altura: 56dp (h-14)
Layout: Row, items-center, px-6
```

**Back arrow (left):**
- Material icon: `arrow_back`
- Color: onSurface (#E4E1E9)
- Tap: navegar atrás a Perfil
- Size: 24dp

**Título (center o left-offset):**
- Texto: "Ajustes de cuenta"
- Fuente: Space Grotesk, 18sp, bold
- Color: onSurface

---

### 2. Avatar Section

```
Layout: Column, items-center
Padding top: 24dp
Margin bottom: 32dp
```

**Avatar:**
- 96×96dp, rounded-2xl (16dp)
- Imagen de perfil del usuario
- Sombra: shadow-xl
- Borde: 2px primary/20

**Edit overlay:**
- Position: absolute bottom-0 right-0
- 32×32dp circle, bg tertiary (#27E0A9)
- Icono: camera (onTertiary #003827), 16dp
- Tap: abrir selector de imagen (galería/cámara)

**Nombre:**
- Fuente: Space Grotesk, 24sp, bold
- Color: onSurface
- Margin top: 16dp

**Email:**
- Fuente: Manrope, 14sp, medium
- Color: onSurfaceVariant (#C4C5D7)
- Margin top: 4dp

**Badge (opcional):**
- Pill: "ELITE MEMBER"
- Fondo: tertiary/10
- Color: tertiary
- Fuente: 10sp, bold, tracking-widest, uppercase
- Margin top: 8dp

---

### 3. Tab Row (Pill Selector)

```
Layout: Row, gap-2, px-4
Fondo container: surfaceContainerLow (#1B1B20)
Forma container: rounded-full
Padding container: 4dp
Margin bottom: 32dp
```

| Tab | Estado | Fondo | Color |
|-----|--------|-------|-------|
| Cuenta | **Activo** | primary (#BAC3FF) | onPrimary (#00218D) |
| Notificaciones | Inactivo | transparent | outline (#8E8FA1) |
| Apariencia | Inactivo | transparent | outline (#8E8FA1) |

- Fuente: Manrope, 12sp, bold
- Forma pill: rounded-full
- Padding pill: px-4 py-2
- Hover inactivo: surfaceContainerHigh
- Transición: backgroundColor 200ms

---

### 4. Datos Personales

**Section label:**
```
Fuente: Space Grotesk, 10sp, bold, tracking-[0.2em], uppercase
Color: outline (#8E8FA1), opacity-60
Margin bottom: 16dp
```

**TextFields (compartido):**
```
Fondo: surfaceContainerLow (#1B1B20)
Borde: 1px outlineVariant (#444655)
Focus border: 1px primary (#BAC3FF)
Forma: rounded-xl (12dp)
Padding: 16dp horizontal, 14dp vertical
Fuente input: Manrope, 16sp, medium
Color input: onSurface (#E4E1E9)
Fuente label: Space Grotesk, 10sp, bold, tracking-widest, uppercase
Color label: outline (#8E8FA1)
Margin bottom entre fields: 16dp
```

**Badge "VERIFICADO" (en campo email):**
- Position: inline-end del campo
- Color: tertiary (#27E0A9)
- Fuente: 10sp, bold, uppercase tracking
- Padding: px-2 py-1

---

### 5. Código Secreto

```
Margin top: 32dp
```

**Label:**
- "CÓDIGO SECRETO" — misma tipografía que section labels
- Info tooltip: "ESTE SERÁ SU CÓDIGO DE ACTIVACIÓN ÚNICO"
  - Fuente: 10sp, italic
  - Color: onSurfaceVariant

**PasswordField:**
- Mismos estilos que TextField
- Input type: password (dots ••••••••••)
- Toggle icono: visibility / visibility_off (right)
  - Color: outline (#8E8FA1)
  - Hover: onSurface

---

### 6. Seguridad y Conexiones

```
Margin top: 32dp
```

**Section label:** "SEGURIDAD Y CONEXIONES"

**Botones (Row, gap-3, flex-wrap):**

| Botón | Icono | Texto | Estilo |
|-------|-------|-------|--------|
| Cambiar contraseña | lock | "Cambiar contraseña" | Outlined, onSurface |
| Vincular Google | Google "G" | "Vincular Google" | Outlined, onSurface |

```
Borde: 1px outlineVariant (#444655)
Hover borde: primary (#BAC3FF)
Hover fondo: surfaceContainerHigh (#2A292F)
Forma: rounded-xl (12dp)
Padding: px-5 py-3
Fuente: Manrope, 14sp, medium
Layout: Row, gap-3, items-center
Icono: 20dp, onSurfaceVariant
```

---

### 7. Zona de Peligro

```
Margin top: 40dp
```

**Section label:** "ZONA DE PELIGRO"
- Color label: error (#FFB4AB) en lugar de outline

**Botón "Cerrar sesión":**
```
Borde: 1px error/30
Hover borde: error
Fondo: transparent → error/5 on hover
Color texto: error (#FFB4AB)
Icono: logout (FILL 0)
Fuente: 14sp, bold
Forma: rounded-xl
Padding: px-5 py-3
```

**Botón "Eliminar cuenta":**
```
Fondo: transparent
Color texto: error (#FFB4AB), opacity-60
Icono: delete_forever
Fuente: 14sp, medium
Hover: opacity-100
Sin borde
```

**Comportamiento "Eliminar cuenta":**
1. Tap → Diálogo de confirmación
2. Título: "¿Eliminar cuenta permanentemente?"
3. Descripción: "Esta acción no se puede deshacer. Se borrarán todos tus datos."
4. Input: escribir "ELIMINAR" para confirmar
5. Botón confirmar: fondo error, disabled hasta que se escriba "ELIMINAR"

---

### 8. Stats KPI Row

```
Layout: Row, justify-evenly
Fondo: surfaceContainerLow (#1B1B20)
Forma: rounded-xl
Padding: 20dp
Margin top: 32dp
```

| KPI | Valor | Label |
|-----|-------|-------|
| Racha | 12 | DÍAS |
| Entrenamientos | 14 | ENTRENAMIENTOS |
| Adherencia | 82% | ADHERENCIA |

- Valor: Space Grotesk, 28sp, bold, onSurface
- Label: Manrope, 10sp, bold, outline, uppercase, tracking-widest
- Separador vertical: 1px h-8 white/10 entre items

---

### 9. Botón "GUARDAR CAMBIOS"

```
Posición: fixed bottom-0, px-6, pb-safe (bottom inset)
Ancho: full - 48dp (mx-6)
Fondo: gradient to-r from-primary (#BAC3FF) to-primaryContainer (#4361EE)
Color texto: onPrimary (#00218D)
Fuente: Space Grotesk, 14sp, bold, tracking-widest, uppercase
Icono trailing: arrow_forward
Forma: rounded-xl (12dp)
Padding: py-4
Sombra: shadow-xl shadow-primary/20
Estado disabled: opacity-50, sin sombra
```

**Comportamiento:**
- Disabled por defecto hasta que el usuario modifique algún campo
- Al tap: guardar cambios → mostrar toast "Cambios guardados ✓"
- Loading state: CircularProgressIndicator reemplaza texto

---

## Comportamiento e Interacciones

1. **Avatar edit**: Tap en overlay → BottomSheet con opciones (Galería, Cámara, Eliminar foto)
2. **Tab navigation**: Tap en tab → slide horizontal del contenido con animación
3. **Campo email**: Solo lectura si está verificado (border tertiary en vez de outline)
4. **Código secreto**: Se genera automáticamente en el registro; el usuario puede regenerarlo
5. **Vincular Google**: Inicia flujo OAuth2 Google Sign-In
6. **Cambiar contraseña**: Navega a pantalla dedicada o abre BottomSheet
7. **Guardar**: Valida campos → API PUT /api/v2/user → actualiza local → toast confirmación
8. **Swipe back**: Navega a Perfil (con animación slide-right)

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Pantalla dedicada | No existe (solo toggle en Perfil) | Sheet completa con tabs |
| Edición de datos | No implementado | Campos editables con validación |
| Avatar editable | No existe | Con overlay de cámara |
| Código secreto | No visible | Campo con toggle visibilidad |
| Google linking | No implementado | Botón de vinculación |
| Zona de peligro | Solo "Cerrar sesión" en Perfil | Cerrar sesión + Eliminar cuenta |
| Tabs | No existen | Cuenta / Notificaciones / Apariencia |
| Stats inline | No existen aquí | KPI row con racha, entrenamientos, adherencia |

---

## Plan de Implementación

### Paso 1: Crear AccountSettingsSheet
- Nuevo composable `AccountSettingsSheet` en `ui/screens/sheets/`
- Parámetros: `SheetNavigator`, `AccountSettingsViewModel`
- Estructura con tabs usando `HorizontalPager`

### Paso 2: Tab "Cuenta"
- TextFields para nombre, apellido, email
- Sección de código secreto con PasswordVisualTransformation
- Botones de seguridad (cambiar contraseña, vincular Google)
- Zona de peligro con diálogo de confirmación

### Paso 3: Tab "Notificaciones" (enlaza con guía 13)
- Reutilizar configuración de notificaciones de la guía 13

### Paso 4: Tab "Apariencia"
- Toggle tema oscuro/claro (migrar de AppConfigSheet)
- Selector de color accent (si se implementa)

### Paso 5: Avatar editable
- Integrar `ActivityResultContracts.GetContent()` para galería
- `ActivityResultContracts.TakePicture()` para cámara
- Subir a API con multipart

### Paso 6: API & Validación
- Form Request en Laravel: `UpdateProfileRequest`
- Endpoint: `PUT /api/v2/user` (ya existente o crear)
- Validación cliente: campos requeridos, email formato, etc.
