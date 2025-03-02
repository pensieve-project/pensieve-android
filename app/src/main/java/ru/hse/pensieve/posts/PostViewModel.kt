package ru.hse.pensieve.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.posts.models.PostRequest
import ru.hse.pensieve.posts.repository.PostRepository
import java.util.UUID

class PostViewModel: ViewModel() {
    private val postRepository = PostRepository()

    fun createPost(text: String, threadId: UUID) {
        viewModelScope.launch {
            try {
                postRepository.createPost(text, threadId);
            } catch (e: Exception) {
                println("Exception!! In createPost PostViewModel")
                println(e.cause)
                println(e.stackTrace)
                println(e.message)
            }
        }
    }
}