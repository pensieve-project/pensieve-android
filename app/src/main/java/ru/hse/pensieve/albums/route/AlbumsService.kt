package ru.hse.pensieve.albums.route

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.posts.models.Post
import java.util.UUID

interface AlbumsService {

    @GET("/albums")
    fun getUserAlbums(@Query("userId") userId: UUID): Deferred<List<Album>>

    @GET("/albums/posts")
    fun getAlbumPosts(@Query("coAuthors") coAuthors: Set<UUID>): Deferred<List<Post>>

}
