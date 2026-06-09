package industrial.einhorn.mjolnir.data.model

import com.google.gson.annotations.SerializedName

data class Apple(
    val id: Long,
    @SerializedName("agent_id") val agentId: String,
    @SerializedName("source_repo") val sourceRepo: String,
    @SerializedName("run_id") val runId: String,
    @SerializedName("apple_type") val appleType: String,
    val title: String,
    val body: String? = null,
    @SerializedName("recorded_at") val recordedAt: String
)

data class ApplesResponse(
    val apples: List<Apple>
)

data class DeviceTokenRequest(
    @SerializedName("agent_name") val agentName: String,
    val platform: String = "android",
    @SerializedName("fcm_token") val fcmToken: String,
    val fingerprint: String = ""
)
