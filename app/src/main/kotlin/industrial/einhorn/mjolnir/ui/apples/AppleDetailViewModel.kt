package industrial.einhorn.mjolnir.ui.apples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.model.Apple
import industrial.einhorn.mjolnir.data.repository.ApplesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppleDetailViewModel @Inject constructor(
    private val repo: ApplesRepository
) : ViewModel() {
    private val _apple = MutableStateFlow<Apple?>(null)
    val apple: StateFlow<Apple?> = _apple

    fun load(id: Long) {
        viewModelScope.launch {
            repo.getApple(id).onSuccess { _apple.value = it }
        }
    }
}
