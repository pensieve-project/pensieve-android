package ru.hse.pensieve.api

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object Client {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
