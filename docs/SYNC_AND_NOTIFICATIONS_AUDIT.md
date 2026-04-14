# Sync and Notifications Audit (Android)

## Scope

Audit of synchronization orchestration, sync state handling, notification pipeline, and deep-link support in the Android app.

## Files Reviewed

- app/src/main/java/com/mintocode/rutinapp/sync/SyncManager.kt
- app/src/main/java/com/mintocode/rutinapp/sync/SyncState.kt
- app/src/main/java/com/mintocode/rutinapp/viewmodels/NotificationsViewModel.kt
- app/src/main/java/com/mintocode/rutinapp/data/repositories/NotificationRepository.kt
- app/src/main/java/com/mintocode/rutinapp/data/notifications/NotificationHelper.kt
- app/src/main/java/com/mintocode/rutinapp/data/notifications/RutinAppMessagingService.kt
- app/src/main/AndroidManifest.xml

## Sync Architecture Summary

###+ Global behavior

- SyncManager centralizes bidirectional sync against API v2.
- Full sync order is explicit and coherent:
  1. Trainer data
  2. Exercises
  3. Routines
  4. Workouts
  5. Planning
  6. Calendar phases
- SyncStateHolder exposes a single global state with:
  - isSyncing
  - lastError
  - lastSuccess

###+ Entity-level sync

- Exercises:
  - Upload: sync/exercises with created and updated payloads
  - Download: GET exercises (mine/public split client-side)
- Routines:
  - Upload: sync/routines
  - Download: GET routines (mine/public split client-side)
- Workouts:
  - Upload: sync/workouts
  - Download: GET workouts
- Planning:
  - Upload: sync/planning (created/updated)
  - Download: GET planning
- Calendar phases:
  - Upload + update: sync/calendar-phases
  - Download: sync/calendar-phases using old client timestamp
- Trainer data:
  - Download: sync/trainer-data (relations + grants)

###+ Observed strengths

- Clear endpoint separation by domain.
- Mapping DTO layer in place (DtoMapper).
- Explicit full-sync ordering prevents obvious dependency races.

###+ Risks and gaps

### HIGH

1. Sync failures are often swallowed as empty results
- Pattern: catch exception and return empty list / default values.
- Impact: silent partial data divergence (user sees stale or missing data without clear recovery).

### MEDIUM

1. Single global sync flag for all sync operations
- Impact: concurrent operations can overwrite state and hide real status.

2. Hardcoded historical timestamps for full downloads
- Pattern: 2000-01-01T00:00:00 used for trainer/calendar phase pulls.
- Impact: unnecessary payload growth and backend load as data scales.

3. No retry/backoff policy in sync layer
- Impact: transient failures are not automatically retried.

4. No conflict-resolution strategy documented
- Impact: last-write-wins behavior may be implicit and not user-visible.

## Notifications Pipeline Summary

###+ Flow

1. FCM token generated/refreshed in RutinAppMessagingService.
2. NotificationHelper can fetch current FCM token and register it in backend.
3. On push message:
  - Service builds local notification.
  - Message is persisted in Room.
4. NotificationsViewModel reads Room reactively and can sync server list.
5. NotificationRepository supports read/delete local+remote actions.

###+ Observed strengths

- Local persistence enables offline visibility.
- Server sync available for consistency.
- Permission flow is integrated for Android 13+.

###+ Risks and gaps

### MEDIUM

1. No deduplication strategy for push + sync overlap
- Impact: duplicate notifications can appear when a push and server sync represent the same event.

2. No robust reconciliation contract documented for local-first operations
- Impact: mark-as-read/delete can diverge under network failures.

3. FCM token registration is best-effort and tied to authenticated state only
- Impact: token refresh while logged out may delay backend registration until next explicit trigger.

## Deep Links Audit

- AndroidManifest only declares launcher intent-filter for MainActivity.
- No app links, no custom scheme links, no route-level deep-link mapping.

### Gap

- External deep-link entry points are not implemented.
- Push payload route handling currently relies on extras in MainActivity intent only.

## Recommended Next Steps

1. Introduce per-domain sync status and richer error model.
2. Add retry with exponential backoff for sync endpoints.
3. Add persisted last-sync timestamps per domain instead of static historical values.
4. Define conflict-resolution policy and expose sync health in UI.
5. Add notification dedup key strategy (server id + type + created_at hash).
6. Add deep-link support (app links/custom scheme) and explicit route parser.
