package ru.hse.pensieve.ui.favourites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityFavouritesBinding
import ru.hse.pensieve.feed.FeedViewModel
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.themes.ThemeActivity
import ru.hse.pensieve.ui.themes.ThemeAdapter
import ru.hse.pensieve.ui.themes.ThemesSearchFragment
import java.util.UUID

class FavouritesActivity : ToolbarActivity() {
    private lateinit var binding: ActivityFavouritesBinding
    private val themeViewModel: ThemesViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()
    private lateinit var themesSearchFragment: ThemesSearchFragment
    private lateinit var adapter: ThemeAdapter
    private var isSearchActive = false

    private enum class ActiveButton { FAVOURITES, POPULAR }
    private var currentActiveButton = ActiveButton.FAVOURITES

    private var likedThemeIds: Set<UUID> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        themesSearchFragment = ThemesSearchFragment.newInstance {theme ->
            val intent = Intent(this, ThemeActivity::class.java).apply {
                putExtra("THEME_ID", theme.themeId.toString())
            }
            startActivity(intent)
        }

        setupToolBar()
        setupRecyclerView()
        setupButtons()
        setupObservers()
        setupSearchView()

        setMode(ActiveButton.FAVOURITES)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ThemeAdapter(
            themes = emptyList(),
            onItemClick = { theme ->
                val intent = Intent(this, ThemeActivity::class.java).apply {
                    putExtra("THEME_ID", theme.themeId.toString())
                }
                startActivity(intent)
            },
            onLikeClick = { themeId, isLiked ->
                if (currentActiveButton == ActiveButton.FAVOURITES && !isLiked) {
                    val newThemes = adapter.currentList.filter { it.themeId != themeId }
                    adapter.updateData(newThemes = newThemes)
                }
                themeViewModel.toggleLike(themeId, isLiked)
            },
            likedThemes = emptySet(),
            authorUsernames = emptyMap()
        )

        binding.recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        binding.favouritesButton.setOnClickListener { setMode(ActiveButton.FAVOURITES) }
        binding.popularButton.setOnClickListener { setMode(ActiveButton.POPULAR) }
    }

    private fun setMode(mode: ActiveButton) {
        currentActiveButton = mode
        setActiveButton(mode)
        if (mode == ActiveButton.FAVOURITES) {
            showRecycler()
            themeViewModel.getLikedThemes()
        } else {
            showRecycler()
            feedViewModel.loadPopularThemes()
        }
    }

    private fun setupObservers() {
        themeViewModel.likedThemes.observe(this) { likedThemes ->
            likedThemeIds = likedThemes?.mapNotNull { it.themeId }?.toSet() ?: emptySet()
            if (currentActiveButton == ActiveButton.FAVOURITES) {
                adapter.updateData(
                    newThemes = likedThemes ?: emptyList(),
                    newLikedThemes = likedThemeIds
                )

                val authorIds = likedThemes.mapNotNull { theme -> theme.authorId }.toSet()
                if (authorIds.isNotEmpty()) {
                    themeViewModel.loadAuthorUsernames(authorIds)
                }
            } else if (currentActiveButton == ActiveButton.POPULAR) {
                adapter.updateData(
                    newThemes = adapter.currentList,
                    newLikedThemes = likedThemeIds
                )
            }
        }

        feedViewModel.popularThemes.observe(this) { themes ->
            if (currentActiveButton == ActiveButton.POPULAR) {
                adapter.updateData(newThemes = themes ?: emptyList(), newLikedThemes = likedThemeIds)
                val authorIds = themes?.mapNotNull { it.authorId }?.toSet() ?: emptySet()
                if (authorIds.isNotEmpty()) themeViewModel.loadAuthorUsernames(authorIds)
            }
        }

        themeViewModel.authorUsernames.observe(this) { usernamesMap ->
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

    private fun setupSearchView() {
        val searchView = binding.searchView

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchViewModel.searchThemes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    closeSearch()
                    return true
                }
                if (!isSearchActive) {
                    isSearchActive = true
                    showSearchResults()
                }
                searchViewModel.searchThemes(newText)
                return true
            }
        })

        searchView.setOnCloseListener {
            if (isSearchActive) {
                closeSearch()
                true
            } else {
                false
            }
        }
    }

    private fun showSearchResults() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.search_fragment_container, themesSearchFragment)
            .commit()
        binding.searchFragmentContainer.visibility = View.VISIBLE
        binding.themesListContainer.visibility = View.GONE
    }

    private fun showRecycler() {
        binding.searchFragmentContainer.visibility = View.GONE
        binding.themesListContainer.visibility = View.VISIBLE
    }

    private fun closeSearch() {
        isSearchActive = false
        binding.searchView.clearFocus()
        searchViewModel.clearThemesSearch()
        binding.searchFragmentContainer.visibility = View.GONE
        binding.themesListContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .remove(themesSearchFragment)
            .commit()
    }

    private fun setActiveButton(button: ActiveButton) {
        currentActiveButton = button

        binding.favouritesButton.setImageResource(
            if (button == ActiveButton.FAVOURITES) R.drawable.heart_fill
            else R.drawable.heart_light
        )

        binding.popularButton.setImageResource(
            if (button == ActiveButton.POPULAR) R.drawable.fire_fill
            else R.drawable.fire_light
        )
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
        button4.setImageResource(R.drawable.filter_fill)
    }
}