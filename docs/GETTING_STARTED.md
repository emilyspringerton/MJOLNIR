# MJOLNIR — Getting Started

Emily Springerton's intelligence terminal. Apples feed, RSI cycle state, HEIMDAL sprint
submissions, and push notifications from Emily Prime.

Requires Android 8.0 or newer.

---

## Step 1 — Add the secret to GitHub

The app uses Firebase for push notifications. Before the first build will succeed, add the
Firebase config to the repo:

1. Go to **github.com/emilyspringerton/MJOLNIR → Settings → Secrets and variables → Actions**
2. Click **New repository secret**
3. Name: `GOOGLE_SERVICES_JSON`
4. Value: paste the full contents of `app/google-services.json` (the real one, not the example)
5. Save

The workflow runs automatically on every push to `main`. After you push, the build takes
about 5–8 minutes.

---

## Step 2 — Download the APK

1. Go to **github.com/emilyspringerton/MJOLNIR → Actions**
2. Click the latest green **Build** run
3. Scroll to the bottom — **Artifacts** section
4. Click **mjolnir-\<sha\>** to download the zip
5. Unzip it — you'll find `app-staging.apk` inside

---

## Step 3 — Install on your phone

Android blocks installs from outside the Play Store by default. One-time setup:

**Android 8+:**
1. Transfer `app-staging.apk` to your phone (AirDrop/Google Drive/USB cable)
2. Tap the file in Files — you'll be prompted to allow installs from that source
3. Tap **Allow from this source**, then **Install**

**If you get "Install blocked":**
- Settings → Apps → Special app access → Install unknown apps
- Find the app you used to open the file (Files, Chrome, etc.) → Allow

---

## Step 4 — Sign in

1. Open **MJOLNIR**
2. Tap **Sign in with Google**
3. Choose **emilyspringerton@gmail.com**

That's it. The app authenticates against IDUNA (`iduna.farthq.com`) using your Google
identity token. No separate password.

On first launch the app registers your device for push notifications. Emily Prime will
send critical alerts to this device automatically.

---

## What you'll see

| Screen | What it shows |
|---|---|
| **Apples** | Live audit trail — every meaningful event Emily Prime files |
| **RSI** | Current cycle phase, gear state (ACTIVE/COAST/REST), 7-day token spend |
| **HEIMDAL** | Active sprint submissions — submit new product requirements from here |
| **Intelligence** | FatBaby signal observations (financial intelligence) |
| **Chat** | Emily Prime chat (requires emily-agent to be publicly accessible) |

---

## Updating

Every push to `main` produces a new APK. Repeat steps 2–3 to install the update.
Android will upgrade in place — no need to uninstall first.

---

## Troubleshooting

**"Sign-in failed"** — IDUNA may be down. Check `https://iduna.farthq.com` is reachable.

**No push notifications** — Token registration runs once on first sign-in. If you signed in
before the FCM config was wired, sign out (clear app data) and sign in again.

**Chat screen errors** — Emily Prime is not publicly exposed. Chat requires a local or VPN
connection to port 8086. All other screens work without it.
