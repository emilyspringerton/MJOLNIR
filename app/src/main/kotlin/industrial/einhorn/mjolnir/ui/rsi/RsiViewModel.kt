package industrial.einhorn.mjolnir.ui.rsi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.model.CycleState
import industrial.einhorn.mjolnir.data.model.DailyTokenStat
import industrial.einhorn.mjolnir.data.remote.EmilyApi
import industrial.einhorn.mjolnir.data.remote.IdunaApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RsiUiState(
    val cycleState: CycleState? = null,
    val tokenStats: List<DailyTokenStat> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class RsiViewModel @Inject constructor(
    private val emilyApi: EmilyApi,
    private val idunaApi: IdunaApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RsiUiState(isLoading = true))
    val uiState: StateFlow<RsiUiState> = _uiState

    init { load() }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val cycleDeferred = async { runCatching { emilyApi.getCycleState() } }
            val statsDeferred = async { runCatching { idunaApi.getDailyTokenStats(days = 7) } }

            val cycleResult = cycleDeferred.await()
            val statsResult = statsDeferred.await()

            _uiState.value = RsiUiState(
                cycleState = cycleResult.getOrNull(),
                tokenStats = statsResult.getOrNull()?.stats ?: emptyList(),
                isLoading = false,
                error = if (cycleResult.isFailure) cycleResult.exceptionOrNull()?.message else null,
            )
        }
    }
}
