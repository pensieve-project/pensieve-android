package ru.hse.pensieve.ui.feed

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityFeedBinding
import ru.hse.pensieve.feed.FeedViewModel
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsDataSource
import ru.hse.pensieve.ui.posts_view.PostsFeedFragment

class PopularDataSource(
    private val feedViewModel: FeedViewModel
) : PostsDataSource {
    override fun getPosts(): LiveData<List<Post>> {
        return feedViewModel.popularPosts
    }

    override fun loadMorePosts() {
        feedViewModel.loadPopularPosts()
    }
}


class PopularPostsFeedActivity :  ToolbarActivity() {
    private lateinit var binding: ActivityFeedBinding
    private val feedViewModel: FeedViewModel by viewModels()
    private lateinit var popularFragment: PostsFeedFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        popularFragment = PostsFeedFragment.newInstance(PopularDataSource(feedViewModel))

        supportFragmentManager.beginTransaction()
            .add(R.id.feed_container, popularFragment)
            .commit()

        setupToolBar()
        showPostsFeed()
    }

    private fun showPostsFeed() {
        supportFragmentManager.beginTransaction()
            .show(popularFragment)
            .commit()
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