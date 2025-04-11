package ru.hse.pensieve.ui.posts_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.FragmentPostsFeedBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.utils.UserPreferences

class PostsFeedFragment : Fragment() {
    private var _binding: FragmentPostsFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostsAdapter(viewModel, this)
        binding.recyclerViewFeed.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PostsFeedFragment.adapter
        }

        viewModel.allPosts.observe(viewLifecycleOwner) { allPosts ->
            adapter.posts = allPosts as List<Post>
        }

        loadPosts()
    }

    private fun loadPosts() {
        lifecycleScope.launch {
            viewModel.getAllPosts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PostsFeedFragment()
    }
}