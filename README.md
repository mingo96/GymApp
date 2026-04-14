# RutinApp — Android

App de fitness para usuarios finales. Kotlin + Jetpack Compose con Material Design 3.

## Requisitos

| Herramienta | Versión mínima |
|-------------|----------------|
| Android Studio | Ladybug (2024.2+) |
| JDK | 17 |
| Android SDK | API 36 (compile), API 29+ (min) |
| Gradle | 8.13+ (wrapper incluido) |

## Configuración inicial

### 1. Clonar el repositorio

```bash
git clone <repo-url>
cd RutinApp
```

### 2. Firebase / Google Services

El archivo `app/google-services.json` es necesario para:
- Firebase Authentication (Google Sign-In)
- Firebase Cloud Messaging (notificaciones push)
- Google Ads (AdMob)

> **Nota**: Este archivo contiene configuración del proyecto Firebase `rutinapplication` con package name `com.mintocode.rutinapp`. Contacta al propietario del proyecto si necesitas regenerarlo.

### 3. Variables de entorno y secrets

| Variable | Ubicación | Descripción |
|----------|-----------|-------------|
| `sdk.dir` | `local.properties` | Ruta al Android SDK (se genera automáticamente) |
| Firebase config | `app/google-services.json` | Configuración de Firebase |

### 4. Compilar

```bash
# Debug build
./gradlew assembleDebug

# Release build (requiere signing config)
./gradlew assembleRelease

# Ejecutar en emulador/dispositivo
./gradlew installDebug
```

## Arquitectura

```
MVVM + Use Cases (Clean-ish)
Room DB ← Repository ← UseCase ← ViewModel → Compose UI
```

### Estructura de paquetes

| Paquete | Responsabilidad |
|---------|-----------------|
| `data/api/v2/` | Retrofit API v2 (service, Hilt module, DTOs) |
| `data/daos/` | Room DAO interfaces |
| `data/models/` | Modelos de dominio |
| `data/repositories/` | Repositorios (local + remoto) |
| `data/notifications/` | FCM service + NotificationHelper |
| `domain/` | Use Cases (add, get, update, delete) |
| `sync/` | SyncManager + SyncStateHolder |
| `ui/screens/root/` | 3 páginas raíz (Home, Train, Profile) |
| `ui/screens/sheets/` | 18 bottom sheet composables |
| `ui/navigation/` | SheetNavigator, SheetDestination, SheetHost |
| `ui/theme/` | Material3 theme (Color, Font, Type, Shape) |
| `ui/premade/` | Componentes reutilizables (Calendar, Charts) |
| `viewmodels/` | ViewModels con @HiltViewModel |
| `utils/` | DataStoreManager, date formatting, connectivity |

### Navegación

Sistema de **sheets apilados** (Trade Republic-style):
- `HorizontalPager` con 3 páginas raíz: Home, Entrenar, Perfil
- Sub-pantallas via `ModalBottomSheet` apilados (profundidad progresiva)
- Ver [docs/NAVIGATION_MAP.md](docs/NAVIGATION_MAP.md) para el mapa completo

### API Backend

Base URL: `https://rutynapp.com/api/v2/`

Autenticación via Bearer token (Sanctum). El `AuthInterceptor` adjunta automáticamente el token almacenado.

## Stack técnico

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| Lenguaje | Kotlin | 2.2.20 |
| UI | Jetpack Compose | BOM 2025.01.01 |
| DI | Dagger Hilt | 2.56.2 |
| DB Local | Room | 2.7.2 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Auth | Google Sign-In + Firebase Auth | — |
| Push | Firebase Cloud Messaging | BOM 33.8.0 |
| Ads | Google AdMob | 23.6.0 |
| Charts | Compose Charts | ehsannarmani |

## Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests
./gradlew connectedDebugAndroidTest
```

## Build & Release

```bash
# AAB para Play Store
./gradlew bundleRelease

# APK firmado
./gradlew assembleRelease
```

El APK/AAB de release se genera en `app/release/`.

## Documentación adicional

- [Mapa de navegación](docs/NAVIGATION_MAP.md)
- [Diseño](docs/DESIGN_DOCUMENT.md)
- [Auditoría de auth y seguridad](docs/AUTH_SECURITY_AUDIT.md)
- [Auditoría de sync y notificaciones](docs/SYNC_AND_NOTIFICATIONS_AUDIT.md)
- [Changelog](docs/CHANGELOG.md)
