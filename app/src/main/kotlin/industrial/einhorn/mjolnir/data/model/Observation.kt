package industrial.einhorn.mjolnir.data.model

import com.google.gson.annotations.SerializedName

data class Observation(
    val id: Long,
    @SerializedName("agent_name") val agentName: String,
    @SerializedName("media_type") val mediaType: String,
    val prompt: String?,
    val status: String,          // pending | processing | done | error
    val analysis: String?,
    @SerializedName("analysis_preview") val analysisPreview: String?,
    @SerializedName("apple_id") val appleId: Long?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("processed_at") val processedAt: String?,
)

data class ObservationRequest(
    @SerializedName("image_data") val imageData: String,
    @SerializedName("media_type") val mediaType: String = "image/jpeg",
    val prompt: String? = null,
)

data class ObservationResponse(
    val id: Long,
    @SerializedName("agent_name") val agentName: String,
    val status: String,
)

data class ObservationsResponse(
    val observations: List<Observation>,
)
