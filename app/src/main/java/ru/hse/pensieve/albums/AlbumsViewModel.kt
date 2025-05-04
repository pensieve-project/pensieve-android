package ru.hse.pensieve.albums

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.albums.repository.AlbumsRepository
import java.util.UUID

class AlbumsViewModel : ViewModel() {

    private val albumsRepository = AlbumsRepository()

    private val _albums = MutableLiveData<List<Album>>()
    val albums: MutableLiveData<List<Album>> get() = _albums

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

//    fun getAlbumPosts(coAuthors: Set<UUID>) {
//        viewModelScope.launch {
//            try {
//                val albums = albumsRepository.getAlbumPosts(coAuthors)
//                _albums.value = albums
//            } catch (e: Exception) {
//                println("getUserAlbums: ${e.message}")
//                e.printStackTrace()
//            }
//        }
//    }

}