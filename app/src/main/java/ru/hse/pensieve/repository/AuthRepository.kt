package ru.hse.pensieve.repository

import LoginRequest
import RegistrationRequest
import User

class AuthRepository(
    private val authApi: AuthApi
) {

    suspend fun login(request: LoginRequest): User {
        return authApi.login(request)
    }

    suspend fun register(request: RegistrationRequest): User {
        return authApi.register(request)
    }
}