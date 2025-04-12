package ru.hse.pensieve.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.hse.pensieve.room.entities.User
import java.util.UUID

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: UUID): User?

    @Query("SELECT username FROM users WHERE id = :userId")
    suspend fun getUsernameById(userId: UUID): String?
}