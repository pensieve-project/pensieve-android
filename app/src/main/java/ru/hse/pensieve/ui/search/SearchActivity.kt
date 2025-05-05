package ru.hse.pensieve.ui.search

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivitySearchBinding
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.ImageAdapter
import ru.hse.pensieve.ui.posts_view.OpenPostActivity
import ru.hse.pensieve.ui.themes.ThemeActivity
import ru.hse.pensieve.ui.themes.ThemeAdapter

class SearchActivity : ToolbarActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var adapter: ThemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ThemeAdapter(emptyList()) {
            // click
        }

        binding.recyclerView.adapter = adapter

        searchViewModel.themes.observe(this, { themes ->
            if (themes != null) {
                adapter = ThemeAdapter(themes) { theme ->
                    val intent = Intent(this, ThemeActivity::class.java).apply {
                        putExtra("THEME_ID", theme.themeId.toString())
                    }
                    startActivity(intent)
                }
                binding.recyclerView.adapter = adapter
            }
        })

        searchViewModel.getAllThemes()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchThemes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchViewModel.searchThemes(it)
                }
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            searchViewModel.getAllThemes()
            false
        }
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