package ru.hse.pensieve.themes.route

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.hse.pensieve.themes.models.Like
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
import java.util.UUID

interface ThemeService {
    @POST("/themes")
    fun createTheme(@Body request: ThemeRequest): Deferred<Theme>

    @GET("/themes")
    fun getAllThemes(): Deferred<List<Theme>>

    @GET("/themes/get-liked")
    fun getLikedThemes(@Query("authorId") authorId: UUID): Deferred<List<Theme>>

    @GET("/themes/title")
    fun getThemeTitle(@Query("themeId") themeId: UUID): Deferred<String>

    @GET("/themes/liked")
    fun hasUserLikedTheme(@Query("authorId") authorId: UUID, @Query("themeId") themeId: UUID): Deferred<Boolean>

    @POST("/themes/like")
    fun likeTheme(@Body request: Like?): Deferred<Response<Void>>

    @DELETE("/themes/unlike")
    fun unlikeTheme(@Body request: Like?): Deferred<Response<Void>>
}
