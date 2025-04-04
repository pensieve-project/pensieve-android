package ru.hse.pensieve.repositories

import ru.hse.pensieve.room.daos.UserDao
import ru.hse.pensieve.room.entities.User
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun getUserById(userId: UUID): User? {
        return userDao.getUserById(userId)
    }
}