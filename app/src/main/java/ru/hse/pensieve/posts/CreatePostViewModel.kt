package ru.hse.pensieve.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.posts.repository.PostRepository
import java.util.UUID
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.themes.models.Theme

class CreatePostViewModel: ViewModel() {
    private val postRepository = PostRepository()

    val postTheme = MutableLiveData<Theme>()
    val postThemeTitle = MutableLiveData<String>()
    val postText = MutableLiveData<String>()
    val postPhotoChosen = MutableLiveData<Boolean>()
//    val postMedia = MutableLiveData<Uri>()

    fun createPost() {
        viewModelScope.launch {
            try {
                if (postTheme.value == null) {
                    postRepository.createPostInNewTheme(postText.value ?: "", postThemeTitle.value ?: "")
                } else {
                    postRepository.createPostInExistingTheme(postText.value ?: "", postTheme.value!!.themeId!!)
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
}