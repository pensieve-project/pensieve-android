package ru.hse.pensieve

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import ru.hse.pensieve.api.AuthApiService
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.repository.AuthRepository
import ru.hse.pensieve.models.TokenManager

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Client.init(this)
    }
}
