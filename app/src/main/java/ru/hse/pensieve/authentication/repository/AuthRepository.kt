package ru.hse.pensieve.authentication.repository

import ru.hse.pensieve.authentication.models.AuthenticationResponse
import ru.hse.pensieve.authentication.models.LoginRequest
import ru.hse.pensieve.authentication.models.RegistrationRequest
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.authentication.route.AuthService

class AuthRepository {
    private val authApi = Client.getInstanceOfService(AuthService::class.java)
    private val tokenManager = Client.getTokenManagerInstance()

    suspend fun login(request: LoginRequest): AuthenticationResponse {
        val response = authApi.login(request).await()
        tokenManager.saveAccessToken(response.accessToken!!)
        tokenManager.saveRefreshToken(response.refreshToken!!)
        return response
    }

    suspend fun register(request: RegistrationRequest): AuthenticationResponse {
        val response = authApi.register(request).await()
        tokenManager.saveAccessToken(response.accessToken!!)
        tokenManager.saveRefreshToken(response.refreshToken!!)
        return response
    }
}