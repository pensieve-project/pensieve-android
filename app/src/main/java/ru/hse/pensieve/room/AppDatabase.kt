package ru.hse.pensieve.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.hse.pensieve.room.daos.PostDao
import ru.hse.pensieve.room.daos.UserDao
import ru.hse.pensieve.room.entities.Post
import ru.hse.pensieve.room.entities.User

@Database(
    entities = [Post::class, User::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration(false)
                .build()
        }
    }
}