# 17 — Autenticación (Auth Sheet)

## Metadatos Stitch

| Campo | Valor |
|-------|-------|
| Screen ID | `e7c913687d5e41f7a7f910b07c3f802b` |
| Nombre | AuthSheet (Login/Register) |
| Tipo | BottomSheet modal (animate slide-in-from-bottom) |
| Ancho diseño | 780 px (móvil) |
| Max-height content | 707 px |
| Fondo sheet | `#1A1A24` (auth-sheet) |

---

## Jerarquía Visual

```
Overlay  bg-black/60  backdrop-blur-sm
└── AuthSheet  bg-[#1A1A24]  rounded-t-32  shadow-2xl  slide-in-from-bottom-500ms
    ├── DragHandle  w-10 h-1.5  outlineVariant/40  rounded-full
    ├── Header  px-6 py-4
    │   ├── BackButton  40dp  rounded-full  bg-surfaceContainerHigh
    │   │   └── Icon "arrow_back"  onSurface
    │   └── Title "Iniciar sesión"  headline  2xl  bold  tracking-tight
    └── Content  px-8  pt-2  pb-12  scroll  max-h-707
        ├── WelcomeText  onSurfaceVariant  medium  leading-relaxed  mb-8
        ├── Form  space-y-6
        │   ├── EmailField
        │   │   ├── Label "CORREO ELECTRÓNICO"  xs  bold  uppercase  tracking-widest  onSurfaceVariant
        │   │   └── Input  h-14  bg-surfaceContainerHigh  rounded-xl  focus:ring-2 primary/30
        │   │       ├── IconLeft "mail"  outline  20sp
        │   │       ├── Placeholder  outline/50
        │   │       └── IconRight "check_circle" FILL=1  tertiary  (validación OK)
        │   ├── PasswordField
        │   │   ├── Label "CONTRASEÑA"  xs  bold  uppercase  tracking-widest  onSurfaceVariant
        │   │   ├── Input  h-14  bg-surfaceContainerHigh  rounded-xl
        │   │   │   ├── IconLeft "lock"  outline  20sp
        │   │   │   └── ToggleVisibility "visibility"  outline  → hover: onSurface
        │   │   └── ForgotPassword "¿Olvidaste tu contraseña?"  sm  semibold  primary
        │   ├── RegisterToggle
        │   │   ├── Label "¿Nuevo en RutinApp?"  onSurface  medium
        │   │   └── Switch  w-12 h-6
        │   │       ├── Track:Off  bg-surfaceContainerHighest
        │   │       ├── Track:On  bg-primaryContainer (#4361EE)
        │   │       ├── Thumb  20dp  white  rounded-full
        │   │       └── SwitchLabel "Registrarse"  sm  bold  onSurfaceVariant
        │   └── SubmitButton "Iniciar sesión"  w-full  h-14
        │       ├── Background  gradient(primary → primaryContainer)
        │       ├── Text  onPrimaryFixed  bold
        │       ├── Icon "arrow_forward"  20sp
        │       └── Shadow  primaryContainer/20
        ├── Divider
        │   ├── Line  border-t outlineVariant/30
        │   └── Label "o"  bg-auth-sheet  outline  xs  bold  uppercase  tracking-[0.2em]
        ├── GoogleButton  w-full  h-14
        │   ├── Border  2px  outlineVariant/30  hover: outlineVariant
        │   ├── Fondo  transparent  hover: white/5
        │   ├── GoogleIcon  SVG 20dp (4 colores)
        │   └── Text "Continuar con Google"  onSurface  bold
        └── Disclaimer  center  xs  onSurfaceVariant/60  medium
            └── Links "Términos de Servicio" / "Política de Privacidad"  underline
```

---

## Componentes Detallados

### 1. Input Fields

