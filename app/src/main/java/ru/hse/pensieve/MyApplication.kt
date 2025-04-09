package ru.hse.pensieve

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.utils.UserPreferences

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        UserPreferences.init(this)
        Client.init(this)
    }
}
