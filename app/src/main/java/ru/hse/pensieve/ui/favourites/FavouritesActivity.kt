package ru.hse.pensieve.ui.favourites

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityFavouritesBinding
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.authorization.LoginActivity

class FavouritesActivity : ToolbarActivity() {
    private lateinit var binding: ActivityFavouritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.root.findViewById(R.id.my_toolbar))

        val buttons = listOf(
            binding.root.findViewById<ImageButton>(R.id.button1),
            binding.root.findViewById<ImageButton>(R.id.button2),
            binding.root.findViewById<ImageButton>(R.id.button3),
            binding.root.findViewById<ImageButton>(R.id.button4),
            binding.root.findViewById<ImageButton>(R.id.button5)
        )
        setupButtons(buttons, defaultIcons, selectedIcons, -1)
        val button4 = binding.root.findViewById<ImageButton>(R.id.button4)
        button4.setImageResource(R.drawable.heart_fill)
    }
}