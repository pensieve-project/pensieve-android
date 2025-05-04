package ru.hse.pensieve.search.route

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.themes.models.Theme

interface SearchService {
    @GET("/search/users")
    fun searchUsers(@Query("query") query: String): Deferred<List<User>>

    @GET("/search/themes")
    fun searchThemes(@Query("query") query: String): Deferred<List<Theme>>
}
