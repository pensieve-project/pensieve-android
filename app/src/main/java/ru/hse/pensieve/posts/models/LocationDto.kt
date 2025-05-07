package ru.hse.pensieve.posts.models

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("placeName")
    val placeName: String
)