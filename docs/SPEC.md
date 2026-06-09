# MJOLNIR Technical Spec
## Android Intelligence Terminal — Build 0001

**Date**: 2026-06-09  
**Status**: Pre-implementation spec

---

## Stack

| Layer | Choice | Rationale |
|-------|--------|-----------|
| Language | Kotlin 1.9 | Android-native, Compose-ready |
| UI | Jetpack Compose + Material 3 | Modern declarative, no XML layouts |
| DI | Hilt | Dagger-backed, Compose-native |
| HTTP | Retrofit 2 + OkHttp 4 | Battle-tested, interceptor chain for JWT |
| Auth | Google Sign-In + IDUNA JWT | Single sign-on, no separate password |
| Push | Firebase Cloud Messaging v1 | Google-native, IDUNA stores device tokens |
| Storage | EncryptedSharedPreferences | JWT + secrets, AES-GCM, Android Keystore |
| Git sync | JGit (offline Apples cache) | Pure Java, no native binary required |
| Build | Gradle Kotlin DSL | |
| minSdk | 26 (Android 8.0) | EncryptedSharedPreferences requires 26 |
| targetSdk | 34 | |

---

## Module Structure

```
app/
  build.gradle.kts
  google-services.json          ← NOT committed (pulled from CI secrets)
  src/main/
    AndroidManifest.xml
    kotlin/industrial/einhorn/mjolnir/
      MjolnirApp.kt             ← Hilt @HiltAndroidApp
      MainActivity.kt           ← NavHost, notification deep-link handler
      ui/
        theme/
          Theme.kt              ← Material 3 dark theme (EINHORN palette)
          Color.kt
        apples/
          ApplesFeedScreen.kt   ← main screen: LazyColumn of AppleCards
          AppleDetailScreen.kt  ← single Apple, raw JSON expandable
          AppleCard.kt          ← composable card
          AppleViewModel.kt     ← StateFlow<List<Apple>>, poll + push refresh
        products/
          ProductsScreen.kt     ← tab row: FatBaby | SignalAPI | TYLER | SHANKPIT
          WebViewScreen.kt      ← Accompanist WebView wrapper
        auth/
          LoginScreen.kt        ← Google Sign-In button
          AuthViewModel.kt
        rsi/
          RsiStatusScreen.kt    ← RSI loop state display (Milestone 4)
      data/
        model/
          Apple.kt              ← data class
          DeviceToken.kt
          RsiLoopState.kt
        remote/
          IdunaClient.kt        ← Retrofit interface
          IdunaAuthInterceptor.kt ← injects Bearer JWT
          FcmTokenManager.kt    ← registers FCM token with IDUNA
        local/
          ApplesGitSyncWorker.kt ← WorkManager: clone/pull APPLES repo
          ManifestParser.kt     ← parse APPLES/MANIFEST.json
        repository/
          ApplesRepository.kt   ← merges remote + local, exposes Flow<List<Apple>>
          AuthRepository.kt
      di/
        NetworkModule.kt        ← Hilt: Retrofit, OkHttp
        DatabaseModule.kt
        AppModule.kt
      notification/
        MjolnirMessagingService.kt ← FirebaseMessagingService subclass
        NotificationChannels.kt    ← creates CRITICAL / HIGH / NORMAL channels
    res/
      values/
        strings.xml
        colors.xml              ← EINHORN palette: #1A1A2E dark, #E94560 accent
      drawable/
        ic_apple.xml            ← Apple icon (vector)
        ic_notification.xml
      xml/
        network_security_config.xml ← allow cleartext localhost for dev
```

---

## IDUNA API Endpoints Required

These must be implemented on the IDUNA side (backlog item):

```
POST /api/v1/devices/register
  Body: { agent_name, platform, fcm_token, device_fingerprint }
  Auth: JWT (any authenticated user)
  Response: { device_id, registered_at }

GET /api/v1/devices/{agent_name}/token
  Auth: M2M (emily-prime agent only)
  Response: { fcm_token, last_used_at }

GET /api/v1/apples
  Query: limit, offset, source_repo, severity, since
  Auth: JWT
  Response: { apples: [...], total, next_offset }
```

---

## Auth Flow

```
First launch:
  1. LoginScreen shows Google Sign-In button
  2. User signs in → Google ID token received
  3. POST /auth/google { id_token } → IDUNA JWT + refresh token
  4. Store JWT in EncryptedSharedPreferences
  5. Register FCM token with IDUNA (POST /api/v1/devices/register)
  6. Navigate to ApplesFeedScreen

Subsequent launches:
  1. Load JWT from EncryptedSharedPreferences
  2. Validate expiry; if expired, refresh via /auth/refresh
  3. Navigate directly to ApplesFeedScreen

Token refresh:
  - IdunaAuthInterceptor catches 401 → triggers refresh → retries request
  - Refresh failure → clear stored credentials → show LoginScreen
```

---

## Notification Handling

```
App foregrounded:
  MjolnirMessagingService.onMessageReceived()
  → ApplesFeedViewModel.triggerRefresh()
  → show in-app snackbar with Apple title + "View" action

App backgrounded:
  FCM SDK delivers system tray notification automatically
  (notification payload handled by OS)
  Tap → MainActivity.onCreate() reads intent data
     → NavController.navigate("apple/{apple_id}")

App killed:
  OS wakes app on FCM message delivery
  Same deep-link flow as backgrounded
```

---

## Build Variants

| Variant | IDUNA URL | FCM project | Notes |
|---------|-----------|-------------|-------|
| debug | http://10.0.2.2:8090 (emulator) / http://192.168.x.x:8090 (device) | dev project | cleartext allowed |
| release | https://iduna.einhorn.industrial | prod project | TLS required |

---

## Acceptance Criteria for Milestone 0

- [ ] App builds and runs on Android 8.0+ emulator
- [ ] IDUNA client authenticates with Google Sign-In and stores JWT
- [ ] Apple feed loads from IDUNA and displays in reverse-chron order
- [ ] FCM token registration completes on first launch
- [ ] Test notification received and shows in system tray

---

*MJOLNIR SPEC | Build 0001 | EINHORN_INDUSTRIAL | 2026-06-09*
