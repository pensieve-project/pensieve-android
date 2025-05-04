package ru.hse.pensieve.ui.favourites

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityFavouritesBinding
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.themes.ThemeAdapter
import java.util.UUID

class FavouritesActivity : ToolbarActivity() {
    private lateinit var binding: ActivityFavouritesBinding
    private val viewModel: ThemesViewModel by viewModels()
    private lateinit var adapter: ThemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupRecyclerView()
        setupObservers()

        viewModel.getLikedThemes()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ThemeAdapter(
            themes = emptyList(),
            onItemClick = {
                // click
            },
            onLikeClick = { themeId, isLiked ->
                if (!isLiked) {
                    val newThemes = adapter.currentList.filter { it.themeId != themeId }
                    adapter.updateData(newThemes = newThemes)
                }
                viewModel.toggleLike(themeId, isLiked)
            },
            likedThemes = emptySet(),
            authorUsernames = emptyMap()
        )

        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.likedThemes.observe(this) { likedThemes ->
            likedThemes?.let {
                val likedThemeIds = it.map { theme -> theme.themeId!! }.toSet()
                adapter.updateData(
                    newThemes = it,
                    newLikedThemes = likedThemeIds
                )

                val authorIds = it.mapNotNull { theme -> theme.authorId }.toSet()
                if (authorIds.isNotEmpty()) {
                    viewModel.loadAuthorUsernames(authorIds)
                }
            }
        }

        viewModel.authorUsernames.observe(this) { usernamesMap ->
            usernamesMap?.let { map ->
                adapter.updateAuthorUsernames(map)
                notifyUsernameUpdates(map)
            }
        }
    }

    private fun notifyUsernameUpdates(usernamesMap: Map<UUID, String>) {
        val updatedPositions = adapter.currentList
            .mapIndexedNotNull { index, theme ->
                theme.authorId?.takeIf { usernamesMap.containsKey(it) }?.let { index }
            }
        updatedPositions.forEach { pos -> adapter.notifyItemChanged(pos) }
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
        val button4 = binding.root.findViewById<ImageButton>(R.id.button4)
        button4.setImageResource(R.drawable.heart_fill)
    }
}