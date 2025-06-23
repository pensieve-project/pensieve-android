package ru.hse.pensieve.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
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
import ru.hse.pensieve.room.repositories.UserRepository
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.room.entities.User
import ru.hse.pensieve.ui.albums.AlbumsFragment
import ru.hse.pensieve.ui.authorization.LoginActivity
import ru.hse.pensieve.ui.posts_view.PostsOnMapFragment


class ProfileActivity :  ToolbarActivity(), ProfileContainer {
    private lateinit var binding: ActivityProfileBinding
    private val profileRepository = ProfileRepository()

    private lateinit var postsGridFragment: PostsGridFragment
    private lateinit var postsOnMapFragment: PostsOnMapFragment
    private lateinit var albumsFragment: AlbumsFragment
    private lateinit var menuButton: ImageButton
    private lateinit var menuContainer: LinearLayout

    private enum class ActiveButton { POSTS, LOCATIONS, ALBUMS }
    private var currentActiveButton = ActiveButton.POSTS

    private lateinit var userId: UUID
    private var isMenuOpen = false

    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val userRepository by lazy { UserRepository(appDatabase.userDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = UserPreferences.getUserId() ?: return
        loadProfile(userId)

        binding.locationsButton.setOnClickListener {
            showMap()
            setActiveButton(ActiveButton.LOCATIONS)
        }

        binding.postsButton.setOnClickListener {
            showGrid()
            setActiveButton(ActiveButton.POSTS)
        }

        binding.albumsButton.setOnClickListener {
            showAlbums()
            setActiveButton(ActiveButton.ALBUMS)
        }

        binding.followersButton.setOnClickListener {
            showSubscriptions(SubscriptionType.FOLLOWERS)
        }

        binding.followingsButton.setOnClickListener {
            showSubscriptions(SubscriptionType.FOLLOWINGS)
        }

        setupMenu()

        onBackPressedDispatcher.addCallback(this) {
            if (binding.fullscreenContainer.visibility == View.VISIBLE) {
                hideFullscreenContainer()
                supportFragmentManager.popBackStack()
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

    fun loadProfile(authorId: UUID) {
        lifecycleScope.launch {
            try {
                binding.username.text = UserPreferences.getUsername(authorId)
                val cachedProfile = userRepository.getUserById(authorId)
                if (cachedProfile == null) {
                    val profile = withContext(Dispatchers.IO) {
                        profileRepository.getProfileByAuthorId(authorId)
                    }
                    userRepository.insertUser(User(authorId, UserPreferences.getUsername(authorId)!!, profile.description, profile.avatar))
                    println("Added " + userRepository.getUsernameById(authorId))
                    binding.description.text = profile.description;
                    if (profile.avatar == null || profile.avatar.isEmpty()) {
                        binding.avatar.setImageResource(R.drawable.default_avatar)
                    } else {
                        val avatarBitmap = BitmapFactory.decodeByteArray(profile.avatar, 0, profile.avatar.size)
                        binding.avatar.setImageBitmap(avatarBitmap)
                    }
                }
                else {
                    println("Add from cache")
                    binding.description.setText(cachedProfile.description)
                    if (cachedProfile.avatar == null || cachedProfile.avatar!!.isEmpty()) {
                        binding.avatar.setImageResource(R.drawable.default_avatar)
                    } else {
                        val avatarBitmap =
                            BitmapFactory.decodeByteArray(cachedProfile.avatar, 0, cachedProfile.avatar!!.size)
                        binding.avatar.setImageBitmap(avatarBitmap)
                    }
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

    override fun showFullscreenContainer() {
        binding.mainContent.visibility = View.GONE
        binding.fullscreenContainer.visibility = View.VISIBLE
    }

    override fun hideFullscreenContainer() {
        binding.mainContent.visibility = View.VISIBLE
        binding.fullscreenContainer.visibility = View.GONE
    }

    private fun setActiveButton(button: ActiveButton) {
        currentActiveButton = button

        binding.postsButton.setImageResource(
            if (button == ActiveButton.POSTS) R.drawable.grid_3x3_gap_fill
            else R.drawable.grid_3x3_gap_light
        )

        binding.locationsButton.setImageResource(
            if (button == ActiveButton.LOCATIONS) R.drawable.geo_fill
            else R.drawable.geo_light
        )

        binding.albumsButton.setImageResource(
            if (button == ActiveButton.ALBUMS) R.drawable.people_fill
            else R.drawable.people_light
        )
    }

    private fun setupMenu() {
        menuButton = findViewById(R.id.menu_button)
        menuContainer = findViewById(R.id.menu_container)

        menuButton.apply {
            setOnClickListener {
                isMenuOpen = !isMenuOpen
                toggleMenu(isMenuOpen)
            }
        }

        setupMenuItem(R.id.menu_item1, EditProfileFragment())
        setupMenuItem(R.id.menu_item2) { logout() }
    }

    private fun setupMenuItem(menuItemId: Int, fragment: Fragment? = null, action: (() -> Unit)? = null) {
        findViewById<Button>(menuItemId).setOnClickListener {
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fullscreen_container, it)
                    .addToBackStack(null)
                    .commit()
            }
            action?.invoke()
            showFullscreenContainer()
            closeMenu()
        }
    }

    private fun toggleMenu(open: Boolean) {
        menuButton.setImageResource(
            if (open) R.drawable.ic_three_dots_fill else R.drawable.ic_three_dots
        )

        menuContainer.apply {
            if (open) {
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(200).start()
            } else {
                animate().alpha(0f).setDuration(200).withEndAction {
                    visibility = View.GONE
                }.start()
            }
        }
    }

    private fun closeMenu() {
        isMenuOpen = false
        toggleMenu(false)
    }

    private fun logout() {
        UserPreferences.clearUserId()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