| Propiedad | Valor |
|-----------|-------|
| Alto | 56 dp (h-14) |
| Fondo | `surfaceContainerHigh` (#2A292F) |
| Borde | ninguno (`border-none`) |
| Redondeo | `rounded-xl` (12 dp) |
| Focus ring | `ring-2 primary/30` |
| Padding izquierdo | `pl-12` (icono) |
| Padding derecho | `pr-12` (acción/validación) |
| Texto | `onSurface` (#E4E1E9) |
| Placeholder | `outline/50` |

**Icono izquierdo:** `outline` (#8E8FA1), 20sp, `pointer-events-none`

**Validación (email):** `check_circle` FILL=1, `tertiary` (#27E0A9) — aparece cuando el email es válido

**Toggle visibility (password):** `visibility`/`visibility_off`, `outline` → hover `onSurface`

### 2. Labels

| Propiedad | Valor |
|-----------|-------|
| Texto | Uppercase (ej: "CORREO ELECTRÓNICO") |
| Font | `Manrope`, `xs`, `bold`, `uppercase`, `tracking-widest` |
| Color | `onSurfaceVariant` (#C4C5D7) |
| Margin-left | `ml-1` (4 dp) |

### 3. Register Toggle (Switch)

| Propiedad | Valor |
|-----------|-------|
| Track ancho | 48 dp (w-12) |
| Track alto | 24 dp (h-6) |
| Track off | `surfaceContainerHighest` (#35343A) |
| Track on | `primaryContainer` (#4361EE) |
| Thumb | 20×20 dp, `white`, `rounded-full` |
| Label izq | "¿Nuevo en RutinApp?", `onSurface`, `medium` |
| Label der | "Registrarse", `sm`, `bold`, `onSurfaceVariant` |

### 4. Submit Button (Primary CTA)

| Propiedad | Valor |
|-----------|-------|
| Alto | 56 dp |
| Fondo | `gradient(primary #BAC3FF → primaryContainer #4361EE)` dirección `to-br` |
| Texto | `onPrimaryFixed`, `bold` |
| Icono | `arrow_forward`, 20sp |
| Redondeo | `rounded-xl` |
| Sombra | `primaryContainer/20` |
| Press | `scale(0.98)` |

### 5. Google Button (Social Auth)

| Propiedad | Valor |
|-----------|-------|
| Alto | 56 dp |
| Fondo | `transparent` |
| Borde | `2px outlineVariant/30` — hover: `outlineVariant` |
| Hover fondo | `white/5` |
| Icono | SVG Google 20×20 dp (4 colores: #EA4335, #4285F4, #FBBC05, #34A853) |
| Texto | "Continuar con Google", `onSurface`, `bold` |
| Press | `scale(0.98)` |

### 6. Divider "o"

| Propiedad | Valor |
|-----------|-------|
| Línea | `border-t outlineVariant/30` |
| Texto | "o", `outline`, `xs`, `bold`, `uppercase`, `tracking-[0.2em]` |
| Fondo texto | `#1A1A24` (para cubrir la línea), `px-4` |
| Margin vertical | `my-8` (32 dp) |

### 7. Forgot Password Link

| Propiedad | Valor |
|-----------|-------|
| Texto | "¿Olvidaste tu contraseña?" |
| Color | `primary` (#BAC3FF) |
| Size | `sm` |
| Weight | `semibold` (600) |
| Hover | `primary-fixed` |
| Posición | `flex justify-end` (alineado derecha) |

### 8. Disclaimer

| Propiedad | Valor |
|-----------|-------|
| Color | `onSurfaceVariant/60` |
| Size | `xs` |
| Weight | `medium` |
| Links | `underline` |
| Margin top | `mt-8` (32 dp) |
| Alineación | centrado |

---

## Comportamiento e Interacciones

| Interacción | Efecto |
|-------------|--------|
| Toggle "Registrarse" | Cambia form a modo registro (campos adicionales: nombre, confirmar contraseña) |
| Tap "Iniciar sesión" | Valida campos, autenticación con backend |
| Tap "Continuar con Google" | Flujo OAuth Google |
| Tap "¿Olvidaste tu contraseña?" | Navega a recuperación |
| Email válido | Muestra check_circle tertiary |
| Error validación | Input ring cambia a error (#FFB4AB) |
| Tap back | Cierra sheet |
| Slide-in | Animación de entrada desde abajo (500ms) |

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Stitch KP |
|---------|--------|-----------|
| Auth screen | Dialog o page completa | BottomSheet modal con slide-in animation |
| Input fields | Standard TextField | Custom con icon left/right, surfaceContainerHigh, rounded-xl |
| Validation icon | N/A | check_circle tertiary en email cuando válido |
| Register toggle | Pantalla separada | Switch inline "¿Nuevo en RutinApp?" |
| Google auth | Botón estándar | Outlined con SVG nativo, hover states |
| Divider | Simple | "o" con background trick para overlay sobre línea |
| Forgot password | Link genérico | Alineado derecha, primary, semibold |
| Disclaimer | N/A o básico | Links con underline, onSurfaceVariant/60 |

---

## Plan de Implementación

1. **Crear `AuthSheet` composable** — ModalBottomSheet con bg `#1A1A24`, rounded-t-32, slide-in animation
2. **AuthTextField** — Componente reutilizable con leadingIcon, trailingIcon (validación o toggle), surfaceContainerHigh bg, focus ring primary/30
3. **Register/Login toggle** — Mutable state que muestra/oculta campos adicionales con Switch Material3 (colors: checkedTrackColor=primaryContainer)
4. **GradientSubmitButton** — Reutilizar o extender `rutinAppButtonsColours()` con brush gradient
5. **GoogleSignInButton** — OutlinedButton con SVG icon, border outlineVariant/30
6. **DividerWithLabel** — Row con 2 Dividers + Text "o" centrado
7. **AuthViewModel** — Login/register state, email validation (tertiary check on valid), password visibility toggle, Google OAuth flow
