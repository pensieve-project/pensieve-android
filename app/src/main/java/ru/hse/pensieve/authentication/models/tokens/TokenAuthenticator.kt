package ru.hse.pensieve.authentication.models.tokens;

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.hse.pensieve.authentication.models.TokenManager
import ru.hse.pensieve.authentication.route.RefreshService

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApi: RefreshService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val refreshToken = runBlocking { tokenManager.refreshToken.first() }
            if (refreshToken == null) {
                return null
            }

            val newTokens = runBlocking {
                try {
                    refreshApi.getNewTokens(refreshToken).await()
                } catch (e: Exception) {
                    null
                }
            }

            if (newTokens == null || newTokens.refreshToken == null || newTokens.accessToken == null) {
                runBlocking {
                    tokenManager.deleteTokens()
                }
                return null
            }

            runBlocking {
                tokenManager.saveAccessToken(newTokens.accessToken)
                tokenManager.saveRefreshToken(newTokens.refreshToken)
            }

            return response.request().newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        }
    }
}
