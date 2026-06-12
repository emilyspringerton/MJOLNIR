package industrial.einhorn.mjolnir.data.remote

import industrial.einhorn.mjolnir.data.model.CycleState
import retrofit2.http.GET

interface EmilyApi {
    @GET("api/v1/emily/state")
    suspend fun getCycleState(): CycleState
}
