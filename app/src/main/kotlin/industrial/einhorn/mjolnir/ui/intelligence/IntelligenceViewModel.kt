package industrial.einhorn.mjolnir.ui.intelligence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.model.Observation
import industrial.einhorn.mjolnir.data.repository.IntelligenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SubmitState {
    object Idle : SubmitState()
    object Loading : SubmitState()
    data class Success(val observationId: Long) : SubmitState()
    data class Error(val message: String) : SubmitState()
}

@HiltViewModel
class IntelligenceViewModel @Inject constructor(
    private val repo: IntelligenceRepository,
) : ViewModel() {

    val observations: StateFlow<List<Observation>> = repo.observations

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repo.refresh() }
    }

    fun submitImage(jpegBytes: ByteArray, prompt: String?) {
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            repo.submitImage(jpegBytes, prompt)
                .onSuccess { id ->
                    _submitState.value = SubmitState.Success(id)
                    repo.refresh()
                }
                .onFailure { e ->
                    _submitState.value = SubmitState.Error(e.message ?: "unknown error")
                }
        }
    }

    fun resetSubmit() { _submitState.value = SubmitState.Idle }

    // Exposed for detail screen
    val repository: IntelligenceRepository get() = repo
}
