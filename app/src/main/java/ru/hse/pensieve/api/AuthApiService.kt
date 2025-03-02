package ru.hse.pensieve.api

import ru.hse.pensieve.models.LoginRequest
import ru.hse.pensieve.models.RegistrationRequest
import ru.hse.pensieve.models.AuthenticationResponse

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/register")
    fun register(@Body request: RegistrationRequest): Deferred<AuthenticationResponse>

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Deferred<AuthenticationResponse>
}

