# MJOLNIR

Android intelligence terminal for Emily Springerton / EINHORN_INDUSTRIAL.

Push notifications from Emily Prime. Apple feed from IDUNA. Front door to the products.

## Installing a build on your Android tablet

**⚠️ Blocked right now**: every CI run to date (4 for 4, most recently run #4 on 2026-08-04) has
failed at a step called **"Validate google-services.json"**, which means **no installable APK
exists yet** — there is nothing to download until this is fixed, and it needs a real action from
you specifically, not something fixable by editing code:

1. Go to **github.com/emilyspringerton/MJOLNIR → Settings → Secrets and variables → Actions**.
2. Click **New repository secret**, name it `GOOGLE_SERVICES_JSON`.
3. Paste the **full contents** of your real Firebase project's `google-services.json` file as the
   value (from the Firebase console → Project settings → your Android app → download
   `google-services.json`). The placeholder/example file in this repo will NOT work — CI checks
   for a real numeric `project_number` and fails on purpose if it's missing.
4. Save, then push any commit (or re-run the workflow from the Actions tab) to trigger a fresh
   build. It takes about 5–8 minutes.

**Once a green build exists**, from your tablet's own browser (no computer needed):

1. Open `github.com/emilyspringerton/MJOLNIR/actions/workflows/build.yml`, signed in to GitHub.
2. Tap the latest run with a green check (not a red X).
3. Scroll to **Artifacts**, tap `mjolnir-<sha>` to download the `.zip`.
4. Unzip it (Files app → tap the zip → Extract) — this reveals `app-staging.apk`.
5. Tap the `.apk`. Android will prompt to allow installs from that source the first time
   (**Settings → Apps → Special app access → Install unknown apps**, enable it for your browser
   or file manager) — a normal one-time Android prompt, not specific to this app.
6. Confirm install. Full walkthrough, sign-in, and what each screen shows:
   [Getting Started](docs/GETTING_STARTED.md).

## Docs

- [Getting Started](docs/GETTING_STARTED.md) — the fuller version of the install guide above
- [Northstar](docs/NORTHSTAR.md) — intent, architecture, milestones
- [Spec](docs/SPEC.md) — technical stack, module structure, auth flow
- [Push Notifications](docs/PUSH_NOTIFICATIONS.md) — FCM integration, Emily Prime sender
- [APPLES Integration](docs/APPLES_INTEGRATION.md) — live feed + offline git cache

## Status

**Milestone 4 — RSI Observability** (current)  
Milestones 0–3 complete. 34 Kotlin source files. Features: Apple feed, push notifications,
WebView products (FatBaby newssite, SignalAPI, TYLER episodes, SHANKPIT ping), HEIMDAL sprint UI,
camera intelligence, offline APPLES source browser. Pending: RSI loop state display + token sparkline.

## Depends on

- [IDUNA](../IDUNA) — auth + Apple storage + device token registry
- [EMILY](../EMILY) — push notification trigger (Emily Prime FCM sender)
- [APPLES](../APPLES) — offline Apple cache (git sync)
- [PRRJECT_FATBABY](../PRRJECT_FATBABY) — newssite + signal API (WebView targets)
