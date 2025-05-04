package ru.hse.pensieve.albums.repository

import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.albums.route.AlbumsService
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class AlbumsRepository {
    private val albumsService = Client.getInstanceOfService(AlbumsService::class.java)
    private val userId: UUID = UserPreferences.getUserId()!!

    suspend fun getUserAlbums(id: UUID?): List<Album> {
        return try {
            val response = albumsService.getUserAlbums(id ?: userId).await()
            response
        } catch (e: Exception) {
            println("Error in getUserAlbums: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAlbumPosts(coAuthors: Set<UUID>): List<Post> {
        return try {
            val response = albumsService.getAlbumPosts(coAuthors).await()
            response
        } catch (e: Exception) {
            println("Error in getAlbumPosts: ${e.message}")
            emptyList()
        }
    }
}