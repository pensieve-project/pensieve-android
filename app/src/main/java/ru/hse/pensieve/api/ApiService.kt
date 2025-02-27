package ru.hse.pensieve.api

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    fun getGreeting(): Call<GreetingResponse>
}

data class GreetingResponse(
    @JsonProperty("message") val message: String = ""
)
