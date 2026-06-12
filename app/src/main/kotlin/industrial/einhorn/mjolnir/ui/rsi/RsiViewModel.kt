package industrial.einhorn.mjolnir.ui.rsi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.model.CycleState
import industrial.einhorn.mjolnir.data.remote.EmilyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RsiUiState(
    val cycleState: CycleState? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class RsiViewModel @Inject constructor(
    private val emilyApi: EmilyApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RsiUiState(isLoading = true))
    val uiState: StateFlow<RsiUiState> = _uiState

    init { load() }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val state = emilyApi.getCycleState()
                _uiState.value = RsiUiState(cycleState = state, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Could not reach Emily Prime"
                )
            }
        }
    }
}
