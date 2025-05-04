package ru.hse.pensieve.profiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.albums.repository.AlbumsRepository
import ru.hse.pensieve.profiles.repository.ProfileRepository
import java.util.UUID

class ProfileViewModel : ViewModel() {

    private val profileRepository = ProfileRepository()

    private val _userNames = MutableLiveData<Map<UUID, String>>()
    val userNames: LiveData<Map<UUID, String>> get() = _userNames

    suspend fun getUsername(userId: UUID): String {
        return try {
            profileRepository.getUsernameByAuthorId(userId)
        } catch (e: Exception) {
            "Unknown"
        }
    }

}