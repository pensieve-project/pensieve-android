package ru.hse.pensieve.ui.posts_view

import androidx.lifecycle.LiveData
import ru.hse.pensieve.posts.models.Post

interface PostsDataSource {
    fun getPosts(): LiveData<List<Post>>
    fun loadMorePosts()
}