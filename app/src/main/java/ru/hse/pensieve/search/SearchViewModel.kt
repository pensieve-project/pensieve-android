package ru.hse.pensieve.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.posts.models.Post
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
    private var currentSearchQuery: String = ""

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _themes = MutableLiveData<List<Theme>>()
    val themes: LiveData<List<Theme>> get() = _themes

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

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

    fun searchPosts(query: String = currentSearchQuery) {
        if (query.isBlank()) return
        currentSearchQuery = query
        viewModelScope.launch {
            try {
                val posts = searchRepository.searchPosts(query)
                _posts.value = posts
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

    fun clearPostsSearch() {
        _posts.value = emptyList()
    }

    fun clearThemesSearch() {
        _themes.value = emptyList()
    }
}