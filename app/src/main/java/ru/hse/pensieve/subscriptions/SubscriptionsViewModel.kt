package ru.hse.pensieve.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.lifecycle.MutableLiveData
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.subscriptions.repository.SubscriptionsRepository

class SubscriptionsViewModel: ViewModel() {
    private val subscriptionsRepository = SubscriptionsRepository()

    private val _subscribedPosts = MutableLiveData<List<Post>>()
    val subscribedPosts: LiveData<List<Post>> get() = _subscribedPosts

//    fun getUserFeed(userId: UUID, ) {
//        viewModelScope.launch {
//            try {
//                subscriptionsRepository.getUse
//            } catch (e: Exception) {
//                println("Exception: ${e.message}")
//            }
//        }
//    }
}