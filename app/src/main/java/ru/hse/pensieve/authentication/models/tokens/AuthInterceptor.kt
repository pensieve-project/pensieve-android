package ru.hse.pensieve.authentication.models.tokens

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Response
import ru.hse.pensieve.authentication.models.TokenManager

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { tokenManager.accessToken.first() }
        val request = if (accessToken != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            chain.request().newBuilder()
                .build()
        }
        return chain.proceed(request)
    }
}
