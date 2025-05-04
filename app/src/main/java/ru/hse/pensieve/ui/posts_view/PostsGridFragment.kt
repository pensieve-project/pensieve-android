package ru.hse.pensieve.ui.posts_view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.FragmentPostsGridBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.utils.UserPreferences

class PostsGridFragment : Fragment() {
    private var _binding: FragmentPostsGridBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ImageAdapter(emptyList()) { postId ->
            val intent = Intent(requireContext(), OpenPostActivity::class.java).apply {
                putExtra("POST_ID", postId.toString())
            }
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        lifecycleScope.launch {
            val currentUserId = UserPreferences.getUserId()
            if (currentUserId != null) {
                viewModel.getAllUsersPosts(currentUserId)
                viewModel.posts.observe(viewLifecycleOwner) { posts ->
                    adapter.posts = posts?.reversed() as List<Post>
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}