package ru.hse.pensieve.themes.repository

import ru.hse.pensieve.api.Client
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

    suspend fun getThemeTitle(themeId: UUID): String {
        return themeApi.getThemeTitle(themeId).await()
    }
}