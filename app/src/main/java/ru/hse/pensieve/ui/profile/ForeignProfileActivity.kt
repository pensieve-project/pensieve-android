package ru.hse.pensieve.ui.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.addCallback
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityForeignProfileBinding
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsGridFragment
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.pensieve.subscriptions.repository.SubscriptionsRepository
import ru.hse.pensieve.repositories.UserRepository
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.room.entities.User
import ru.hse.pensieve.ui.albums.AlbumsFragment
import ru.hse.pensieve.ui.posts_view.PostsOnMapFragment

class ForeignProfileActivity : ToolbarActivity() {
    private lateinit var binding: ActivityForeignProfileBinding
    private val profileRepository = ProfileRepository()
    private val subscriptionsRepository = SubscriptionsRepository()
    private lateinit var userId: UUID

    private lateinit var postsGridFragment: PostsGridFragment
    private lateinit var postsOnMapFragment: PostsOnMapFragment
    private lateinit var albumsFragment: AlbumsFragment

    private var isSubscribed = false

    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val userRepository by lazy { UserRepository(appDatabase.userDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForeignProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = UUID.fromString(intent.getStringExtra("USER_ID"))
        println("get " + userId)

        loadProfile(userId)

        binding.btnClose.setOnClickListener {
            finish()
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
        val button2 = binding.root.findViewById<ImageButton>(R.id.button2)
        button2.setImageResource(R.drawable.search_fill)

        showGrid()
    }

    private fun loadProfile(authorId: UUID) {
        lifecycleScope.launch {
            val cachedProfile = userRepository.getUserById(authorId)
            try {
                val profile = withContext(Dispatchers.IO) {
                    profileRepository.getProfileByAuthorId(authorId)
                }
                val subscribed = withContext(Dispatchers.IO) {
                    subscriptionsRepository.hasUserSubscribed(
                        UserPreferences.getUserId()!!,
                        authorId
                    )
                }
                isSubscribed = subscribed
                binding.subscribeButton.apply {
                    text = context.getString(
                        if (isSubscribed) R.string.unsubscribe else R.string.subscribe
                    )
                    setOnClickListener { toggleSubscription() }
                }
                val username = profileRepository.getUsernameByAuthorId(authorId)
                userRepository.upsertUser(User(authorId, username, profile.description, profile.avatar))
                println("Added " + userRepository.getUsernameById(authorId))
                binding.username.text = username
                binding.description.text = profile.description
                if (profile.avatar == null || profile.avatar.isEmpty()) {
                    binding.avatar.setImageResource(R.drawable.default_avatar)
                } else {
                    val avatarBitmap = BitmapFactory.decodeByteArray(profile.avatar, 0, profile.avatar.size)
                    binding.avatar.setImageBitmap(avatarBitmap)
                }
            } catch (e: Exception) {
                println(e.message)
                if (cachedProfile != null) {
                    println("Add from cache")
                    binding.username.text = cachedProfile.username
                    binding.description.text = cachedProfile.description
                    if (cachedProfile.avatar == null || cachedProfile.avatar!!.isEmpty()) {
                        binding.avatar.setImageResource(R.drawable.default_avatar)
                    } else {
                        val avatarBitmap =
                            BitmapFactory.decodeByteArray(cachedProfile.avatar, 0, cachedProfile.avatar!!.size)
                        binding.avatar.setImageBitmap(avatarBitmap)
                    }
                }
            }
        }
    }

    private fun toggleSubscription() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (isSubscribed) {
                    subscriptionsRepository.unsubscribe(UserPreferences.getUserId()!!, userId)
                } else {
                    subscriptionsRepository.subscribe(UserPreferences.getUserId()!!, userId)
                }
            }
            isSubscribed = !isSubscribed
            binding.subscribeButton.text = getString(
                if (isSubscribed) R.string.unsubscribe else R.string.subscribe
            )
        }
    }

    private fun showGrid() {
        postsGridFragment = PostsGridFragment.newInstance("USERS_POSTS", userId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsGridFragment)
            .commit()
    }

    private fun showMap() {
        postsOnMapFragment = PostsOnMapFragment.newInstance("USERS_POSTS", userId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsOnMapFragment)
            .commit()
    }

    private fun showAlbums() {
        albumsFragment = AlbumsFragment.newInstance(userId)

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
}