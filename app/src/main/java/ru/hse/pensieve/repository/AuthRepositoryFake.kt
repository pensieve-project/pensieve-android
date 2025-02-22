package ru.hse.pensieve.repository

import LoginRequest
import RegistrationRequest
import User

class AuthRepositoryFake {
    suspend fun login(request: LoginRequest): User {
        return User(
            id = 1,
            email = request.email,
            username = "fake_user",
        )
    }

    suspend fun register(request: RegistrationRequest): User {
        return User(
            id = 2,
            email = request.email,
            username = request.username
        )
    }
}