package industrial.einhorn.mjolnir.data.remote

import industrial.einhorn.mjolnir.data.model.FatBabyChatRequest
import industrial.einhorn.mjolnir.data.model.FatBabyChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FatBabyApi {
    @POST("api/ask")
    suspend fun ask(@Body request: FatBabyChatRequest): FatBabyChatResponse
}
