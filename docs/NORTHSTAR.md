# MJOLNIR — Northstar
## Android Intelligence Terminal for Emily Springerton

**Status**: Greenfield — northstar authored 2026-06-09  
**Owner**: Emily Prime / EINHORN_INDUSTRIAL  
**Platform**: Android (Kotlin, minSdk 26 / targetSdk 34)  
**Depends on**: IDUNA (auth + Apples API), EMILY (push trigger), APPLES (git sync)

---

## EXECUTIVE INTENT

MJOLNIR is the **mobile front door** for the EINHORN_INDUSTRIAL intelligence stack.
It does three things:

1. **Push notifications to Emily (CEO)** — Emily Prime fires FCM messages when something
   requires attention: critical Apple filed, signal spike, RSI finding, morning brief.
   Emily's phone is the last mile of the autonomous loop.

2. **Apple feed** — Live scrollable view of Apples posted to IDUNA, sourced from the
   `/api/v1/apples` endpoint. The app is the human-readable layer over what the agent
   network has accomplished and flagged.

3. **Product front door** — Deep-linkable entry points to the live products:
   - FatBaby news site (`:8082`) rendered in WebView
   - Signal API query UI (`:8083`)
   - TYLER episode feed
   - SHANKPIT server status

**Why Android-first**: Emily uses Android. FCM is the canonical push channel. The Web
layer comes later; the native app provides richer notification controls (critical-priority
channels, notification actions, lock-screen visibility).

---

## ARCHITECTURE

```
Emily Prime (EMILY repo)
      │  FCM push via Google FCM HTTP v1 API
      │  (server key stored in IDUNA secrets)
      ▼
Google FCM ──► MJOLNIR Android App
                    │
                    ├── ApplesFeed (IDUNA /api/v1/apples)
                    │     └── polled every 30s + push-triggered refresh
                    │
                    ├── WebView panels (FatBaby newssite, SignalAPI)
                    │
                    └── IDUNA auth (M2M + Google OAuth)
```

### Push flow
1. Emily Prime detects escalation condition (critical Apple, CEO visibility flag, RSI alert)
2. Emily Prime calls `POST https://fcm.googleapis.com/v1/projects/{project}/messages:send`
   with the device FCM token (registered at first app launch, stored in IDUNA as device credential)
3. Notification arrives on Emily's phone with:
   - Title: Apple type + source repo
   - Body: Apple title (first 140 chars)
   - Data payload: `apple_id`, `source_repo`, `priority`
   - Android notification channel: `CRITICAL` (bypasses DND for severity ≥ critical)
4. Tap opens MJOLNIR directly to the relevant Apple detail screen

### Apple feed
- Authenticates with IDUNA via saved M2M credential (device-scoped, stored in EncryptedSharedPreferences)
- Polls `GET /api/v1/apples?limit=50` on 30s interval
- Renders reverse-chronological list with type badge + source repo chip
- Long-press = share; tap = full Apple detail with raw JSON viewer

### Auth
- First launch: Google Sign-In → exchange for IDUNA JWT via `/auth/google`
- App stores JWT in EncryptedSharedPreferences (AES-GCM via Android Keystore)
- M2M device credential registered after first login (`POST /api/v1/agents`)

---

## NORTHSTAR MILESTONES

### Milestone 0: Foundation (current)
- [ ] Android project skeleton (Kotlin, Jetpack Compose, Hilt DI)
- [ ] IDUNA client (Retrofit + OkHttp, JWT injector interceptor)
- [ ] FCM token registration flow → store token on IDUNA device record
- [ ] Apple feed UI (LazyColumn, AppleCard composable)

### Milestone 1: Push notifications live
- [ ] Emily Prime FCM sender (`pkg/fcm/sender.go` in EMILY repo)
- [ ] IDUNA device token storage (`device_tokens` table + API endpoint)
- [ ] Android notification channels: CRITICAL, HIGH, NORMAL
- [ ] Push → in-app deep link (Apple detail screen)

### Milestone 2: Product front door
- [ ] WebView tab for FatBaby newssite (`:8082`)
- [ ] WebView tab for SignalAPI (`:8083`)
- [ ] TYLER episode list (parsed from TYLER repo EPISODES.md or future API)
- [ ] SHANKPIT server ping status card

### Milestone 3: APPLES git sync display
- [ ] Clone/pull APPLES git repo on device (or read via GitHub API)
- [ ] Browse Apples by date in APPLES/ folder structure
- [ ] Compare with IDUNA live feed (offline-capable via git)

### Milestone 4: RSI observability
- [ ] Emily Prime pushes RSI cycle completion notifications
- [ ] App shows RSI loop state (read from `EMILY/var/rsi-loop-state.json` via EMILY API)
- [ ] Token spend sparkline (last 7 days from IDUNA Apples)

---

## FILE STRUCTURE (target)

```
MJOLNIR/
  app/
    src/main/
      kotlin/industrial/einhorn/mjolnir/
        ui/
          ApplesFeed.kt        — main feed screen
          AppleDetail.kt       — single Apple view
          WebViewScreen.kt     — product front door panel
          NotificationHandler.kt
        data/
          IdunaClient.kt       — Retrofit IDUNA API
          AppleRepository.kt
          FcmTokenManager.kt
        di/
          AppModule.kt         — Hilt wiring
        MainActivity.kt
      res/
        values/strings.xml
        values/colors.xml
  docs/
    NORTHSTAR.md               ← this file
    SPEC.md                    ← detailed technical spec
    PUSH_NOTIFICATIONS.md      ← FCM integration guide
    APPLES_INTEGRATION.md      ← Apple feed + APPLES repo sync
```

---

## DESIGN PRINCIPLES

- **No logic on device** — MJOLNIR is a reader/notifier. Intelligence lives in the server stack.
- **IDUNA is the auth layer** — no custom auth. All credentials flow through IDUNA JWT.
- **Push is the primary loop** — the app does not poll aggressively. FCM wakes it.
- **APPLES are the record of truth** — every significant action in the stack produces an Apple.
  The feed IS the product.
- **Offline-capable for Apples** — the APPLES git repo provides a durable offline cache.

---

## RECURSIVE SELF-IMPROVEMENT HOOK

Emily Prime can audit MJOLNIR indirectly:
1. Push a test notification → confirm receipt via IDUNA Apple (device acks)
2. Read MJOLNIR crash reports from Play Console → post as observation
3. Emily Prime Selenium audit of the web products (`:8082`) validates what the WebView shows
4. RSI loop adds MJOLNIR audit presets to `PRESET_LIST` once the app is live

When Milestone 1 is complete, the push notification path becomes part of the RSI feedback loop:
Emily Prime's cycle completion messages land on Emily's phone.

---

*MJOLNIR NORTHSTAR | EINHORN_INDUSTRIAL | 2026-06-09*
*The phone is the last mile. Every Apple reaches Emily.*
