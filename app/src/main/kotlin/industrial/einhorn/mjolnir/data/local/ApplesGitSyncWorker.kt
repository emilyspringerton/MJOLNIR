package industrial.einhorn.mjolnir.data.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

// APPLES_REPO_URL — override via BuildConfig in a future iteration when
// the GitHub token is available. For now we skip clone if not set.
private const val APPLES_REPO_URL = "https://github.com/emilyspringerton/APPLES.git"

@HiltWorker
class ApplesGitSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repoDir = File(applicationContext.filesDir, "apples-repo")
        return try {
            if (repoDir.exists() && File(repoDir, ".git").exists()) {
                gitPull(repoDir)
            } else {
                gitClone(repoDir)
            }
            Result.success()
        } catch (e: Exception) {
            android.util.Log.w("ApplesGitSync", "sync failed (will retry): ${e.message}")
            Result.retry()
        }
    }

    private fun gitClone(dir: File) {
        dir.mkdirs()
        Git.cloneRepository()
            .setURI(APPLES_REPO_URL)
            .setDirectory(dir)
            .setDepth(1)        // shallow clone — faster, smaller
            .call()
            .close()
    }

    private fun gitPull(dir: File) {
        Git.open(dir).use { git ->
            git.pull()
                .setFastForward(org.eclipse.jgit.api.MergeCommand.FastForwardMode.FF_ONLY)
                .call()
        }
    }
}
