package industrial.einhorn.mjolnir.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val CHANNEL_CRITICAL = "MJOLNIR_CRITICAL"
    const val CHANNEL_HIGH = "MJOLNIR_HIGH"
    const val CHANNEL_NORMAL = "MJOLNIR_NORMAL"

    fun create(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannels(listOf(
            NotificationChannel(CHANNEL_CRITICAL, "Critical Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Critical escalations requiring immediate CEO attention"
                enableVibration(true)
                setBypassDnd(true)
            },
            NotificationChannel(CHANNEL_HIGH, "High Priority", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Important signals and CEO-visible observations from Emily Prime"
                enableVibration(true)
            },
            NotificationChannel(CHANNEL_NORMAL, "Activity", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Routine Apple feed activity and RSI cycle updates"
            }
        ))
    }
}
