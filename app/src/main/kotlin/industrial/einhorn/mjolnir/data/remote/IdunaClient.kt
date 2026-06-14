package industrial.einhorn.mjolnir.data.remote

import industrial.einhorn.mjolnir.data.model.Apple
import industrial.einhorn.mjolnir.data.model.ApplesResponse
import industrial.einhorn.mjolnir.data.model.DailyTokenStatsResponse
import industrial.einhorn.mjolnir.data.model.DeviceTokenRequest
import industrial.einhorn.mjolnir.data.model.Observation
import industrial.einhorn.mjolnir.data.model.ObservationRequest
import industrial.einhorn.mjolnir.data.model.ObservationResponse
import industrial.einhorn.mjolnir.data.model.ObservationsResponse
import industrial.einhorn.mjolnir.data.model.SprintItem
import industrial.einhorn.mjolnir.data.model.SprintSubmitRequest
import industrial.einhorn.mjolnir.data.model.SprintSubmitResponse
import industrial.einhorn.mjolnir.data.model.SprintsResponse
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

    // HEIMDAL sprint planning
    @POST("api/v1/heimdal/sprints")
    suspend fun submitSprint(@Body request: SprintSubmitRequest): Response<SprintSubmitResponse>

    @GET("api/v1/heimdal/sprints")
    suspend fun listSprints(
        @Query("limit") limit: Int = 50,
        @Query("status") status: String? = null,
    ): SprintsResponse

    @GET("api/v1/heimdal/sprints/{id}")
    suspend fun getSprint(@Path("id") id: Long): SprintItem

    // Token spend sparkline — IDUNA /api/v1/apples/stats/daily-tokens
    @GET("api/v1/apples/stats/daily-tokens")
    suspend fun getDailyTokenStats(
        @Query("days") days: Int = 7,
    ): DailyTokenStatsResponse
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
