package ru.hse.pensieve.ui.posts_view

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityOpenPostBinding
import ru.hse.pensieve.databinding.ActivityProfileBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.utils.UserPreferences

class OpenPostActivity : ToolbarActivity() {
    private lateinit var binding: ActivityOpenPostBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenPostBinding.inflate(layoutInflater)
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
        val button5 = binding.root.findViewById<ImageButton>(R.id.button5)
        button5.setImageResource(R.drawable.person_fill1)

        val postNumber = intent.getIntExtra("POST_NUMBER", 0)

        lifecycleScope.launch {
            UserPreferences.getUserId()?.let { userId ->
                viewModel.getAllPostsImages(userId)
                viewModel.posts.observe(this@OpenPostActivity) { posts ->
                    if (posts != null && postNumber < posts.size) {
                        supportFragmentManager.commit {
                            replace(R.id.post_container, PostFragment.newInstance(postNumber))
                        }
                    }
                }
            }
        }

        binding.btnPrev.setOnClickListener {
            finish()
        }
    }
}