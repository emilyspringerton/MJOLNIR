package industrial.einhorn.mjolnir.data.model

import com.google.gson.annotations.SerializedName

data class SprintItem(
    val id: Long,
    @SerializedName("agent_name") val agentName: String,
    @SerializedName("requirement_preview") val requirementPreview: String,
    val requirement: String?,
    @SerializedName("roadmap_id") val roadmapId: String,
    val status: String,       // pending | queued | in_progress | complete | blocked
    @SerializedName("apple_id") val appleId: Long,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val criteria: List<SprintCriterion>?,
)

data class SprintCriterion(
    val name: String,
    val description: String,
    val target: String,
)

data class SprintSubmitRequest(
    val requirement: String,
)

data class SprintSubmitResponse(
    val id: Long,
    @SerializedName("agent_name") val agentName: String,
    val status: String,
)

data class SprintsResponse(
    val sprints: List<SprintItem>,
)
