package ru.hse.pensieve.search.route

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import ru.hse.pensieve.search.models.User

interface SearchService {
    @GET("/search")
    fun searchUsers(@Query("query") query: String): Deferred<List<User>>
}
