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

    fun createTheme(title: String) {
        viewModelScope.launch {
            try {
                themeRepository.createTheme(title)
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

    fun searchThemes(query: String) {
        viewModelScope.launch {
            try {
                val themes = themeRepository.searchThemes(query)
                _themes.value = themes
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }
}