package ru.hse.pensieve.posts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.repository.PostRepository
import java.util.UUID

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository()

    private val _post = MutableLiveData<Post?>()
    val post: MutableLiveData<Post?> get() = _post

    private val _posts = MutableLiveData<List<Post?>?>()
    val posts: MutableLiveData<List<Post?>?> get() = _posts

    private val _postImages = MutableLiveData<List<Bitmap>>()
    val postImages: MutableLiveData<List<Bitmap>> get() = _postImages

    suspend fun getCurrentPost(currentUserId: UUID, postNumber: Int) {
        println("id: " + currentUserId)
        viewModelScope.launch {
            try {
                val posts = postRepository.getPostsByAuthor(currentUserId)
                if (posts.isNotEmpty()) {
                    _post.value = posts[postNumber]
                } else {
                    println("No posts found for user $currentUserId")
                    _post.value = null
                }
            } catch (e: Exception) {
                println("Error in getCurrentPost: ${e.message}")
                e.printStackTrace()
                _post.value = null
            }
        }
    }

    suspend fun getAllPosts(currentUserId: UUID) {
        println("id: " + currentUserId)
        viewModelScope.launch {
            try {
                val posts = postRepository.getPostsByAuthor(currentUserId)
                if (posts.isNotEmpty()) {
                    _posts.value = posts
                } else {
                    println("No posts found for user $currentUserId")
                    _posts.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error in getAllPosts: ${e.message}")
                e.printStackTrace()
                _posts.value = emptyList()
            }
        }
    }

    suspend fun getAllPostsImages(currentUserId: UUID) {
        viewModelScope.launch {
            try {
                val posts = postRepository.getPostsByAuthor(currentUserId)
                if (posts.isNotEmpty()) {
                    val images = posts.mapNotNull { post ->
                        post.photo?.toBitmap()
                    }
                    _postImages.value = images
                    _posts.value = posts
                } else {
                    println("No posts found for user $currentUserId")
                    _postImages.value = emptyList()
                    _posts.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error in getAllPostsImages: ${e.message}")
                e.printStackTrace()
                _postImages.value = emptyList()
                _posts.value = emptyList()
            }
        }
    }

    private fun ByteArray.toBitmap(): Bitmap? {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }
}