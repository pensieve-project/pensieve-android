package ru.hse.pensieve.themes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.themes.repository.ThemeRepository
import java.util.UUID
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.themes.models.Theme

class ThemesViewModel: ViewModel() {
    private val themeRepository = ThemeRepository()

    private val _themes = MutableLiveData<List<Theme>>()
    val themes: LiveData<List<Theme>> get() = _themes

    private val _theme = MutableLiveData<Theme>()
    val theme: LiveData<Theme> get() = _theme

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
            }
        }
    }
}