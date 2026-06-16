package industrial.einhorn.mjolnir.data.remote

import industrial.einhorn.mjolnir.data.model.CycleState
import industrial.einhorn.mjolnir.data.model.EmilyChatRequest
import industrial.einhorn.mjolnir.data.model.EmilyChatResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EmilyApi {
    @GET("api/v1/emily/state")
    suspend fun getCycleState(): CycleState

    @POST("chat")
    suspend fun chat(@Body request: EmilyChatRequest): EmilyChatResponse
}
