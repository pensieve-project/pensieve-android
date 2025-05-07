package ru.hse.pensieve.albums

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.albums.repository.AlbumsRepository
import ru.hse.pensieve.posts.models.Post
import java.util.UUID

class AlbumsViewModel : ViewModel() {

    private val albumsRepository = AlbumsRepository()

    private val _albums = MutableLiveData<List<Album>>()
    val albums: MutableLiveData<List<Album>> get() = _albums

    private val _posts = MutableLiveData<List<Post>>()
    val posts: MutableLiveData<List<Post>> get() = _posts

    fun getUserAlbums(userId: UUID? = null) {
        viewModelScope.launch {
            try {
                val albums = albumsRepository.getUserAlbums(userId)
                _albums.value = albums
            } catch (e: Exception) {
                println("getUserAlbums: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getAlbumPosts(albumId: UUID) {
        viewModelScope.launch {
            try {
                val posts = albumsRepository.getAlbumPosts(albumId)
                _posts.value = posts
            } catch (e: Exception) {
                println("getAlbumPosts: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}