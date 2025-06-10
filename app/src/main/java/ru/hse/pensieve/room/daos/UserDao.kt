package ru.hse.pensieve.room.daos

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import ru.hse.pensieve.room.entities.User
import java.util.UUID

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Upsert
    suspend fun upsert(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: UUID): User?

    @Query("SELECT username FROM users WHERE id = :userId")
    suspend fun getUsernameById(userId: UUID): String?

    @Query("SELECT avatar FROM users WHERE id = :userId")
    suspend fun getAvatarById(userId: UUID): ByteArray?

    @Query("DELETE FROM users WHERE id NOT IN " +
            "(SELECT id FROM users ORDER BY lastAccessTime DESC LIMIT 50)")
    suspend fun keepLatestUsers()
}