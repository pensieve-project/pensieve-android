package ru.hse.pensieve.profiles

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.room.repositories.UserRepository
import ru.hse.pensieve.room.entities.User
import ru.hse.pensieve.utils.UserPreferences
import java.io.File

class EditProfileViewModel(private val userRepository : UserRepository): ViewModel() {
    private val profileRepository = ProfileRepository()

    val description = MutableLiveData<String>()
    val avatar = MutableLiveData<Uri>()
    private val userId = UserPreferences.getUserId()


    suspend fun editProfile(avatar: File) {
        try {
            println(avatar)
            profileRepository.editProfile(userId!!, avatar, description.value ?: "")
            userRepository.updateUser(User(userId, profileRepository.getUsernameByAuthorId(userId), description.value, avatar.readBytes()))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                println("Coroutine was cancelled: ${e.message}")
                throw e
            } else {
                println("Exception: ${e.message}")
            }
        }
    }
}