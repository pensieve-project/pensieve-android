package ru.hse.pensieve.posts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.posts.models.Comment
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.repository.PostRepository
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository()

    private val _post = MutableLiveData<Post?>()
    val post: MutableLiveData<Post?> get() = _post

    private val _posts = MutableLiveData<List<Post?>?>()
    val posts: MutableLiveData<List<Post?>?> get() = _posts

    private val _postImages = MutableLiveData<List<Bitmap>>()
    val postImages: MutableLiveData<List<Bitmap>> get() = _postImages

    private val _comments = MutableLiveData<List<Comment?>?>()
    val comments: MutableLiveData<List<Comment?>?> get() = _comments

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: MutableLiveData<Boolean> get() = _isLiked

    private val _likesCount = MutableLiveData<Int>()
    val likesCount: MutableLiveData<Int> get() = _likesCount

    private val _commentsCount = MutableLiveData<Int>()
    val commentsCount: MutableLiveData<Int> get() = _commentsCount

    private val userId = UserPreferences.getUserId()

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

    fun loadLikeStatus(postId: UUID, userId: UUID) {
        viewModelScope.launch {
            val hasLiked = postRepository.hasUserLikedPost(postId, userId)
            _isLiked.value = hasLiked
        }
    }

    fun toggleLike(postId: UUID, isLiked: Boolean) {
        viewModelScope.launch {
            if (isLiked) {
                postRepository.unlikePost(postId, userId!!)
                _likesCount.value = (_likesCount.value ?: 0) - 1
            } else {
                postRepository.likePost(postId, userId!!)
                _likesCount.value = (_likesCount.value ?: 0) + 1
            }
            _isLiked.value = !isLiked
        }
    }

    fun loadCommentsCount(postId: UUID) {
        viewModelScope.launch {
            val count = postRepository.getCommentsCount(postId)
            _commentsCount.value = count
        }
    }

    fun loadComments(postId: UUID) {
        viewModelScope.launch {
            try {
                val comments = postRepository.getPostComments(postId)
                if (comments.isNotEmpty()) {
                    _comments.value = comments
                } else {
                    println("No comments found for post $postId")
                    _comments.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error in loadComments: ${e.message}")
                e.printStackTrace()
                _comments.value = emptyList()
            }
        }
    }

    fun leaveComment(postId: UUID, commentText: String) {
        viewModelScope.launch {
            try {
                val newComment = postRepository.leaveComment(postId, userId!!, commentText)
                val currentList = _comments.value?.toMutableList() ?: mutableListOf()
                currentList.add(newComment)
                _comments.value = currentList
            }
            catch (e: Exception) {
                println("Error in leaveComment: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}