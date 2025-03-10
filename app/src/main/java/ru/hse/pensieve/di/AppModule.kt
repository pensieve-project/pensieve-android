package ru.hse.pensieve.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.hse.pensieve.posts.repository.PostRepository
import ru.hse.pensieve.repositories.UserRepository
import ru.hse.pensieve.room.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(appDatabase: AppDatabase): UserRepository {
        return UserRepository(appDatabase.userDao())
    }
}