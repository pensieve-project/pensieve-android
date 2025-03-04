package ru.hse.pensieve.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.posts.repository.PostRepository
import java.util.UUID

class PostViewModel: ViewModel() {
    private val postRepository = PostRepository()

    fun createPost(text: String, themeId: UUID) {
        viewModelScope.launch {
            try {
                postRepository.createPost(text, themeId);
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}