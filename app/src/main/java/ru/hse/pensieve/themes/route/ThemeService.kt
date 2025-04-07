package ru.hse.pensieve.themes.route

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest

interface ThemeService {
    @POST("/themes")
    fun createTheme(@Body request: ThemeRequest): Deferred<Theme>

    @GET("/themes")
    fun getAllThemes(): Deferred<List<Theme>>

    @GET("/themes/search")
    fun searchThemes(@Query("query") query: String): Deferred<List<Theme>>
}
