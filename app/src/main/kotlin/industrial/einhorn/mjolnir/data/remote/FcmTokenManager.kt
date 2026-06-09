package industrial.einhorn.mjolnir.data.remote

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import industrial.einhorn.mjolnir.BuildConfig
import industrial.einhorn.mjolnir.data.model.DeviceTokenRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(
    private val api: IdunaApi
) {
    suspend fun registerToken() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            val fingerprint = android.os.Build.FINGERPRINT
                .let { java.security.MessageDigest.getInstance("SHA-256")
                    .digest(it.toByteArray())
                    .joinToString("") { b -> "%02x".format(b) }
                }
            api.registerPushToken(
                DeviceTokenRequest(
                    agentName = BuildConfig.MJOLNIR_AGENT_NAME,
                    platform = "android",
                    fcmToken = token,
                    fingerprint = fingerprint
                )
            )
            Log.i("FcmTokenManager", "FCM token registered with IDUNA")
        } catch (e: Exception) {
            Log.w("FcmTokenManager", "FCM token registration failed (non-fatal): ${e.message}")
        }
    }
}
