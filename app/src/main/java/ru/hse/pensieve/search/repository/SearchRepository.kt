package ru.hse.pensieve.search.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.search.route.SearchService
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class SearchRepository {
    private val searchApi = Client.getInstanceOfService(SearchService::class.java)
    private val userId: UUID? = UserPreferences.getUserId()

    suspend fun searchUsers(query: String): List<User> {
        return searchApi.searchUsers(query).await()
    }

    suspend fun searchThemes(query: String): List<Theme> {
        return searchApi.searchThemes(query).await()
    }
}