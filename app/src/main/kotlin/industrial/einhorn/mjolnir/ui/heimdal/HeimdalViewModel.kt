package industrial.einhorn.mjolnir.ui.heimdal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.repository.HeimdalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SubmitSprintState {
    object Idle : SubmitSprintState()
    object Loading : SubmitSprintState()
    data class Success(val id: Long) : SubmitSprintState()
    data class Error(val message: String) : SubmitSprintState()
}

@HiltViewModel
class HeimdalViewModel @Inject constructor(
    private val repository: HeimdalRepository,
) : ViewModel() {

    val sprints = repository.sprints

    private val _submitState = MutableStateFlow<SubmitSprintState>(SubmitSprintState.Idle)
    val submitState: StateFlow<SubmitSprintState> = _submitState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        refresh()
    }

    fun submitRequirement(requirement: String) {
        if (requirement.isBlank()) return
        viewModelScope.launch {
            _submitState.value = SubmitSprintState.Loading
            repository.submitRequirement(requirement)
                .onSuccess { id ->
                    _submitState.value = SubmitSprintState.Success(id)
                    refresh()
                }
                .onFailure { e ->
                    _submitState.value = SubmitSprintState.Error(e.message ?: "submit failed")
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refresh()
            _isRefreshing.value = false
        }
    }

    fun resetSubmit() {
        _submitState.value = SubmitSprintState.Idle
    }
}
