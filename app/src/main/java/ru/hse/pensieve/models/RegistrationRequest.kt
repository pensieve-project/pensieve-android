package ru.hse.pensieve.models

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)