package ru.hse.pensieve.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.search.repository.SearchRepository
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.repository.ThemeRepository
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class SearchViewModel: ViewModel() {
    private val searchRepository = SearchRepository()
    private val themeRepository = ThemeRepository()
    private val userId: UUID? = UserPreferences.getUserId()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _themes = MutableLiveData<List<Theme>>()
    val themes: LiveData<List<Theme>> get() = _themes

    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                val users = searchRepository.searchUsers(query).filter { it.userId != userId }
                _users.value = users
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun searchThemes(query: String) {
        viewModelScope.launch {
            try {
                val themes = searchRepository.searchThemes(query)
                _themes.value = themes
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun getAllThemes() {
        viewModelScope.launch {
            try {
                val themes = themeRepository.getAllThemes()
                _themes.value = themes
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun clearUsersSearch() {
        _users.value = emptyList()
    }
}