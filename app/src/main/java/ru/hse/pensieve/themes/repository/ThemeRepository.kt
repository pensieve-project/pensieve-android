package ru.hse.pensieve.themes.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Like
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class ThemeRepository {
    private val themeService = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID? = UserPreferences.getUserId()

    suspend fun createTheme(title: String): Theme {
        return themeService.createTheme(ThemeRequest(userId, title)).await()
    }

    suspend fun getAllThemes(): List<Theme> {
        return themeService.getAllThemes().await()
    }

    suspend fun getThemeTitle(themeId: UUID): String {
        return themeService.getThemeTitle(themeId).await()
    }

    suspend fun hasUserLikedTheme(authorId: UUID, themeId: UUID) : Boolean {
        return themeService.hasUserLikedTheme(Like(authorId, themeId)).await()
    }

    suspend fun likeTheme(authorId: UUID, themeId: UUID): Boolean {
        return themeService.likeTheme(Like(authorId, themeId)).await().isSuccessful
    }

    suspend fun unlikeTheme(authorId: UUID, themeId: UUID): Boolean {
        return themeService.unlikeTheme(Like(authorId, themeId)).await().isSuccessful
    }
}