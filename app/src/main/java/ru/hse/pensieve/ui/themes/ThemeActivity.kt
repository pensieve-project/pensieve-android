package ru.hse.pensieve.ui.themes

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityThemeBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostsGridFragment
import ru.hse.pensieve.ui.posts_view.PostsOnMapFragment
import ru.hse.pensieve.ui.profile.ForeignProfileActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class ThemeActivity : ToolbarActivity() {
    private lateinit var binding: ActivityThemeBinding
    private val themesViewModel: ThemesViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var postsGridFragment: PostsGridFragment
    private lateinit var postsOnMapFragment: PostsOnMapFragment
    private lateinit var themeId: UUID

    private enum class ActiveButton { POSTS, LOCATIONS }
    private var currentActiveButton = ActiveButton.POSTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
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

        themeId = UUID.fromString(intent.getStringExtra("THEME_ID"))
        println("get " + themeId)

        themesViewModel.getThemeById(themeId);
        themesViewModel.theme.observe(this) { theme ->
            if (theme != null) {
                binding.themeName.text = theme.title
                postViewModel.getAuthorUsername(theme.authorId!!)
                postViewModel.authorUsername.observe(this) { authorUsername ->
                    binding.author.text = "Theme created by " + authorUsername
                }
                binding.author.setOnClickListener {
                    val intent = Intent(this, ForeignProfileActivity::class.java).apply {
                        putExtra("USER_ID", theme.authorId.toString())
                    }
                    startActivity(intent)
                }
                binding.date.text = formatInstant(theme.timeStamp!!)
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
        postsGridFragment = PostsGridFragment.newInstance("THEMES_POSTS", themeId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsGridFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showMap() {
        postsOnMapFragment = PostsOnMapFragment.newInstance("THEMES_POSTS", themeId)
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

    private fun formatInstant(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}