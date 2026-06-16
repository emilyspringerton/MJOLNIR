package industrial.einhorn.mjolnir.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.model.ChatMessage
import industrial.einhorn.mjolnir.data.model.ChatMode
import industrial.einhorn.mjolnir.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mode: ChatMode = ChatMode.valueOf(
        savedStateHandle.get<String>("mode") ?: ChatMode.EMILY_PRIME.name
    )

    private val sessionId = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _uiState.value.isLoading) return

        _uiState.update {
            it.copy(
                messages = it.messages + ChatMessage("user", trimmed),
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            runCatching {
                when (mode) {
                    ChatMode.EMILY_PRIME -> repo.sendToEmily(sessionId, trimmed)
                    ChatMode.FATBABY_EMILY -> repo.sendToFatBaby(sessionId, trimmed)
                }
            }.fold(
                onSuccess = { reply ->
                    _uiState.update {
                        it.copy(
                            messages = it.messages + ChatMessage("assistant", reply),
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Request failed")
                    }
                }
            )
        }
    }
}
