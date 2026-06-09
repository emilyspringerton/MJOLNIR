# MJOLNIR Push Notifications — Integration Spec
## FCM v1 API + Emily Prime sender + IDUNA device token registry

**Date**: 2026-06-09  
**Status**: Spec — implementation pending Milestone 1

---

## Overview

Emily Prime sends push notifications to Emily Springerton's Android phone via
Firebase Cloud Messaging (FCM) HTTP v1 API. The device FCM token is registered
in IDUNA on first app launch. Emily Prime resolves it from IDUNA when dispatching.

---

## FCM Setup

### 1. Firebase Project
- Project name: `einhorn-mjolnir`
- Package name: `industrial.einhorn.mjolnir`
- Service account JSON: stored in IDUNA secrets store as `fcm_service_account`
- FCM sender ID: in `app/google-services.json` (not committed — pulled from IDUNA at build time)

### 2. Android App Registration
On first launch after login:
```kotlin
FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
    idunaClient.registerDeviceToken(
        DeviceTokenRequest(
            agentName = "mjolnir-emily",
            platform = "android",
            token = token
        )
    )
}
```

IDUNA endpoint: `POST /api/v1/devices/register`
```json
{
  "agent_name": "mjolnir-emily",
  "platform": "android",
  "fcm_token": "<token>",
  "device_fingerprint": "<sha256 of Build.FINGERPRINT>"
}
```

IDUNA stores this in a `device_tokens` table alongside the agent record.

---

## IDUNA Schema Addition

```sql
CREATE TABLE device_tokens (
    id          TEXT PRIMARY KEY,
    agent_name  TEXT NOT NULL REFERENCES agents(name),
    platform    TEXT NOT NULL DEFAULT 'android',
    fcm_token   TEXT NOT NULL,
    fingerprint TEXT,
    registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at  DATETIME
);
```

IDUNA API additions:
- `POST /api/v1/devices/register` — upsert token by agent_name + fingerprint
- `GET  /api/v1/devices/{agent_name}/token` — returns current FCM token (M2M auth required)

---

## Emily Prime FCM Sender (Go)

New package: `EMILY/pkg/fcm/sender.go`

```go
package fcm

import (
    "context"
    "encoding/json"
    "fmt"
    "net/http"

    "golang.org/x/oauth2/google"
)

type Sender struct {
    projectID      string
    serviceAccount []byte // JSON from IDUNA secrets
    httpClient     *http.Client
}

type Message struct {
    Title    string
    Body     string
    Data     map[string]string
    Priority string // "high" | "normal"
}

func (s *Sender) Send(ctx context.Context, deviceToken string, msg Message) error {
    token, err := s.getOAuthToken(ctx)
    if err != nil {
        return fmt.Errorf("fcm oauth: %w", err)
    }
    payload := map[string]any{
        "message": map[string]any{
            "token": deviceToken,
            "notification": map[string]string{
                "title": msg.Title,
                "body":  msg.Body,
            },
            "data": msg.Data,
            "android": map[string]any{
                "priority": msg.Priority,
                "notification": map[string]string{
                    "channel_id": channelID(msg.Priority),
                    "click_action": "OPEN_APPLE_DETAIL",
                },
            },
        },
    }
    // POST to https://fcm.googleapis.com/v1/projects/{projectID}/messages:send
    _ = payload
    _ = token
    return nil // TODO: implement HTTP call
}

func channelID(priority string) string {
    switch priority {
    case "critical":
        return "MJOLNIR_CRITICAL"
    case "high":
        return "MJOLNIR_HIGH"
    default:
        return "MJOLNIR_NORMAL"
    }
}
```

### Emily Prime dispatch triggers

Emily Prime fires a push when:
- An Apple is posted with `severity == "critical"` or `requires_ceo_visibility == true`
- RSI loop completes with `net_improvement > 0` (token savings confirmed)
- Morning brief (09:00 daily cron) — summary of overnight Apples
- IDUNA auth failure on any agent (security alert)

Wire in `EMILY/emily-agent/cron.go` after Apple submission:
```go
if apple.Severity == "critical" || apple.RequiresCEOVisibility {
    if err := fcmSender.Send(ctx, deviceToken, fcm.Message{
        Title:    fmt.Sprintf("[%s] %s", apple.Type, apple.SourceRepo),
        Body:     truncate(apple.Title, 140),
        Priority: "high",
        Data: map[string]string{
            "apple_id":    apple.ID,
            "source_repo": apple.SourceRepo,
        },
    }); err != nil {
        log.Printf("fcm: send failed: %v", err)
    }
}
```

---

## Android Notification Channels

Declared in `MainActivity.kt` on app start:

| Channel ID         | Name             | Importance | Sound | DND bypass |
|--------------------|------------------|------------|-------|------------|
| MJOLNIR_CRITICAL   | Critical Alerts  | URGENT     | yes   | yes        |
| MJOLNIR_HIGH       | High Priority    | HIGH       | yes   | no         |
| MJOLNIR_NORMAL     | Activity         | DEFAULT    | no    | no         |

---

## Deep Link Schema

Notification tap opens:
```
mjolnir://apple/{apple_id}     → Apple detail screen
mjolnir://feed                 → Apple feed (default)
mjolnir://rsi                  → RSI loop status screen
mjolnir://product/{name}       → WebView product panel
```

Declared in `AndroidManifest.xml`:
```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <data android:scheme="mjolnir"/>
</intent-filter>
```

---

*MJOLNIR PUSH NOTIFICATIONS SPEC | 2026-06-09*
