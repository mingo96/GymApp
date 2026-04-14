# Auth and Security Audit (Android)

## Scope

Audit performed on the Android app module to validate authentication flow, token lifecycle, and critical security controls.

## Files Reviewed

- app/src/main/java/com/mintocode/rutinapp/viewmodels/SettingsViewModel.kt
- app/src/main/java/com/mintocode/rutinapp/ui/screens/sheets/AuthSheet.kt
- app/src/main/java/com/mintocode/rutinapp/ui/screens/SettingsScreen.kt
- app/src/main/java/com/mintocode/rutinapp/data/api/v2/ApiV2Module.kt
- app/src/main/java/com/mintocode/rutinapp/data/api/v2/ApiV2Service.kt
- app/src/main/java/com/mintocode/rutinapp/data/api/v2/dto/ResponseDtos.kt
- app/src/main/java/com/mintocode/rutinapp/utils/DataStoreManager.kt
- app/src/main/java/com/mintocode/rutinapp/data/UserDetails.kt
- app/src/main/java/com/mintocode/rutinapp/ui/screens/root/ProfilePage.kt
- app/src/main/java/com/mintocode/rutinapp/data/notifications/NotificationHelper.kt
- app/src/main/java/com/mintocode/rutinapp/data/notifications/RutinAppMessagingService.kt
- app/src/main/res/xml/network_conf.xml
- app/src/main/AndroidManifest.xml

## End-to-End Auth Flow

### 1. Email/password login

1. User opens Auth sheet.
2. User submits email/password.
3. SettingsViewModel calls POST auth/login (or POST auth/register when in register mode).
4. Backend returns access_token + user profile.
5. App stores authToken and user identity in UserDetails.
6. UserDetails is persisted via DataStoreManager.
7. UserDetails.actualValue is updated by DataStore flow collector.
8. AuthInterceptor attaches Authorization: Bearer token on future API calls.

### 2. Google login

1. User taps Google button in Auth sheet.
2. App starts GoogleSignIn intent and obtains Google ID token.
3. App sends ID token to POST auth/google.
4. Backend verifies token and returns Sanctum access token.
5. App attempts Firebase sign-in with credential.
6. If Firebase succeeds or fails, backend token is still persisted locally.
7. AuthInterceptor uses stored token for all authenticated requests.

### 3. Subsequent API requests

1. ApiV2Module creates OkHttp client.
2. AuthInterceptor reads UserDetails.actualValue.authToken.
3. If present, Authorization and Accept headers are added.
4. Request is executed against API v2 endpoints.

### 4. Logout (implemented in this sprint)

1. User taps Cerrar sesion in Profile page.
2. SettingsViewModel best-effort calls:
   - DELETE device/unregister
   - POST auth/logout
3. Firebase session is signed out.
4. Local sensitive session data is sanitized:
   - authToken cleared
   - email cleared
5. Sanitized state is persisted and UI returns to login state.

## Token Lifecycle

### Creation

- Created by backend on auth/login, auth/register, or auth/google.

### Storage

- Persisted in DataStore preferences via DataStoreManager.
- Mirrored in-memory via UserDetails.actualValue singleton.

### Usage

- Auto-attached in ApiV2Module.AuthInterceptor as Bearer token.

### Refresh and expiry

- No refresh token flow implemented.
- No proactive session validation via auth/me during startup.
- No automatic token rotation on 401 responses.

### Deletion

- Implemented now through SettingsViewModel.logOut() + SessionDataSanitizer.

## Findings

### HIGH

1. No token refresh strategy
- Impact: users can remain with invalid sessions until failing requests accumulate.
- Status: pending.

2. Tokens persisted in plain DataStore preferences
- Impact: sensitive token material is not encrypted at rest.
- Status: pending.

### MEDIUM

1. Legacy Google Sign-In API usage
- Impact: deprecation risk and future maintenance burden.
- Status: pending migration.

2. Hardcoded SECRET_CODE in source
- Impact: reverse-engineering and feature bypass risk.
- Status: pending.

3. Backup/extraction defaults not tightened for auth data
- Impact: token-bearing app data may be included in backup/transfer.
- Status: pending.

### FIXED IN THIS SPRINT

1. HTTP request logging hardened
- Before: BODY logging in all builds.
- Now: BODY in debug, NONE in release, sensitive headers redacted.
- Files:
  - app/src/main/java/com/mintocode/rutinapp/data/api/v2/ApiV2Module.kt
  - app/src/main/java/com/mintocode/rutinapp/data/api/v2/HttpLoggingPolicy.kt

2. Logout path implemented end-to-end
- Before: logout endpoint existed but was never used.
- Now: backend logout + local session cleanup + UI action.
- Files:
  - app/src/main/java/com/mintocode/rutinapp/viewmodels/SettingsViewModel.kt
  - app/src/main/java/com/mintocode/rutinapp/security/SessionDataSanitizer.kt
  - app/src/main/java/com/mintocode/rutinapp/ui/screens/root/ProfilePage.kt

3. Network cleartext hardened by default
- Before: cleartext explicitly allowed in main config.
- Now: main disallows cleartext, debug keeps local fallback domains.
- Files:
  - app/src/main/res/xml/network_conf.xml
  - app/src/debug/res/xml/network_conf.xml

4. Sensitive FCM token logging removed
- Before: raw FCM token was logged.
- Now: generic event log only.
- File:
  - app/src/main/java/com/mintocode/rutinapp/data/notifications/RutinAppMessagingService.kt

## Tests Added

- app/src/test/java/com/mintocode/rutinapp/data/api/v2/HttpLoggingPolicyTest.kt
- app/src/test/java/com/mintocode/rutinapp/security/SessionDataSanitizerTest.kt

## Validation Commands

```bash
./gradlew testDebugUnitTest --no-daemon
./gradlew assembleDebug --no-daemon
```

Both commands completed successfully after the changes.
