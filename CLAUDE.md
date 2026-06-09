# MJOLNIR

Android app for Emily Springerton. Push notifications from Emily Prime, Apple feed from IDUNA, WebView product panels.

## Stack
- Kotlin 2.0 / Jetpack Compose / Hilt
- Firebase Cloud Messaging (FCM) for push
- Retrofit → IDUNA API
- JGit → APPLES git repo offline cache
- Accompanist WebView for product panels

## Key files
- `app/src/main/kotlin/industrial/einhorn/mjolnir/` — all source
- `app/google-services.json` — **not committed** (gitignored). Copy from `google-services.json.example` and fill in real Firebase project values.
- `docs/NORTHSTAR.md` — intent and milestones
- `docs/SPEC.md` — full architecture spec
- `docs/PUSH_NOTIFICATIONS.md` — FCM design
- `docs/APPLES_INTEGRATION.md` — Apple feed and offline cache design

## Build variants
- **debug**: `IDUNA_BASE_URL=http://10.0.2.2:8090` (emulator host)
- **release**: `IDUNA_BASE_URL=https://iduna.einhorn.industrial`

Set `MJOLNIR_AGENT_NAME=mjolnir-emily` in both variants (already hardcoded in `app/build.gradle.kts`).

## Push token registration
On first launch the app calls `FcmTokenManager.registerIfNeeded()`. This:
1. Fetches the FCM token from Firebase
2. Computes a SHA-256 device fingerprint
3. POSTs to `IDUNA /api/v1/push-tokens` with a bearer JWT

Emily Prime reads the token via `GET /api/v1/push-tokens/mjolnir-emily` and sends pushes via FCM HTTP v1.

## Notifications channels
| Channel ID | Name | Use |
|---|---|---|
| `mjolnir_critical` | Critical Alerts | Bypass DND |
| `mjolnir_high` | High Priority | CEO-visible signals |
| `mjolnir_normal` | Activity | Routine feed updates |

## Dev setup
```sh
cp app/google-services.json.example app/google-services.json
# fill in real Firebase values
./gradlew assembleDebug
```

## Related repos
- `github.com/emilyspringerton/IDUNA` — auth + Apple store + push token registry
- `github.com/emilyspringerton/APPLES` — append-only Apple archive (JGit synced)
- `github.com/emilyspringerton/EMILY` — Emily Prime agent (sends pushes)
