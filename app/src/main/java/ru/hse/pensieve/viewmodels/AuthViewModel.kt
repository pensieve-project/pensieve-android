package ru.hse.pensieve.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.models.AuthenticationResponse
import ru.hse.pensieve.models.LoginRequest
import ru.hse.pensieve.models.RegistrationRequest
import ru.hse.pensieve.repository.AuthRepository

class AuthViewModel: ViewModel() {
    private val authRepository = AuthRepository()

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

    fun register(email: String, username: String, password: String) {
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