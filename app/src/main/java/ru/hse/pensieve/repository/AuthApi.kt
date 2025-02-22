package ru.hse.pensieve.repository

import LoginRequest
import RegistrationRequest
import User

interface AuthApi {
    suspend fun login(request: LoginRequest): User
    suspend fun register(request: RegistrationRequest): User
}
