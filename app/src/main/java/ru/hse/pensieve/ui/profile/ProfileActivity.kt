package ru.hse.pensieve.ui.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.addCallback
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityProfileBinding
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsGridFragment
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.pensieve.ui.albums.AlbumsFragment
import ru.hse.pensieve.ui.posts_view.PostsOnMapFragment


class ProfileActivity :  ToolbarActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileRepository = ProfileRepository()

    private lateinit var postsGridFragment: PostsGridFragment
    private lateinit var postsOnMapFragment: PostsOnMapFragment
    private lateinit var albumsFragment: AlbumsFragment

    private lateinit var userId: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = UserPreferences.getUserId() ?: return
        if (userId != null) {
            loadProfile(userId)
        }

        binding.locationsButton.setOnClickListener {
            showMap()
        }

        binding.postsButton.setOnClickListener {
            showGrid()
        }

        binding.albumsButton.setOnClickListener {
            showAlbums()
        }

        binding.followersButton.setOnClickListener {
            showSubscriptions(SubscriptionType.FOLLOWERS)
        }

        binding.followingsButton.setOnClickListener {
            showSubscriptions(SubscriptionType.FOLLOWINGS)
        }

        onBackPressedDispatcher.addCallback(this) {
            if (binding.fullscreenContainer.visibility == View.VISIBLE) {
                binding.mainContent.visibility = View.VISIBLE
                binding.fullscreenContainer.visibility = View.GONE
            } else {
                finish()
            }
        }

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

        showGrid()
    }

    private fun loadProfile(authorId: UUID) {
        lifecycleScope.launch {
            try {
                val profile = withContext(Dispatchers.IO) {
                    profileRepository.getProfileByAuthorId(authorId)
                }
                binding.username.text = UserPreferences.getUsername(authorId);
                binding.description.text = profile.description;
                if (profile.avatar == null || profile.avatar.isEmpty()) {
                    binding.avatar.setImageResource(R.drawable.default_avatar)
                } else {
                    val avatarBitmap = BitmapFactory.decodeByteArray(profile.avatar, 0, profile.avatar.size)
                    binding.avatar.setImageBitmap(avatarBitmap)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private fun showGrid() {
        postsGridFragment = PostsGridFragment.newInstance("USERS_POSTS", UserPreferences.getUserId()!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsGridFragment)
            .commit()
    }

    private fun showMap() {
        postsOnMapFragment = PostsOnMapFragment.newInstance("USERS_POSTS", UserPreferences.getUserId()!!)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsOnMapFragment)
            .commit()
    }

    private fun showAlbums() {
        albumsFragment = AlbumsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, albumsFragment)
            .commit()
    }

    private fun showSubscriptions(type: SubscriptionType) {
        val fragment = SubscriptionsFragment.newInstance(userId, type)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fullscreen_container, fragment)
            .addToBackStack(null)
            .commit()

        showFullscreenContainer()
    }

    fun showFullscreenContainer() {
        binding.mainContent.visibility = View.GONE
        binding.fullscreenContainer.visibility = View.VISIBLE
    }

    fun hideFullscreenContainer() {
        binding.mainContent.visibility = View.VISIBLE
        binding.fullscreenContainer.visibility = View.GONE
    }
}