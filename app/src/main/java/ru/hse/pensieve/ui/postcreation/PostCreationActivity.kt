package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.hse.pensieve.databinding.ActivityPostCreationBinding
import ru.hse.pensieve.posts.PostViewModel
import java.util.UUID

class PostCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostCreationBinding
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPublish.setOnClickListener {
            val postText = binding.etPostContent.text.toString().trim()

            if (postText.isEmpty()) {
                Toast.makeText(this, "Введите текст поста", Toast.LENGTH_SHORT).show()
            } else {
                postViewModel.createPost(postText, UUID.fromString("a1b4720b-9671-4f8e-abac-2c75e37da506"))
                Toast.makeText(this, "Пост опубликован: $postText", Toast.LENGTH_LONG).show()
                binding.etPostContent.setText("")
            }
        }
    }
}
