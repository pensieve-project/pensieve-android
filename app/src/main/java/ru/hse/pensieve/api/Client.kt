package ru.hse.pensieve.api

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.hse.pensieve.models.AuthInterceptor
import ru.hse.pensieve.models.TokenAuthenticator
import ru.hse.pensieve.models.TokenManager

object Client {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        val tokenManager = TokenManager(context.applicationContext)

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

        val refreshApi = refreshRetrofit.create(RefreshApiService::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager, refreshApi))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    fun <T> getInstanceOfService(type: Class<T>): T {
        return retrofit!!.create(type)
    }
}
