package ru.hse.pensieve.profiles

import androidx.lifecycle.ViewModel
import ru.hse.pensieve.profiles.repository.ProfileRepository
import java.util.UUID

class ProfileViewModel : ViewModel() {

    private val profileRepository = ProfileRepository()

    suspend fun getUsername(userId: UUID): String {
        return try {
            profileRepository.getUsernameByAuthorId(userId)
        } catch (e: Exception) {
            "unknown"
        }
    }

    suspend fun getAvatar(userId: UUID): ByteArray? {
        return try {
            profileRepository.getAvatarByAuthorId(userId)
        } catch (e: Exception) {
            null
        }
    }

}