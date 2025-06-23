package ru.hse.pensieve.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.feed.repository.FeedRepository
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.themes.models.Theme
import java.time.Instant

class FeedViewModel: ViewModel() {
    private val feedRepository = FeedRepository()

    private val _subscribedPosts = MutableLiveData<List<Post>>()
    val subscribedPosts: LiveData<List<Post>> get() = _subscribedPosts

    private val _popularPosts = MutableLiveData<List<Post>>()
    val popularPosts: LiveData<List<Post>> get() = _popularPosts

    private val _popularThemes = MutableLiveData<List<Theme>>()
    val popularThemes: LiveData<List<Theme>> get() = _popularThemes

    private var lastSeenTime: Instant? = null
    private var isLoading = false
    private var isEndReached = false

    fun loadMorePosts() {
        if (isLoading || isEndReached) return

        isLoading = true
        viewModelScope.launch {
            try {
                val lastTime = lastSeenTime ?: Instant.now()
                val newPosts = feedRepository.getSubscriptionsFeed(5, lastTime)
                println("Received " + newPosts.size + " posts in feed view model")
                if (newPosts.isEmpty()) {
                    isEndReached = true
                } else {
                    val currentPosts = _subscribedPosts.value ?: emptyList()
                    _subscribedPosts.value = currentPosts + newPosts
                    lastSeenTime = newPosts.lastOrNull()?.timeStamp
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadPopularPosts() {
        viewModelScope.launch {
            try {
                val posts = feedRepository.getPopularFeed()
                _popularPosts.value = posts
            } catch (e: Exception) {
                println("Error in getPopularPosts: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun loadPopularThemes() {
        viewModelScope.launch {
            try {
                val themes = feedRepository.getPopularThemes()
                _popularThemes.value = themes
            } catch (e: Exception) {
                println("Error in getPopularThemes: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}