package ru.hse.pensieve.authentication.route

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import ru.hse.pensieve.authentication.models.tokens.Tokens

interface RefreshService {
    @GET("/auth/token")
    suspend fun getNewTokens(refreshToken: String): Deferred<Tokens>
}
