# MJOLNIR Changelog

## 2026-06-16

- Emily Prime chat + FatBaby Emily chat: two ChatScreen/ChatViewModel screens wired into navigation (chat/{mode}); ChatRepository, FatBabyApi, FATBABY_BASE_URL; Apple #905


## 2026-06-14
- feat(rsi): TokenSparklineCard — 7-day token spend bar chart in RsiScreen; Canvas-based, no chart library dep; zero-pads days with no activity
- feat(rsi): RsiViewModel now fetches IDUNA /api/v1/apples/stats/daily-tokens in parallel with Emily cycle state; tokenStats added to RsiUiState
- feat(api): IdunaApi.getDailyTokenStats() — GET api/v1/apples/stats/daily-tokens?days=7
- feat(model): TokenStats.kt — DailyTokenStat + DailyTokenStatsResponse data classes
- docs(northstar): Milestone 4 marked complete; token spend sparkline checked off
