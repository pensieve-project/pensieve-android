package ru.hse.pensieve.themes.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
import java.util.UUID

class ThemeRepository {
    private val themeApi = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID? = UUID.fromString("a4956bff-2362-4ac7-88c1-fc4fdee8810f")

    suspend fun createTheme(title: String): Theme {
        return themeApi.createTheme(ThemeRequest(userId, title)).await()
    }

    suspend fun getAllThemes(): List<Theme> {
        return themeApi.getAllThemes().await()
    }

    suspend fun searchThemes(query: String): List<Theme> {
        return themeApi.searchThemes(query).await()
    }
}