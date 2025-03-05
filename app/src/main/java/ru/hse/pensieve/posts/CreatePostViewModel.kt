package ru.hse.pensieve.posts

import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import ru.hse.pensieve.posts.repository.PostRepository
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.themes.models.Theme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreatePostViewModel: ViewModel() {
    private val postRepository = PostRepository()

    val postTheme = MutableLiveData<Theme>()
    val postThemeTitle = MutableLiveData<String>()
    val postText = MutableLiveData<String>()
    val postPhoto = MutableLiveData<Uri>()

    suspend fun createPost(photo: File) {
        try {
            println(photo)
            if (postTheme.value == null) {
                postRepository.createPostInNewTheme(postText.value ?: "", photo,postThemeTitle.value ?: "")
            } else {
                postRepository.createPostInExistingTheme(postText.value ?: "", photo, postTheme.value!!.themeId!!)
            }
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