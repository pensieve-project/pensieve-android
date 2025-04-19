package ru.hse.pensieve.ui.feed

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityFeedBinding
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsFeedFragment

class FeedActivity :  ToolbarActivity() {
    private lateinit var binding: ActivityFeedBinding
    private val viewModel: SearchViewModel by viewModels()
    private var isSearchActive = false
    private lateinit var postsFeedFragment: PostsFeedFragment
    private lateinit var usersSearchFragment: UsersSearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postsFeedFragment = PostsFeedFragment.newInstance()
        usersSearchFragment = UsersSearchFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.feed_container, usersSearchFragment)
            .hide(usersSearchFragment)
            .add(R.id.feed_container, postsFeedFragment)
            .commit()

        setupToolBar()
        setupSearchView()
        showPostsFeed()
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSearchActive = true
                showSearchResults()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchUsers(it) }
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
                viewModel.searchUsers(newText)
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

    private fun showPostsFeed() {
        supportFragmentManager.beginTransaction()
            .hide(usersSearchFragment)
            .show(postsFeedFragment)
            .commit()
    }

    private fun showSearchResults() {
        supportFragmentManager.beginTransaction()
            .hide(postsFeedFragment)
            .show(usersSearchFragment)
            .commit()
    }

    private fun closeSearch() {
        isSearchActive = false
        binding.searchView.clearFocus()
        viewModel.clearSearch()
        showPostsFeed()
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
        val button1 = binding.root.findViewById<ImageButton>(R.id.button1)
        button1.setImageResource(R.drawable.house_fill)
    }
}