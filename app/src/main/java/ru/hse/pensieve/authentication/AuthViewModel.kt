package ru.hse.pensieve.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.authentication.models.AuthenticationResponse
import ru.hse.pensieve.authentication.models.LoginRequest
import ru.hse.pensieve.authentication.models.RegistrationRequest
import ru.hse.pensieve.authentication.repository.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)

    private val _user = MutableLiveData<AuthenticationResponse>()
    val user: LiveData<AuthenticationResponse> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.login(LoginRequest(email, password))
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.register(RegistrationRequest(username, email, password))
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            }
        }
    }
}