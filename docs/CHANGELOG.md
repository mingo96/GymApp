# Changelog

## 2026-04-14

### Added

- Root Android README with setup, architecture, stack, and build/test instructions.
- Auth and security audit report in docs/AUTH_SECURITY_AUDIT.md.
- Sync and notifications audit report in docs/SYNC_AND_NOTIFICATIONS_AUDIT.md.
- Unit tests:
  - app/src/test/java/com/mintocode/rutinapp/data/api/v2/HttpLoggingPolicyTest.kt
  - app/src/test/java/com/mintocode/rutinapp/security/SessionDataSanitizerTest.kt

### Changed

- Added centralized HttpLoggingPolicy and wired it into ApiV2Module.
- Added release-safe network logging behavior:
  - Debug: BODY
  - Release: NONE
  - Redacted headers: Authorization, Cookie
- Implemented logout flow in SettingsViewModel with backend session revocation and local cleanup.
- Added Profile action for Cerrar sesion when a token is present.
- Hardened network cleartext policy by variant:
  - main: cleartext disabled
  - debug: local/dev cleartext domains allowed
- Removed raw FCM token logging from RutinAppMessagingService.
- Enabled BuildConfig generation in app/build.gradle.kts for build-type-aware policies.

### Security Impact

- Reduced risk of credential leakage in logs.
- Reduced risk of stale authenticated sessions by adding explicit logout path.
- Reduced cleartext traffic exposure in non-debug variants.
