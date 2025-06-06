package ru.hse.pensieve.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

object UserPreferences {

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    fun saveUserId(userId: UUID) {
        sharedPreferences.edit().putString("user_id", userId.toString()).apply()
    }

    fun saveUserUsername(userId: UUID, username: String) {
        sharedPreferences.edit().putString("username_$userId", username).apply()
    }

    fun getUsername(userId: UUID): String? {
        return sharedPreferences.getString("username_$userId", null)
    }

    fun getUserId(): UUID? {
        val userIdString = sharedPreferences.getString("user_id", null)
        return userIdString?.let { UUID.fromString(it) }
    }

    fun clearUserId() {
        sharedPreferences.edit().remove("user_id").apply()
    }
}