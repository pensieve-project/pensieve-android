package ru.hse.pensieve.ui.profile

import android.os.Bundle
import android.widget.ImageButton
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityProfileBinding
import ru.hse.pensieve.ui.ToolbarActivity

class ProfileActivity :  ToolbarActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
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
        val button5 = binding.root.findViewById<ImageButton>(R.id.button5)
        button5.setImageResource(R.drawable.person_fill1)
    }
}