package industrial.einhorn.mjolnir.data.repository

import industrial.einhorn.mjolnir.data.model.SprintItem
import industrial.einhorn.mjolnir.data.model.SprintSubmitRequest
import industrial.einhorn.mjolnir.data.remote.IdunaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeimdalRepository @Inject constructor(private val api: IdunaApi) {

    private val _sprints = MutableStateFlow<List<SprintItem>>(emptyList())
    val sprints: StateFlow<List<SprintItem>> = _sprints

    suspend fun submitRequirement(requirement: String): Result<Long> =
        withContext(Dispatchers.IO) {
            runCatching {
                val resp = api.submitSprint(SprintSubmitRequest(requirement = requirement))
                if (!resp.isSuccessful) error("submit failed: ${resp.code()}")
                resp.body()?.id ?: error("no id in response")
            }
        }

    suspend fun refresh(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val resp = api.listSprints(limit = 50)
            _sprints.value = resp.sprints
        }
    }

    suspend fun getSprint(id: Long): Result<SprintItem> =
        withContext(Dispatchers.IO) { runCatching { api.getSprint(id) } }
}
