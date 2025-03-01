package ru.hse.pensieve.models;

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.hse.pensieve.api.RefreshApiService

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApi: RefreshApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val newAccess = runBlocking {
                try {
                    refreshApi.getNewAccess()
                } catch (e: Exception) {
                    null
                }
            } ?: return null

            runBlocking {
                tokenManager.saveAccessToken(newAccess)
            }

            println(tokenManager.accessToken)

            return response.request().newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
        }
    }
}
