package ru.hse.pensieve.authentication.route

import ru.hse.pensieve.authentication.models.LoginRequest
import ru.hse.pensieve.authentication.models.RegistrationRequest
import ru.hse.pensieve.authentication.models.AuthenticationResponse

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/register")
    fun register(@Body request: RegistrationRequest): Deferred<AuthenticationResponse>

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Deferred<AuthenticationResponse>
}

