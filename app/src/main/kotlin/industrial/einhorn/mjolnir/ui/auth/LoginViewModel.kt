package industrial.einhorn.mjolnir.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.remote.FcmTokenManager
import industrial.einhorn.mjolnir.data.remote.GoogleAuthRequest
import industrial.einhorn.mjolnir.data.remote.IdunaApi
import industrial.einhorn.mjolnir.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: IdunaApi,
    private val authRepo: AuthRepository,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun signInWithGoogle() {
        // The Google Sign-In flow requires an Activity context and must be
        // triggered via the Launcher API from the Composable. The ViewModel
        // receives the resulting ID token via exchangeGoogleToken().
        _state.value = LoginState.Loading
    }

    fun exchangeGoogleToken(idToken: String) {
        viewModelScope.launch {
            try {
                val response = api.authenticateGoogle(GoogleAuthRequest(idToken))
                authRepo.saveToken(response.token)
                fcmTokenManager.registerToken()
                _state.value = LoginState.Success
            } catch (e: Exception) {
                _state.value = LoginState.Error("Sign-in failed: ${e.message}")
            }
        }
    }
}
