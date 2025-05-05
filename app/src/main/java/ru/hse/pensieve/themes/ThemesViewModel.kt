package ru.hse.pensieve.themes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.themes.repository.ThemeRepository
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class ThemesViewModel: ViewModel() {
    private val themeRepository = ThemeRepository()
    private val profileRepository = ProfileRepository()

    private val userId = UserPreferences.getUserId()

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: MutableLiveData<Boolean> get() = _isLiked

    private val _authorUsernames = MutableLiveData<Map<UUID, String>>()
    val authorUsernames: LiveData<Map<UUID, String>> = _authorUsernames

    private val _themes = MutableLiveData<List<Theme>>()
    val themes: LiveData<List<Theme>> get() = _themes

    private val _likedThemes = MutableLiveData<List<Theme>>()
    val likedThemes: LiveData<List<Theme>> get() = _likedThemes

    private val _theme = MutableLiveData<Theme>()
    val theme: MutableLiveData<Theme> get() = _theme

    fun createTheme(title: String) {
        viewModelScope.launch {
            try {
                themeRepository.createTheme(title)
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun getThemeById(themeId : UUID) {
        viewModelScope.launch {
            try {
                val theme = themeRepository.getThemeById(themeId)
                _theme.value = theme
            } catch (e: Exception) {
                println("getThemeById: ${e.message}")
                e.printStackTrace()
                
    fun getLikedThemes() {
        viewModelScope.launch {
            try {
                val likedThemes = themeRepository.getLikedThemes(userId!!)
                _likedThemes.value = likedThemes
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun toggleLike(themeId: UUID, isLiked: Boolean) {
        viewModelScope.launch {
            if (isLiked) {
                themeRepository.unlikeTheme(userId!!, themeId)
            } else {
                themeRepository.likeTheme(userId!!, themeId)
            }
            getLikedThemes()
        }
    }

    fun loadAuthorUsernames(authorIds: Set<UUID>) {
        viewModelScope.launch {
            try {
                val loadedUsernames = authorIds.associateWith { id ->
                    profileRepository.getUsernameByAuthorId(id).takeIf { it.isNotEmpty() } ?: "Unknown"
                }
                _authorUsernames.value = _authorUsernames.value.orEmpty() + loadedUsernames
            } catch (e: Exception) {
                val errorMap = authorIds.associateWith { "Unknown" }
                _authorUsernames.value = _authorUsernames.value.orEmpty() + errorMap
            }
        }
    }
}