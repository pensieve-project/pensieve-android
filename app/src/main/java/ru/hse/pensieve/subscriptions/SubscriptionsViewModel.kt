package ru.hse.pensieve.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.subscriptions.repository.SubscriptionsRepository

class SubscriptionsViewModel: ViewModel() {
    private val subscriptionsRepository = SubscriptionsRepository()

    private val _followers = MutableLiveData<List<User>>()
    val followers: LiveData<List<User>> get() = _followers

    private val _followings = MutableLiveData<List<User>>()
    val followings: LiveData<List<User>> get() = _followings

    fun getFollowers(userId: UUID) {
        viewModelScope.launch {
            try {
                val followers = subscriptionsRepository.getSubscribers(userId)
                _followers.value = followers.map {id -> User(id)}
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun getFollowings(userId: UUID) {
        viewModelScope.launch {
            try {
                val followings = subscriptionsRepository.getSubscriptions(userId)
                _followings.value = followings.map {id -> User(id)}
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }
}