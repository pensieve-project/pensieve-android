package ru.hse.pensieve

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.hse.pensieve.api.Client

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Client.init(this)
    }
}
