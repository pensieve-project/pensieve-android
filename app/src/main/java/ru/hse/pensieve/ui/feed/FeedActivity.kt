package ru.hse.pensieve.ui.feed

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityFeedBinding
import ru.hse.pensieve.feed.FeedViewModel
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsDataSource
import ru.hse.pensieve.ui.posts_view.PostsFeedFragment

class SubscriptionsDataSource(
    private val feedViewModel: FeedViewModel
) : PostsDataSource {
    override fun getPosts(): LiveData<List<Post>> {
        return feedViewModel.subscribedPosts
    }

    override fun loadMorePosts() {
        feedViewModel.loadMorePosts()
    }
}

class FeedActivity :  ToolbarActivity() {
    private lateinit var binding: ActivityFeedBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()
    private var isSearchActive = false
    private lateinit var usersSearchFragment: UsersSearchFragment
    private lateinit var subscriptionsFragment: PostsFeedFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersSearchFragment = UsersSearchFragment.newInstance()
        subscriptionsFragment = PostsFeedFragment.newInstance(SubscriptionsDataSource(feedViewModel))

        supportFragmentManager.beginTransaction()
            .add(R.id.feed_container, usersSearchFragment)
            .hide(usersSearchFragment)
            .add(R.id.feed_container, subscriptionsFragment)
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
                query?.let { searchViewModel.searchUsers(it) }
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
                searchViewModel.searchUsers(newText)
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
            .show(subscriptionsFragment)
            .commit()
    }

    private fun showSearchResults() {
        supportFragmentManager.beginTransaction()
            .hide(subscriptionsFragment)
            .show(usersSearchFragment)
            .commit()
    }

    private fun closeSearch() {
        isSearchActive = false
        binding.searchView.clearFocus()
        searchViewModel.clearSearch()
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