package ru.hse.pensieve.models

data class LoginRequest(
    val email: String,
    val password: String
)