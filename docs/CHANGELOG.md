# Changelog

## 2026-07-13

### Changed — KP (Kinetic Precision) UI Redesign — Exercise, Routine & Workout Sheets

Complete KP design rewrite of all 10 bottom sheet screens:

**Exercise Sheets:**
- `ExerciseListSheet.kt`: Card-based layout with body part badge initials, outlineVariant borders, Card(primaryContainer) create button, uppercase section headers
- `ExerciseDetailSheet.kt`: FlowRow chips for related exercises (replacing LazyVerticalGrid), 48dp header badge, Card-based Edit/Delete/Obtain actions
- `ExerciseCreateSheet.kt`: Form Card(surfaceContainerHigh), Card(primaryContainer) submit with Add icon
- `ExerciseEditSheet.kt`: FlowRow Card chips with Close X for related exercises, Card-based save with Done icon

**Routine Sheets:**
- `RoutineListSheet.kt`: Horizontal LazyRow per body part group, 160dp RoutineCardItem, uppercase section headers, Card(primaryContainer) create button
- `RoutineDetailSheet.kt`: Info Card, exercise list Card section, Card-based Edit/Delete actions
- `RoutineCreateSheet.kt`: Form Card(surfaceContainerHigh), Card(primaryContainer) create button
- `RoutineEditSheet.kt`: All 3 animated sections (content/exercises/relation) — Card-based counters with surfaceContainerHighest bg, colored IconButtons

**Workout Sheets:**
- `WorkoutHistorySheet.kt`: Card(surfaceContainerHigh) sections for recent workouts and routines, Card(primaryContainer) "Entrenar sin rutina" button, Card-based workout actions (PlayArrow/Delete) in ModalBottomSheet
- `ActiveWorkoutSheet.kt`: Card-based finish button (error color with Stop icon), Card(tertiaryContainer) finished state with Done icon

**Design Pattern Applied:**
- All backgrounds: surfaceVariant → surfaceContainerHigh
- All buttons: Button(rutinAppButtonsColours) → Card(primaryContainer) or Card(error α0.15)
- Section headers: uppercase, 11sp Bold, letterSpacing 1.5sp
- Body part badges: 2-char initials in primaryContainer α0.2 box
- Card borders: outlineVariant α0.3
- Card shapes: medium (12dp)

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
