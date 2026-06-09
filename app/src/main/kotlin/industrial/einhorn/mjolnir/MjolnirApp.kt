package industrial.einhorn.mjolnir

import android.app.Application
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import industrial.einhorn.mjolnir.data.local.ApplesGitSyncWorker
import industrial.einhorn.mjolnir.notification.NotificationChannels
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MjolnirApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.create(this)
        scheduleApplesSync()
    }

    private fun scheduleApplesSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
            .build()
        val request = PeriodicWorkRequestBuilder<ApplesGitSyncWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(30, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "apples-git-sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
