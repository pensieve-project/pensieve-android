package ru.hse.pensieve

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.utils.UserPreferences

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        UserPreferences.init(this)
        Client.init(this)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(this@MyApplication).apply {
                postDao().keepLatestPosts()
                userDao().keepLatestUsers()
            }
        }
    }
}
