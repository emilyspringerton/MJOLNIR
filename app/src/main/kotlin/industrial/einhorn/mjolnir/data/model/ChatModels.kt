package industrial.einhorn.mjolnir.data.model

data class ChatMessage(
    val role: String,
    val content: String,
    val timestampMs: Long = System.currentTimeMillis()
)

enum class ChatMode { EMILY_PRIME, FATBABY_EMILY }

data class EmilyChatRequest(
    val session_id: String,
    val message: String
)

data class EmilyChatResponse(
    val reply: String,
    val turn_id: String? = null
)

data class FatBabyChatRequest(
    val question: String,
    val session_id: String? = null
)

data class FatBabyChatResponse(
    val answer: String
)
