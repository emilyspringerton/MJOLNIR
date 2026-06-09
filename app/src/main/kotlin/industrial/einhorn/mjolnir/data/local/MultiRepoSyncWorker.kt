package industrial.einhorn.mjolnir.data.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.TransportException
import java.io.File

/**
 * Clones or pulls all EINHORN source repos into filesDir/src-repos/.
 * Scheduled daily on WiFi; provides the offline source browser with current code.
 *
 * Repos synced:
 *  - EMILY   github.com/emilyspringerton/EMILY
 *  - TYLER   github.com/emilyspringerton/TYLER
 *  - IDUNA   github.com/emilyspringerton/IDUNA
 *  - MJOLNIR github.com/emilyspringerton/MJOLNIR
 *  - APPLES  github.com/emilyspringerton/APPLES
 */
@HiltWorker
class MultiRepoSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        val REPOS = listOf(
            "EMILY"   to "https://github.com/emilyspringerton/EMILY.git",
            "TYLER"   to "https://github.com/emilyspringerton/TYLER.git",
            "IDUNA"   to "https://github.com/emilyspringerton/IDUNA.git",
            "MJOLNIR" to "https://github.com/emilyspringerton/MJOLNIR.git",
            "APPLES"  to "https://github.com/emilyspringerton/APPLES.git",
        )
        const val WORK_NAME = "multi-repo-sync"
    }

    override suspend fun doWork(): Result {
        val baseDir = File(applicationContext.filesDir, "src-repos")
        baseDir.mkdirs()

        var anyFailure = false
        for ((name, url) in REPOS) {
            val repoDir = File(baseDir, name)
            try {
                if (File(repoDir, ".git").exists()) {
                    Git.open(repoDir).use { git ->
                        git.pull()
                            .setRemote("origin")
                            .setFastForward(org.eclipse.jgit.api.MergeCommand.FastForwardMode.FF_ONLY)
                            .call()
                    }
                } else {
                    repoDir.mkdirs()
                    Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(repoDir)
                        .setDepth(1)
                        .setCloneAllBranches(false)
                        .call()
                        .close()
                }
            } catch (e: TransportException) {
                // Network unavailable or auth required — best-effort, continue
                anyFailure = true
            } catch (e: Exception) {
                anyFailure = true
            }
        }
        return if (anyFailure) Result.retry() else Result.success()
    }
}
