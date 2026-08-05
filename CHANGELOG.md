# MJOLNIR Changelog

## 2026-08-05
- README: expanded Step 1 into a full Firebase-project + google-services.json secret walkthrough (package name, download, paste); refreshed stale CI run count (5->7) (sess-20260723-2347-df115bd5)

- docs(readme): clearer step 1/step 2 split for the install-blocker walkthrough; refreshed CI run count (still blocked on GOOGLE_SERVICES_JSON secret, run #5 failing) (sess-20260723-2347-df115bd5)


## 2026-08-04

- ci(build): fail fast with an actionable error when `google-services.json` is invalid. Founder
  asked how to install MJOLNIR on an Android tablet from GitHub Actions artifacts — found both
  real CI runs to date (2026-06-17, 2026-07-23) had failed at "Build staging APK" with no artifact
  ever produced, most likely because the `GOOGLE_SERVICES_JSON` repo secret was never set. New
  validation step catches this before Gradle runs instead of a deep, unhelpful stack trace.
  README now links `docs/GETTING_STARTED.md` from the docs index for device install instructions.

## 2026-06-17

- feat(ci): GitHub Actions workflow — builds staging APK on every push to main; uploads artifact; CEO can download and sideload
- feat(build): staging build type pointing to iduna.farthq.com (live server); initWith(debug) so it's debug-signed and sideloadable
- docs: GETTING_STARTED.md — step-by-step CEO install guide (secret setup, APK download, sideload, Google sign-in)

## 2026-06-16

- Emily Prime chat + FatBaby Emily chat: two ChatScreen/ChatViewModel screens wired into navigation (chat/{mode}); ChatRepository, FatBabyApi, FATBABY_BASE_URL; Apple #905


## 2026-06-14
- feat(rsi): TokenSparklineCard — 7-day token spend bar chart in RsiScreen; Canvas-based, no chart library dep; zero-pads days with no activity
- feat(rsi): RsiViewModel now fetches IDUNA /api/v1/apples/stats/daily-tokens in parallel with Emily cycle state; tokenStats added to RsiUiState
- feat(api): IdunaApi.getDailyTokenStats() — GET api/v1/apples/stats/daily-tokens?days=7
- feat(model): TokenStats.kt — DailyTokenStat + DailyTokenStatsResponse data classes
- docs(northstar): Milestone 4 marked complete; token spend sparkline checked off
