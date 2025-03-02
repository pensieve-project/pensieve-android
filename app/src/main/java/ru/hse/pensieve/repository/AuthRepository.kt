package ru.hse.pensieve.repository

import ru.hse.pensieve.api.AuthApiService
import ru.hse.pensieve.models.AuthenticationResponse
import ru.hse.pensieve.models.LoginRequest
import ru.hse.pensieve.models.RegistrationRequest
import ru.hse.pensieve.api.Client

class AuthRepository {
    private val authApi = Client.getInstanceOfService(AuthApiService::class.java)
    private val tokenManager = Client.getTokenManagerInstance()

    suspend fun login(request: LoginRequest): AuthenticationResponse {
        val response = authApi.login(request).await()
        tokenManager.saveAccessToken(response.accessToken)
        tokenManager.saveRefreshToken(response.refreshToken)
        return response
    }

    suspend fun register(request: RegistrationRequest): AuthenticationResponse {
        val response = authApi.register(request).await()
        tokenManager.saveAccessToken(response.accessToken)
        tokenManager.saveRefreshToken(response.refreshToken)
        return response
    }
}