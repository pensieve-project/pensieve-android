package ru.hse.pensieve.themes.repository

import kotlinx.coroutines.Deferred
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Like
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class ThemeRepository {
    private val themeApi = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID? = UserPreferences.getUserId()

    suspend fun createTheme(title: String): Theme {
        return themeApi.createTheme(ThemeRequest(userId, title)).await()
    }

    suspend fun getAllThemes(): List<Theme> {
        return themeApi.getAllThemes().await()
    }

    suspend fun searchThemes(query: String): List<Theme> {
        return themeApi.searchThemes(query).await()
    }
    
    suspend fun getThemeTitle(themeId: UUID): String {
        return themeApi.getThemeTitle(themeId).await()
    }

    suspend fun hasUserLikedTheme(authorId: UUID, themeId: UUID) : Boolean {
        return themeApi.hasUserLikedTheme(Like(authorId, themeId)).await()
    }

    suspend fun likeTheme(authorId: UUID, themeId: UUID): Boolean {
        return themeApi.likeTheme(Like(authorId, themeId)).await().isSuccessful
    }

    suspend fun unlikeTheme(authorId: UUID, themeId: UUID): Boolean {
        return themeApi.unlikeTheme(Like(authorId, themeId)).await().isSuccessful
    }
}