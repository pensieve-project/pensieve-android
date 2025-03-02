package ru.hse.pensieve.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import ru.hse.pensieve.models.Tokens

interface RefreshApiService {
    @GET("/auth/token")
    suspend fun getNewTokens(refreshToken: String): Deferred<Tokens>
}
