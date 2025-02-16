package ru.hse.pensieve.repository

import AuthRepository
import LoginRequest
import RegistrationRequest
import User

class AuthRepositoryFake : AuthRepository {
    override suspend fun login(request: LoginRequest): User {
        return User(
            id = 1,
            email = request.email,
            username = "fake_user",
        )
    }

    override suspend fun register(request: RegistrationRequest): User {
        return User(
            id = 2,
            email = request.email,
            username = request.username
        )
    }
}