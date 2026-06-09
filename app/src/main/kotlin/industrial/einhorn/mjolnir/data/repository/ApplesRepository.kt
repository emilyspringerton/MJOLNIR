package industrial.einhorn.mjolnir.data.repository

import industrial.einhorn.mjolnir.data.model.Apple
import industrial.einhorn.mjolnir.data.remote.IdunaApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplesRepository @Inject constructor(
    private val api: IdunaApi
) {
    private val _apples = MutableStateFlow<List<Apple>>(emptyList())
    val apples: Flow<List<Apple>> = _apples.asStateFlow()

    suspend fun refresh(sourceRepo: String? = null): Result<Unit> = runCatching {
        val response = api.listApples(limit = 50, sourceRepo = sourceRepo)
        _apples.value = response.apples
    }

    suspend fun getApple(id: Long): Result<Apple> = runCatching {
        api.getApple(id)
    }
}
