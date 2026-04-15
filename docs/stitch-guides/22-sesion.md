# 22 — Sesión Iniciada (Session / Signed In)

## Metadatos Stitch
- **Título**: Sesión Iniciada — Session View
- **Dispositivo**: MOBILE
- **Acceso**: Perfil → "Inicio de sesión" (lock → chevron)

---

## Jerarquía Visual

```
Screen (bg-surface #131318)
├── TopBar (fixed, glass, h-14)
│   ├── Back arrow (← navegar a Perfil)
│   └── Título: "Sesión" (headline, bold)
│
├── Main Content (scroll, px-6, pt-20, pb-32)
│   ├── Status Card (hero, surfaceContainer)
│   │   ├── Icono shield_check (tertiary, 48dp, FILL 1)
│   │   ├── "Sesión activa" (headline, bold, tertiary)
│   │   ├── "Conectado desde hace 12 días" (body, onSurfaceVariant)
│   │   └── Pulse indicator (green dot, animate-pulse)
│   │
│   ├── Section: Información de la Sesión
│   │   ├── Row: "Método" → "Google" (con icono G)
│   │   ├── Row: "Email" → "juan@gmail.com"
│   │   ├── Row: "Último acceso" → "Hace 5 minutos"
│   │   ├── Row: "Dispositivo" → "Samsung Galaxy S24"
│   │   └── Row: "Versión app" → "RutinApp v2.4.0"
│   │
│   ├── Section: Seguridad
│   │   ├── Row: "Autenticación 2FA" → Toggle (activo/inactivo)
│   │   ├── Row: "Recordar sesión" → Toggle
│   │   └── Row: "Notificar nuevos accesos" → Toggle
│   │
│   ├── Section: Sesiones Activas
│   │   ├── Device card (actual): Samsung S24 — "Este dispositivo" badge
│   │   ├── Device card: Chrome Windows — "Hace 2 horas"
│   │   └── Link: "Cerrar todas las demás sesiones" (error, underline)
│   │
│   ├── Section: Tokens de Acceso
│   │   ├── Info: "Tu token actual expira en 28 días"
│   │   ├── Progress bar: 28/30 días restantes (tertiary fill)
│   │   └── Botón "Regenerar token" (outlined, primary)
│   │
│   └── Footer
│       ├── Botón "Cerrar sesión" (error, full-width, outlined)
│       └── Texto: "Última sincronización: hace 3 min" (outline, 12sp)
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

**Back arrow:** Material icon `arrow_back`, onSurface, tap → Perfil
**Título:** "Sesión" — Space Grotesk, 18sp, bold, onSurface

---

### 2. Status Card (Hero)

```
Fondo: surfaceContainer (#1F1F24)
Borde: 1px tertiary/20
Forma: rounded-2xl (16dp)
Padding: 32dp
Layout: Column, items-center, text-center
Margin bottom: 32dp
```

**Icono escudo:**
- Material icon: `verified_user` (FILL 1)
- Color: tertiary (#27E0A9)
- Size: 48dp
- Margin bottom: 16dp

**Título:**
- "Sesión activa"
- Fuente: Space Grotesk, 24sp, bold
- Color: tertiary (#27E0A9)

**Subtítulo:**
- "Conectado desde hace 12 días"
- Fuente: Manrope, 14sp, medium
- Color: onSurfaceVariant (#C4C5D7)
- Margin top: 8dp

**Pulse indicator:**
- Position: absolute top-4 right-4
- 8×8dp circle
- Fondo: tertiary (#27E0A9)
- Animación: scale 1→1.5 + opacity 1→0 loop (1.5s)

---

### 3. Información de la Sesión

**Section label:**
```
Fuente: Space Grotesk, 10sp, bold, tracking-[0.2em], uppercase
Color: outline (#8E8FA1), opacity-60
Margin bottom: 12dp
```

**Info rows (space-y-1):**

```
Fondo: surfaceContainerLow (#1B1B20)
Padding: 16dp (p-4)
Forma: rounded-xl (12dp)
Layout: Row, items-center, space-between
```

| Row | Label | Valor | Extra |
|-----|-------|-------|-------|
| 1 | Método | Google | Icono G multicolor (20dp) |
| 2 | Email | juan@gmail.com | — |
| 3 | Último acceso | Hace 5 minutos | Dot verde si <10min |
| 4 | Dispositivo | Samsung Galaxy S24 | Icono smartphone |
| 5 | Versión app | RutinApp v2.4.0 | — |

**Label:**
- Fuente: Manrope, 14sp, medium
- Color: onSurfaceVariant (#C4C5D7)

**Valor:**
- Fuente: Manrope, 14sp, bold
- Color: onSurface (#E4E1E9)
- Alineación: end

---

### 4. Seguridad (Toggles)

```
Margin top: 32dp
```

**Section label:** "SEGURIDAD"

**Toggle rows (space-y-1):**

```
Fondo: surfaceContainerLow (#1B1B20)
Padding: 16dp horizontal, 14dp vertical
Forma: rounded-xl
Layout: Row, items-center, space-between
```

| Row | Icono | Label | Estado por defecto |
|-----|-------|-------|--------------------|
| 1 | security | Autenticación 2FA | OFF |
| 2 | bookmark | Recordar sesión | ON |
| 3 | notifications_active | Notificar nuevos accesos | ON |

**Toggle switch:**
```
Track ON: primary (#BAC3FF), rounded-full, 48×24dp
Track OFF: outlineVariant (#444655), rounded-full
Thumb ON: onPrimary (#00218D), 16dp, shadow-md
Thumb OFF: outline (#8E8FA1), 16dp
Transición: backgroundColor + translateX 200ms
```

**Icono container:**
- 36×36dp, rounded-lg, surfaceContainerHighest bg
- Color icono: onSurfaceVariant → primary cuando toggle ON

---

### 5. Sesiones Activas

```
Margin top: 32dp
```

**Section label:** "SESIONES ACTIVAS"

**Device card (actual):**
```
Fondo: surfaceContainerLow (#1B1B20)
Borde: 1px tertiary/20
Forma: rounded-xl
Padding: 16dp
Layout: Row, items-center, gap-4
```

- Icono: `smartphone` (40×40dp container, surfaceContainerHighest, tertiary color)
- Nombre: "Samsung Galaxy S24" — bold, onSurface
- Badge: "Este dispositivo" — pill tertiary/10, tertiary text, 10sp bold uppercase
- Subtítulo: "Activo ahora" — onSurfaceVariant, 12sp

**Device card (otro):**
```
Fondo: surfaceContainerLow (#1B1B20)
Borde: 1px outlineVariant (#444655)
Forma: rounded-xl
Padding: 16dp
Layout: Row, items-center, space-between
```

- Icono: `computer` (40×40dp container, surfaceContainerHighest, primary color)
- Nombre: "Chrome — Windows" — bold, onSurface
- Subtítulo: "Hace 2 horas" — onSurfaceVariant, 12sp
- Botón right: `close` icon (error), tap → revocar sesión

**Link "Cerrar todas las demás sesiones":**
- Color: error (#FFB4AB)
- Fuente: 14sp, bold, underline
- Margin top: 12dp
- Tap → Diálogo confirmación → revocar todas excepto actual

---

### 6. Tokens de Acceso

```
Margin top: 32dp
```

**Section label:** "TOKEN DE ACCESO"

**Info text:**
- "Tu token actual expira en 28 días"
- Fuente: Manrope, 14sp
- Color: onSurfaceVariant

**Progress bar:**
```
Fondo track: surfaceContainerHighest (#35343A)
Fondo fill: tertiary (#27E0A9)
Altura: 6dp
Forma: rounded-full
Ancho fill: 93% (28/30)
Margin: 12dp vertical
```

**Botón "Regenerar token":**
```
Borde: 1px primary (#BAC3FF)
Fondo: transparent
Color texto: primary
Icono: refresh (20dp)
Fuente: 14sp, bold
Forma: rounded-xl
Padding: px-5 py-3
Hover: bg primary/10
```

**Comportamiento regenerar:**
1. Tap → Diálogo: "¿Regenerar token? Se cerrarán otras sesiones"
2. Confirmar → POST /api/v2/tokens/regenerate
3. Loading → nuevo token generado → toast "Token regenerado"

---

### 7. Footer

```
Margin top: 40dp
Padding bottom: 32dp
```

**Botón "Cerrar sesión":**
```
Ancho: full width
Borde: 1px error (#FFB4AB)
Fondo: transparent → error/10 on press
Color texto: error
Icono leading: logout
Fuente: Space Grotesk, 14sp, bold, uppercase, tracking-widest
Forma: rounded-xl
Padding: py-4
```

**Texto sincronización:**
- "Última sincronización: hace 3 min"
- Color: outline, opacity-40
- Fuente: 12sp
- Text-align: center
- Margin top: 16dp

---

## Comportamiento e Interacciones

1. **Status card**: Muestra estado en tiempo real — verde si activo, naranja si expirando pronto
2. **2FA toggle**: Activar → flujo de verificación (SMS/email/authenticator)
3. **Recordar sesión**: ON = token 30 días, OFF = token 24h
4. **Cerrar otra sesión**: DELETE /api/v2/tokens/{tokenId} → recargar lista
5. **Cerrar todas**: DELETE /api/v2/tokens/others → solo queda token actual
6. **Cerrar sesión**: Diálogo confirmación → DELETE /api/v2/tokens/current → navegar a Login
7. **Pull-to-refresh**: Actualiza información de sesión y lista de dispositivos
8. **Token bar**: Se actualiza dinámicamente con el tiempo restante

---

## Diferencias con Implementación Actual

| Aspecto | Actual | Diseño Stitch |
|---------|--------|---------------|
| Pantalla | No existe | Sheet completa dedicada |
| Info sesión | No visible | Método, email, dispositivo, versión |
| Seguridad toggles | No existen | 2FA, recordar sesión, notificar accesos |
| Sesiones activas | No visible | Lista de dispositivos con revocación |
| Token info | No visible | Expiración + barra de progreso + regenerar |
| Status visual | No existe | Card hero con pulso verde |

---

## Plan de Implementación

### Paso 1: Crear SessionSheet
- Nuevo composable `SessionSheet` en `ui/screens/sheets/`
- Parámetros: `SheetNavigator`, `SessionViewModel`
- Scroll vertical con secciones

### Paso 2: Status Card + Info
- Card hero con icono animado (pulse)
- Rows de información usando datos de Sanctum token + user

### Paso 3: Seguridad
- Toggle filas con persistencia en SharedPreferences / API

### Paso 4: Sesiones Activas
- GET /api/v2/tokens → listar tokens con dispositivo/IP
- Revocar individual: DELETE /api/v2/tokens/{id}
- Revocar todas: DELETE /api/v2/tokens/others

### Paso 5: Token Management
- Mostrar fecha expiración del token Sanctum actual
- Regenerar = revocar actual + crear nuevo

### Paso 6: Backend API
- `TokenController` con métodos index, destroy, destroyOthers, regenerate
- Registrar device info en tabla `personal_access_tokens` (campo extra `device_name`)
