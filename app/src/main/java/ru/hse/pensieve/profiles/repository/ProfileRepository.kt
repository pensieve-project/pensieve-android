package ru.hse.pensieve.profiles.repository

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.profiles.models.Profile
import ru.hse.pensieve.profiles.route.ProfileService
import java.io.File
import java.util.UUID

class ProfileRepository {
    private val profileService = Client.getInstanceOfService(ProfileService::class.java)

    suspend fun createProfile(authorId: UUID, avatar: File, description: String): Boolean {
        return try {
            val authorIdPart = RequestBody.create(MediaType.parse("text/plain"), authorId.toString())
            val avatarPart = MultipartBody.Part.createFormData(
                "avatar", avatar.getName(), RequestBody.create(
                    MediaType.parse("image/*"), avatar
                )
            )
            val descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description)
            profileService.createProfile(authorIdPart, avatarPart, descriptionPart)
            true
        } catch (e: Exception) {
            println("Error creating profile: ${e.message}")
            false
        }
    }

    suspend fun editProfile(authorId: UUID, avatar: File, description: String): Boolean {
        return try {
            val avatarPart = MultipartBody.Part.createFormData(
                "avatar", avatar.getName(), RequestBody.create(
                    MediaType.parse("image/*"), avatar
                )
            )
            val descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description)
            val authorIdPart = RequestBody.create(MediaType.parse("text/plain"), authorId.toString())
            profileService.editProfile(authorIdPart, avatarPart, descriptionPart)
            true
        } catch (e: Exception) {
            println("Error editing profile: ${e.message}")
            false
        }
    }

    suspend fun getProfileByAuthorId(authorId: UUID): Profile {
        return profileService.getProfileByAuthorId(authorId)
    }

    suspend fun getAvatarByAuthorId(authorId: UUID): ByteArray {
        return profileService.getAvatarByAuthorId(authorId)
    }

    suspend fun getUsernameByAuthorId(authorId: UUID): String {
        return profileService.getUsernameByAuthorId(authorId)
    }
}