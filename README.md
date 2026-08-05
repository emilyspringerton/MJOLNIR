# MJOLNIR

Android intelligence terminal for Emily Springerton / EINHORN_INDUSTRIAL.

Push notifications from Emily Prime. Apple feed from IDUNA. Front door to the products.

## Installing a build on your Android tablet

**⚠️ Blocked right now**: every CI run to date (7 for 7, most recently run #7 on 2026-08-05) has
failed at a step called **"Validate google-services.json"**, which means **no installable APK
exists yet** — there is nothing to download until this is fixed, and it needs a real action from
you specifically, not something fixable by editing code. This is the one step in the whole
process nobody else can do — no Firebase project, no `google-services.json`, no way around it.

### Step 1 — one-time setup (only you can do this part)

**1a. Get a Firebase project with an Android app registered in it.** Skip to 1b if you already
have one.

1. Go to **console.firebase.google.com**, sign in with the Google account you want to own this
   project.
2. Click **Add project** (or **Create a project**). Name it whatever you like — e.g. `MJOLNIR` or
   `einhorn-mjolnir`. Google Analytics can be left off; MJOLNIR doesn't need it.
3. Once the project finishes provisioning, on the project overview page click the **Android icon**
   (`</>`-style app-platform buttons near the top) to register a new Android app.
4. For **Android package name**, enter exactly:
   ```
   industrial.einhorn.mjolnir
   ```
   This must match `applicationId` in `app/build.gradle.kts` character-for-character or the build
   will fail even with a valid file. Nickname/SHA-1 fields are optional — leave them blank, they're
   only needed for Dynamic Links or Firebase Auth, which MJOLNIR doesn't use (auth is IDUNA).
5. Click **Register app**. The next screen offers a `google-services.json` to download — click
   **Download google-services.json** and save it anywhere (Downloads folder is fine, it's about to
   get pasted, not committed).
6. You can click through/skip the remaining "Add Firebase SDK" steps shown in the console — that
   part is already done in this repo's Gradle files.

**1b. Put that file's contents into the GitHub secret.**

1. Open the downloaded `google-services.json` in any text editor and copy the **entire contents**
   (it's one JSON object — copy everything from the opening `{` to the closing `}`).
2. Go to **github.com/emilyspringerton/MJOLNIR → Settings → Secrets and variables → Actions**.
3. If a secret named `GOOGLE_SERVICES_JSON` already exists, click it → **Update**. Otherwise click
   **New repository secret** and name it exactly `GOOGLE_SERVICES_JSON`.
4. Paste the JSON you copied into the **Secret** value box — the whole file, not a snippet. The
   placeholder/example file in this repo will NOT work as a substitute — CI checks for a real
   numeric `project_number` inside the JSON and fails on purpose if that's missing or fake.
5. Click **Add secret** (or **Update secret**).
6. Trigger a fresh build: either push any commit, or go to the **Actions** tab → the `build.yml`
   workflow → **Run workflow**. It takes about 5–8 minutes. Watch for the **Validate
   google-services.json** step specifically — if it still fails, the pasted JSON is either
   incomplete or the package name in Firebase didn't match `industrial.einhorn.mjolnir` exactly.

### Step 2 — once that build goes green, from your tablet's own browser (no computer needed)

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
