# MJOLNIR

Android intelligence terminal for Emily Springerton / EINHORN_INDUSTRIAL.

Push notifications from Emily Prime. Apple feed from IDUNA. Front door to the products.

## Docs

- [Northstar](docs/NORTHSTAR.md) — intent, architecture, milestones
- [Spec](docs/SPEC.md) — technical stack, module structure, auth flow
- [Push Notifications](docs/PUSH_NOTIFICATIONS.md) — FCM integration, Emily Prime sender
- [APPLES Integration](docs/APPLES_INTEGRATION.md) — live feed + offline git cache

## Status

**Milestone 0 — Foundation** (current)  
Android project skeleton in progress. IDUNA device token API pending.

## Depends on

- [IDUNA](../IDUNA) — auth + Apple storage + device token registry
- [EMILY](../EMILY) — push notification trigger (Emily Prime FCM sender)
- [APPLES](../APPLES) — offline Apple cache (git sync)
- [PRRJECT_FATBABY](../PRRJECT_FATBABY) — newssite + signal API (WebView targets)
