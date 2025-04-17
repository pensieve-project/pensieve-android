package ru.hse.pensieve.profiles.route

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import ru.hse.pensieve.profiles.models.Profile
import java.util.UUID

interface ProfileService {
    @Multipart
    @POST("/profile")
    suspend fun createProfile(@Part("authorId") authorId: RequestBody,
                              @Part avatar: MultipartBody.Part,
                              @Part("description") description: RequestBody
    )

    @Multipart
    @PUT("/profile/edit")
    suspend fun editProfile(@Part("authorId") authorId: RequestBody,
                            @Part avatar: MultipartBody.Part,
                            @Part("description") description: RequestBody
    )

    @GET("/profile/by-authorId")
    suspend fun getProfileByAuthorId(@Query("authorId") authorId: UUID): Profile

    @GET("/profile/avatar")
    suspend fun getAvatarByAuthorId(@Query("authorId") authorId: UUID): ByteArray

    @GET("/profile/username")
    suspend fun getUsernameByAuthorId(@Query("authorId") authorId: UUID): String
}