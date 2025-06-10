package ru.hse.pensieve.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.hse.pensieve.room.daos.PostDao
import ru.hse.pensieve.room.daos.UserDao
import ru.hse.pensieve.room.entities.PostEntity
import ru.hse.pensieve.room.entities.User

@Database(
    entities = [PostEntity::class, User::class],
    version = 3
)
@TypeConverters(RoomConverters::class)
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