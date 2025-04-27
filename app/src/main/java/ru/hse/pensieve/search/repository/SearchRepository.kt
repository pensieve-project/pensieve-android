package ru.hse.pensieve.search.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.search.route.SearchService
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class SearchRepository {
    private val searchApi = Client.getInstanceOfService(SearchService::class.java)
    private val userId: UUID? = UserPreferences.getUserId()

    suspend fun searchUsers(query: String): List<User> {
        return searchApi.searchUsers(query).await()
    }
}