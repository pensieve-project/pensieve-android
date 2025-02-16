import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.login(LoginRequest(email, password))
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.register(RegistrationRequest(email, username, password))
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}