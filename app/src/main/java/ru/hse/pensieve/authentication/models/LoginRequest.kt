package ru.hse.pensieve.authentication.models

data class LoginRequest(
    val email: String,
    val password: String
)