package industrial.einhorn.mjolnir.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import industrial.einhorn.mjolnir.MainActivity
import industrial.einhorn.mjolnir.R

class MjolnirMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val appleId = message.data["apple_id"]
        val sourceRepo = message.data["source_repo"]
        val severity = message.data["severity"] ?: "normal"

        val title = message.notification?.title ?: "Emily Prime"
        val body = message.notification?.body ?: return

        val channelId = when (severity) {
            "critical" -> NotificationChannels.CHANNEL_CRITICAL
            "high" -> NotificationChannels.CHANNEL_HIGH
            else -> NotificationChannels.CHANNEL_NORMAL
        }

        // Deep link intent — opens Apple detail if apple_id present, else feed
        val deepLink = if (appleId != null) "mjolnir://apple/$appleId" else "mjolnir://feed"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink), this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, appleId?.hashCode() ?: 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(
                when (channelId) {
                    NotificationChannels.CHANNEL_CRITICAL -> NotificationCompat.PRIORITY_MAX
                    NotificationChannels.CHANNEL_HIGH -> NotificationCompat.PRIORITY_HIGH
                    else -> NotificationCompat.PRIORITY_DEFAULT
                }
            )
            .build()

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        // Token refreshed — re-register with IDUNA.
        // FcmTokenManager is not injectable in a Service without HiltWorker; use a WorkManager task.
        android.util.Log.i("MjolnirFCM", "FCM token refreshed — schedule re-registration")
    }
}
