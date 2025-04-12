package ru.hse.pensieve.api

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.hse.pensieve.authentication.route.RefreshService
import ru.hse.pensieve.authentication.models.tokens.AuthInterceptor
import ru.hse.pensieve.authentication.models.tokens.TokenAuthenticator
import ru.hse.pensieve.authentication.models.TokenManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import retrofit2.converter.scalars.ScalarsConverterFactory

object JacksonConfig {
    val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())
}

object Client {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var retrofit: Retrofit? = null
    private var tokenManager: TokenManager? = null

    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

        val refreshApi = refreshRetrofit.create(RefreshService::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager!!))
            .authenticator(TokenAuthenticator(tokenManager!!, refreshApi))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(JacksonConfig.objectMapper))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    fun getTokenManagerInstance(): TokenManager {
        return tokenManager!!
    }

    fun <T> getInstanceOfService(type: Class<T>): T {
        return retrofit!!.create(type)
    }
}
