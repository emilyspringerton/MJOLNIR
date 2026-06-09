package industrial.einhorn.mjolnir.ui.apples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.model.Apple
import industrial.einhorn.mjolnir.data.repository.ApplesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplesFeedViewModel @Inject constructor(
    private val repo: ApplesRepository
) : ViewModel() {

    val apples: StateFlow<List<Apple>> = repo.apples as StateFlow<List<Apple>>
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.refresh()
            _isLoading.value = false
        }
    }
}
