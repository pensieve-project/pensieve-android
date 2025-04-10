package ru.hse.pensieve.ui.profile

import android.os.Bundle
import android.widget.ImageButton
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.ActivityProfileBinding
import ru.hse.pensieve.ui.ToolbarActivity
import ru.hse.pensieve.ui.posts_view.PostFragment
import ru.hse.pensieve.ui.posts_view.PostsGridFragment

class ProfileActivity :  ToolbarActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var postFragment: PostFragment
    private lateinit var postsGridFragment: PostsGridFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
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

        showGrid()
    }

    fun showGrid() {
        postsGridFragment = PostsGridFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postsGridFragment)
            .addToBackStack(null)
            .commit()
    }

    fun showPost(postNumber: Int) {
        postFragment = PostFragment.newInstance(postNumber)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postFragment)
            .addToBackStack(null)
            .commit()
    }
}