package industrial.einhorn.mjolnir.data.repository

import android.util.Base64
import industrial.einhorn.mjolnir.data.model.Observation
import industrial.einhorn.mjolnir.data.model.ObservationRequest
import industrial.einhorn.mjolnir.data.remote.IdunaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntelligenceRepository @Inject constructor(private val api: IdunaApi) {

    private val _observations = MutableStateFlow<List<Observation>>(emptyList())
    val observations: StateFlow<List<Observation>> = _observations

    suspend fun submitImage(jpegBytes: ByteArray, prompt: String?): Result<Long> =
        withContext(Dispatchers.IO) {
            runCatching {
                val b64 = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
                val req = ObservationRequest(imageData = b64, mediaType = "image/jpeg", prompt = prompt?.ifBlank { null })
                val resp = api.submitObservation(req)
                if (!resp.isSuccessful) error("submit failed: ${resp.code()}")
                resp.body()?.id ?: error("no id in response")
            }
        }

    suspend fun refresh(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val resp = api.listObservations(limit = 30)
            _observations.value = resp.observations
        }
    }

    suspend fun getObservation(id: Long): Result<Observation> = withContext(Dispatchers.IO) {
        runCatching { api.getObservation(id) }
    }
}
