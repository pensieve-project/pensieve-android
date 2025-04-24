package ru.hse.pensieve.ui.posts_view

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityOpenPostBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

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

        val postId = UUID.fromString(intent.getStringExtra("POST_ID"))
        println("get " + postId)

        supportFragmentManager.commit {
            replace(R.id.post_container, PostFragment.newInstance(postId))
        }

        binding.btnPrev.setOnClickListener {
            finish()
        }
    }
}