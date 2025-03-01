package ru.hse.pensieve.repository

import ru.hse.pensieve.api.AuthApiService
import ru.hse.pensieve.models.AuthenticationResponse
import ru.hse.pensieve.models.LoginRequest
import ru.hse.pensieve.models.RegistrationRequest
import ru.hse.pensieve.api.Client

class AuthRepository {
    private val authApi = Client.getInstanceOfService(AuthApiService::class.java)

    suspend fun login(request: LoginRequest): AuthenticationResponse {
        return authApi.login(request).await()
    }

    suspend fun register(request: RegistrationRequest): AuthenticationResponse {
        return authApi.register(request).await()
    }
}