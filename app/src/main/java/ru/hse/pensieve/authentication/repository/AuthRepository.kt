package ru.hse.pensieve.authentication.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import ru.hse.pensieve.authentication.models.AuthenticationResponse
import ru.hse.pensieve.authentication.models.LoginRequest
import ru.hse.pensieve.authentication.models.RegistrationRequest
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.authentication.route.AuthService
import ru.hse.pensieve.profiles.route.ProfileService
import java.io.File
import java.io.FileOutputStream

class AuthRepository(private val context: Context) {
    private val authApi = Client.getInstanceOfService(AuthService::class.java)
    private val profileApi = Client.getInstanceOfService(ProfileService::class.java)
    private val tokenManager = Client.getTokenManagerInstance()

    suspend fun login(request: LoginRequest): AuthenticationResponse {
        val response = authApi.login(request).await()
        tokenManager.saveAccessToken(response.accessToken!!)
        tokenManager.saveRefreshToken(response.refreshToken!!)
        return response
    }

    @SuppressLint("ResourceType")
    suspend fun register(request: RegistrationRequest): AuthenticationResponse {
        val response = authApi.register(request).await()
        tokenManager.saveAccessToken(response.accessToken!!)
        tokenManager.saveRefreshToken(response.refreshToken!!)
        val authorIdPart = RequestBody.create(MediaType.parse("text/plain"), response.id.toString())
        val resourceId = ru.hse.pensieve.R.drawable.default_avatar
        val tempFile = File(context.cacheDir, "default_avatar.png")
        context.resources.openRawResource(resourceId).use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        val avatarPart = MultipartBody.Part.createFormData(
            "avatar", tempFile.getName(), RequestBody.create(
                MediaType.parse("image/*"), tempFile
            )
        )
        val descriptionPart = RequestBody.create(MediaType.parse("text/plain"), "Hi, I'm " + response.username + "!")
        try {
            profileApi.createProfile(authorIdPart, avatarPart, descriptionPart)
        } catch (e: Exception) {
            println("Failed to create profile: ${e.message}")
        }
        return response
    }

    private fun getDefaultAvatar(): ByteArray {
        val bitmap: Bitmap = BitmapFactory.decodeResource(
            context.resources,
            ru.hse.pensieve.R.drawable.default_avatar
        )
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}