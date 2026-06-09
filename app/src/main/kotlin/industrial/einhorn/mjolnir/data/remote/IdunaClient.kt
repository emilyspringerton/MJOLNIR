package industrial.einhorn.mjolnir.data.remote

import industrial.einhorn.mjolnir.data.model.Apple
import industrial.einhorn.mjolnir.data.model.ApplesResponse
import industrial.einhorn.mjolnir.data.model.DeviceTokenRequest
import industrial.einhorn.mjolnir.data.model.Observation
import industrial.einhorn.mjolnir.data.model.ObservationRequest
import industrial.einhorn.mjolnir.data.model.ObservationResponse
import industrial.einhorn.mjolnir.data.model.ObservationsResponse
import retrofit2.Response
import retrofit2.http.*

interface IdunaApi {

    @GET("api/v1/apples")
    suspend fun listApples(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("source_repo") sourceRepo: String? = null
    ): ApplesResponse

    @GET("api/v1/apples/{id}")
    suspend fun getApple(@Path("id") id: Long): Apple

    @POST("api/v1/push-tokens")
    suspend fun registerPushToken(@Body request: DeviceTokenRequest): Response<Unit>

    @POST("api/v1/auth/agent")
    suspend fun authenticateAgent(@Body body: AgentAuthRequest): AgentAuthResponse

    @POST("api/v1/auth/google")
    suspend fun authenticateGoogle(@Body body: GoogleAuthRequest): GoogleAuthResponse

    // Intelligence / camera observations
    @POST("api/v1/intelligence/observe")
    suspend fun submitObservation(@Body request: ObservationRequest): Response<ObservationResponse>

    @GET("api/v1/intelligence/observations")
    suspend fun listObservations(
        @Query("limit") limit: Int = 20,
        @Query("status") status: String? = null,
    ): ObservationsResponse

    @GET("api/v1/intelligence/observations/{id}")
    suspend fun getObservation(@Path("id") id: Long): Observation
}

data class AgentAuthRequest(
    @com.google.gson.annotations.SerializedName("agent_name") val agentName: String,
    val secret: String
)

data class AgentAuthResponse(
    val token: String,
    @com.google.gson.annotations.SerializedName("expires_in") val expiresIn: Int
)

data class GoogleAuthRequest(
    @com.google.gson.annotations.SerializedName("id_token") val idToken: String
)

data class GoogleAuthResponse(
    val token: String,
    @com.google.gson.annotations.SerializedName("expires_in") val expiresIn: Int
)
