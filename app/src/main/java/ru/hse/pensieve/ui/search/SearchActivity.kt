package ru.hse.pensieve.ui.search

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivitySearchBinding
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.themes.ThemeAdapter

class SearchActivity :  ToolbarActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val viewModel: ThemesViewModel by viewModels()
    private lateinit var adapter: ThemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ThemeAdapter(emptyList()) {
            // click
        }

        binding.recyclerView.adapter = adapter

        viewModel.themes.observe(this, { themes ->
            if (themes != null) {
                adapter = ThemeAdapter(themes) {
                    // click
                }
                binding.recyclerView.adapter = adapter
            }
        })

        viewModel.getAllThemes()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.root.findViewById(R.id.my_toolbar))

        val buttons = listOf(
            binding.root.findViewById<ImageButton>(R.id.button1),
            binding.root.findViewById<ImageButton>(R.id.button2),
            binding.root.findViewById<ImageButton>(R.id.button3),
            binding.root.findViewById<ImageButton>(R.id.button4),
            binding.root.findViewById<ImageButton>(R.id.button5)
        )
        setupButtons(buttons, defaultIcons, selectedIcons, -1)
        val button2 = binding.root.findViewById<ImageButton>(R.id.button2)
        button2.setImageResource(R.drawable.search_fill)
    }
}