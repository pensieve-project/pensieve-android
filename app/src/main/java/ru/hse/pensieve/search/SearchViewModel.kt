package ru.hse.pensieve.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.search.repository.SearchRepository

class SearchViewModel: ViewModel() {
    private val searchRepository = SearchRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                val users = searchRepository.searchUsers(query)
                _users.value = users
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun clearSearch() {
        _users.value = emptyList()
    }
}