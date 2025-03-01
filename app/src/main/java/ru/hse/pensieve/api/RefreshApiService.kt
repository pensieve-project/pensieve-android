package ru.hse.pensieve.api

import retrofit2.http.GET

interface RefreshApiService {
    @GET("/auth/token")
    suspend fun getNewAccess(): String
}
