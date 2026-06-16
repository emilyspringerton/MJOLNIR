package industrial.einhorn.mjolnir.data.repository

import industrial.einhorn.mjolnir.data.model.EmilyChatRequest
import industrial.einhorn.mjolnir.data.model.FatBabyChatRequest
import industrial.einhorn.mjolnir.data.remote.EmilyApi
import industrial.einhorn.mjolnir.data.remote.FatBabyApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val emilyApi: EmilyApi,
    private val fatBabyApi: FatBabyApi
) {
    suspend fun sendToEmily(sessionId: String, message: String): String =
        emilyApi.chat(EmilyChatRequest(session_id = sessionId, message = message)).reply

    suspend fun sendToFatBaby(sessionId: String, question: String): String =
        fatBabyApi.ask(FatBabyChatRequest(question = question, session_id = sessionId)).answer
}
