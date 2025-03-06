package ru.hse.pensieve.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ru.hse.pensieve.R
import ru.hse.pensieve.ui.favourites.FavouritesActivity
import ru.hse.pensieve.ui.feed.FeedActivity
import ru.hse.pensieve.ui.search.SearchActivity
import ru.hse.pensieve.ui.postcreation.PostCreationActivity
import ru.hse.pensieve.ui.profile.ProfileActivity
import androidx.appcompat.widget.Toolbar

abstract class ToolbarActivity : AppCompatActivity() {
    companion object {
        private const val PREFS_NAME = "ToolbarPrefs"
        private const val SELECTED_BUTTON_KEY = "selectedButton"
    }

    protected open val defaultIcons = listOf(
        R.drawable.house_light,
        R.drawable.search_light,
        R.drawable.plus_square_light,
        R.drawable.heart_light,
        R.drawable.person_light1
    )

    protected open val selectedIcons = listOf(
        R.drawable.house_fill,
        R.drawable.search_fill,
        R.drawable.plus_square_fill,
        R.drawable.heart_fill,
        R.drawable.person_fill1
    )

    protected open val targetActivities = listOf(
        FeedActivity::class.java,
        SearchActivity::class.java,
        PostCreationActivity::class.java,
        FavouritesActivity::class.java,
        ProfileActivity::class.java
    )

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toolbar)

        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        val buttons = listOf(
            findViewById<ImageButton>(R.id.button1),
            findViewById<ImageButton>(R.id.button2),
            findViewById<ImageButton>(R.id.button3),
            findViewById<ImageButton>(R.id.button4),
            findViewById<ImageButton>(R.id.button5)
        )

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val selectedButtonIndex = prefs.getInt(SELECTED_BUTTON_KEY, -1)

        setupButtons(buttons, defaultIcons, selectedIcons, selectedButtonIndex)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun setupButtons(
        buttons: List<ImageButton>,
        defaultIcons: List<Int>,
        selectedIcons: List<Int>,
        selectedButtonIndex: Int
    ) {
        var currentSelectedButtonIndex = selectedButtonIndex

        buttons.forEachIndexed { index, button ->
            button.setImageResource(defaultIcons[index])
        }

        if (currentSelectedButtonIndex != -1) {
            buttons[currentSelectedButtonIndex].setImageResource(selectedIcons[currentSelectedButtonIndex])
        }

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (currentSelectedButtonIndex != -1) {
                    buttons[currentSelectedButtonIndex].setImageResource(defaultIcons[currentSelectedButtonIndex])
                }

                button.setImageResource(selectedIcons[index])

                currentSelectedButtonIndex = index

                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                prefs.edit().putInt(SELECTED_BUTTON_KEY, index).apply()

                if (index < targetActivities.size) {
                    val intent = Intent(this, targetActivities[index])
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
                    startActivity(intent)
                }
            }
        }
    }
}