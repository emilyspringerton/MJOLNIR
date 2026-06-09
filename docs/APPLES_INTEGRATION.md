# MJOLNIR — APPLES Integration
## Live feed from IDUNA + offline cache from APPLES git repo

**Date**: 2026-06-09

---

## Two Sources of Apples

| Source | Transport | Latency | Offline | Auth |
|--------|-----------|---------|---------|------|
| IDUNA API | HTTPS REST | ~100ms | No | JWT |
| APPLES git repo | GitHub API / git clone | ~2s | Yes | GitHub token |

The app uses IDUNA as the primary live source and the APPLES repo as an offline cache.

---

## IDUNA Apple API

```
GET /api/v1/apples?limit=50&offset=0&source_repo=PRRJECT_FATBABY
Authorization: Bearer <jwt>

Response:
{
  "apples": [
    {
      "id": "apple-123",
      "number": 123,
      "type": "completion",
      "title": "BACKLOG: ✓ cross-scene attack guard",
      "source_repo": "SHANKPIT",
      "source_agent": "claude-code",
      "severity": "normal",
      "requires_ceo_visibility": false,
      "filed_at": "2026-06-09T14:23:00Z",
      "body": "..."
    }
  ],
  "total": 342,
  "next_offset": 50
}
```

Android client (`data/IdunaClient.kt`):
```kotlin
@GET("api/v1/apples")
suspend fun getApples(
    @Query("limit") limit: Int = 50,
    @Query("offset") offset: Int = 0,
    @Query("source_repo") sourceRepo: String? = null
): ApplesResponse
```

---

## APPLES Git Repo Structure

The `APPLES` repo (written by `emily sync --apples-git-dir`) has:
```
APPLES/
  20260607/
    apple-001_completion.json
    apple-002_rsi_iteration.json
  20260608/
    apple-037_completion.json
    ...
  20260609/
    apple-100_signal_observation.json
  docs/
    NORTHSTAR.md
    SCHEMA.md
  MANIFEST.json        ← index of all apples (date + id + type + title)
  README.md
```

`MANIFEST.json` (maintained by emily.cli sync) enables fast indexed reads without cloning all files:
```json
{
  "generated_at": "2026-06-09T22:00:00Z",
  "count": 342,
  "apples": [
    { "id": "apple-001", "number": 1, "type": "completion", "title": "...", "date": "20260607" }
  ]
}
```

---

## Android Offline Cache Strategy

1. App clones/pulls APPLES repo to app-private storage (`filesDir/apples-repo/`)
2. Reads `MANIFEST.json` for index display (fast, no network after first sync)
3. Opens individual `<date>/<id>_<type>.json` files on demand (tap to expand)
4. Syncs on WiFi only, background WorkManager task, daily at 02:00

```kotlin
class ApplesGitSyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repoDir = File(applicationContext.filesDir, "apples-repo")
        return try {
            if (repoDir.exists()) gitPull(repoDir) else gitClone(repoDir)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun gitPull(dir: File) {
        // JGit or shell exec: git -C dir pull --ff-only origin main
    }
    
    private suspend fun gitClone(dir: File) {
        // JGit: Git.cloneRepository().setURI(APPLES_REPO_URL).setDirectory(dir).call()
    }
}
```

---

## Apple Card UI (Compose)

```kotlin
@Composable
fun AppleCard(apple: Apple, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            TypeBadge(type = apple.type)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = apple.title, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    SourceChip(label = apple.sourceRepo)
                    if (apple.requiresCeoVisibility) CriticalChip()
                    TimeAgo(timestamp = apple.filedAt)
                }
            }
        }
    }
}
```

Type badge colors:
| Type | Color |
|------|-------|
| completion | Green |
| rsi_iteration | Blue |
| signal_observation | Amber |
| escalation | Red |
| status | Gray |

---

*MJOLNIR APPLES INTEGRATION | 2026-06-09*
