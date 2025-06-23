package ru.hse.pensieve.ui.albums

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityAlbumBinding
import ru.hse.pensieve.profiles.ProfileViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsGridFragment
import ru.hse.pensieve.ui.posts_view.PostsOnMapFragment
import java.util.UUID

class AlbumActivity : ToolbarActivity() {
    private lateinit var binding: ActivityAlbumBinding

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var postsGridFragment: PostsGridFragment
    private lateinit var postsOnMapFragment: PostsOnMapFragment

    private enum class ActiveButton { POSTS, LOCATIONS }
    private var currentActiveButton = ActiveButton.POSTS

    private lateinit var albumId: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        albumId = UUID.fromString(intent.getStringExtra("ALBUM_ID"))
        val avatar = intent.getByteArrayExtra("ALBUM_AVATAR")
        val coAuthorsStrings = intent.getStringArrayListExtra("CO_AUTHORS")
        val coAuthors = coAuthorsStrings?.map { UUID.fromString(it) }

        if (avatar == null) {
            binding.avatarImageView.setImageResource(R.drawable.default_avatar)
        } else {
            val avatarBitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
            binding.avatarImageView.setImageBitmap(avatarBitmap)
        }

        lifecycleScope.launch {
            if (coAuthors != null) {
                val names = coAuthors.map { id ->
                    async {
                        profileViewModel.getUsername(id)
                    }
                }.awaitAll()

                binding.coAuthors.text = names.joinToString(", ")
            } else {
                binding.coAuthors.text = ""
            }
        }

        binding.btnPrev.setOnClickListener {
            finish()
        }

        binding.locationsButton.setOnClickListener {
            showMap()
            setActiveButton(ActiveButton.LOCATIONS)
        }

        binding.postsButton.setOnClickListener {
            showGrid()
            setActiveButton(ActiveButton.POSTS)
        }

        showGrid()
    }

    private fun showGrid() {
        postsGridFragment = PostsGridFragment.newInstance("ALBUM_POSTS", albumId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsGridFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showMap() {
        postsOnMapFragment = PostsOnMapFragment.newInstance("ALBUM_POSTS", albumId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsOnMapFragment)
            .addToBackStack(null)
            .commit()
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
    }
}
