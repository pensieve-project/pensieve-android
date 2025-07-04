package ru.hse.pensieve.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import ru.hse.pensieve.posts.models.Comment
import ru.hse.pensieve.posts.models.CommentWithAuthor
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.repository.PostRepository
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.themes.repository.ThemeRepository
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val themeRepository = ThemeRepository()
    private val profileRepository = ProfileRepository()

    private val _post = MutableLiveData<Post?>()
    val post: MutableLiveData<Post?> get() = _post

    private val _posts = MutableLiveData<List<Post?>?>()
    val posts: MutableLiveData<List<Post?>?> get() = _posts

    private val _comments = MutableLiveData<List<Comment?>?>()
    val comments: MutableLiveData<List<Comment?>?> get() = _comments

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: MutableLiveData<Boolean> get() = _isLiked

    private val _likesCount = MutableLiveData<Int>()
    val likesCount: MutableLiveData<Int> get() = _likesCount

    private val _commentsCount = MutableLiveData<Int>()
    val commentsCount: MutableLiveData<Int> get() = _commentsCount

    private val _themeTitle = MutableLiveData<String>()
    val themeTitle: MutableLiveData<String> get() = _themeTitle

    private val _authorUsername = MutableLiveData<String>()
    val authorUsername: MutableLiveData<String> get() = _authorUsername

    private val _authorAvatar = MutableLiveData<ByteArray?>()
    val authorAvatar: MutableLiveData<ByteArray?> get() = _authorAvatar

    private val userId = UserPreferences.getUserId()

    val coAuthorsUsernames = MutableLiveData<List<String>>()

    private val _commentsWithAuthors = MutableLiveData<List<CommentWithAuthor>>()
    val commentsWithAuthors: MutableLiveData<List<CommentWithAuthor>> get() = _commentsWithAuthors

    fun getAllThemesPosts(themeId: UUID) {
        viewModelScope.launch {
            try {
                val posts = postRepository.getPostsByTheme(themeId)
                if (posts.isNotEmpty()) {
                    _posts.value = posts
                } else {
                    println("No posts found for theme $themeId")
                    _posts.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error in getAllThemesPosts: ${e.message}")
                e.printStackTrace()
                _posts.value = emptyList()
            }
        }
    }

    fun getAllUsersPosts(currentUserId: UUID) {
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
                println("Error in getAllUsersPosts: ${e.message}")
                e.printStackTrace()
                _posts.value = emptyList()
            }
        }
    }

    fun loadLikeStatus(postId: UUID) {
        viewModelScope.launch {
            val hasLiked = postRepository.hasUserLikedPost(userId!!, postId)
            println(hasLiked)
            _isLiked.value = hasLiked
        }
    }

    fun toggleLike(postId: UUID, isLiked: Boolean) {
        viewModelScope.launch {
            if (isLiked) {
                postRepository.unlikePost(userId!!, postId)
                _likesCount.value = (_likesCount.value ?: 0) - 1
            } else {
                postRepository.likePost(userId!!, postId)
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

    fun loadLikesCount(postId: UUID) {
        viewModelScope.launch {
            val count = postRepository.getLikesCount(postId)
            _likesCount.value = count
        }
    }

    fun loadComments(postId: UUID) {
        viewModelScope.launch {
            try {
                val comments = postRepository.getPostComments(postId)
                if (comments.isNotEmpty()) {
                    _comments.value = comments
                    var commentsWithAuthors: MutableList<CommentWithAuthor> = mutableListOf()
                    for (comment in comments) {
                        val authorAvatar =
                            profileRepository.getProfileByAuthorId(comment.authorId!!).avatar
                        val authorUsername =
                            profileRepository.getUsernameByAuthorId(comment.authorId)
                        commentsWithAuthors.add(
                            CommentWithAuthor(
                                comment,
                                authorUsername,
                                authorAvatar
                            )
                        )
                    }
                    _commentsWithAuthors.value = commentsWithAuthors
                } else {
                    println("No comments found for post $postId")
                    _comments.value = emptyList()
                    _commentsWithAuthors.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error in loadComments: ${e.message}")
                e.printStackTrace()
                _comments.value = emptyList()
                _commentsWithAuthors.value = emptyList()
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
                _commentsCount.value = _commentsCount.value!! + 1
            } catch (e: Exception) {
                println("Error in leaveComment: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getThemeTitle(themeId: UUID) {
        viewModelScope.launch {
            try {
                val themeTitle = themeRepository.getThemeTitle(themeId)
                _themeTitle.value = themeTitle
                println(themeTitle)
            } catch (e: Exception) {
                println("Error in getThemeTitle: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getAuthorData(authorId: UUID) {
        viewModelScope.launch {
            try {
                val username = profileRepository.getUsernameByAuthorId(authorId)
                val profile = profileRepository.getProfileByAuthorId(authorId)
                _authorUsername.value = username
                _authorAvatar.value = profile.avatar
            } catch (e: Exception) {
                println("getAuthorUsername: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getAuthorUsername(authorId: UUID) {
        viewModelScope.launch {
            try {
                val username = profileRepository.getUsernameByAuthorId(authorId)
                _authorUsername.value = username
            } catch (e: Exception) {
                println("getAuthorUsername: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun loadCoAuthorsUsernames(coAuthors: Set<UUID>) {
        viewModelScope.launch {
            try {
                val names = coAuthors.filter { it != post.value?.authorId }.map { id ->
                    async {
                        profileRepository.getUsernameByAuthorId(id)
                    }
                }.awaitAll()
                coAuthorsUsernames.postValue(names)
            } catch (e: Exception) {
                println("loadCoAuthorsUsernames: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getPostById(id: UUID) {
        viewModelScope.launch {
            try {
                val post = postRepository.getPostById(id)
                _post.value = post
            } catch (e: Exception) {
                println("getPostById: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}