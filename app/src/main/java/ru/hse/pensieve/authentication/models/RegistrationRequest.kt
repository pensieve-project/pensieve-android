package ru.hse.pensieve.authentication.models

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)