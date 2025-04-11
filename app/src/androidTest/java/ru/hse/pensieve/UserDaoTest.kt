//package ru.hse.pensieve
//
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.Assert.*
//import ru.hse.pensieve.room.AppDatabase
//import ru.hse.pensieve.room.daos.UserDao
//import ru.hse.pensieve.room.entities.User
//
//class UserDaoTest {
//    private lateinit var database: AppDatabase
//    private lateinit var userDao: UserDao
//
//    @Before
//    fun setup() {
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            AppDatabase::class.java
//        ).allowMainThreadQueries().build()
//        userDao = database.userDao()
//    }
//
//    @After
//    fun teardown() {
//        database.close()
//    }
//
//    @Test
//    fun insertAndDeleteUser() = runBlocking {
//        val user = User(1, "ExampleUser", "email@example.com", "example_password")
//        userDao.insert(user)
//        userDao.delete(user)
//        val deletedUser = userDao.getUserById(1)
//        assertNull(deletedUser)
//    }
//
//    @Test
//    fun insertAndDeleteUserById() = runBlocking {
//        val user = User(1, "ExampleUser", "email@example.com", "example_password")
//        userDao.insert(user)
//        userDao.deleteUserById(1)
//        val deletedUser = userDao.getUserById(1)
//        assertNull(deletedUser)
//    }
//
//    @Test
//    fun insertAndUpdateUser() = runBlocking {
//        val user = User(1, "ExampleUser", "email@example.com", "example_password")
//        userDao.insert(user)
//        val updatedUser = user.copy(name = "User")
//        userDao.update(updatedUser)
//        val retrievedUser = userDao.getUserById(1)
//        assertEquals("User", retrievedUser?.name)
//    }
//
//    @Test
//    fun getAllUsers() = runBlocking {
//        val user1 = User(1, "User1", "user1@example.com", "password1")
//        val user2 = User(2, "User2", "user2@example.com", "password2")
//        val user3 = User(3, "User3", "user3@example.com", "password3")
//
//        userDao.insert(user1)
//        userDao.insert(user2)
//        userDao.insert(user3)
//
//        val users = userDao.getAllUsers()
//
//        assertEquals(3, users.size)
//        assertTrue(users.contains(user1))
//        assertTrue(users.contains(user2))
//        assertTrue(users.contains(user3))
//    }
//}