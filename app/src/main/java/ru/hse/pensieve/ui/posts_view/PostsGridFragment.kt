package ru.hse.pensieve.ui.posts_view

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
import ru.hse.pensieve.ui.profile.ProfileActivity
import ru.hse.pensieve.utils.UserPreferences

class PostsGridFragment : Fragment() {
    private var _binding: FragmentPostsGridBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ImageAdapter(emptyList()) { position ->
            (requireActivity() as ProfileActivity).showPost(reversePosition(position))
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        lifecycleScope.launch {
            val currentUserId = UserPreferences.getUserId()
            if (currentUserId != null) {
                viewModel.getAllPostsImages(currentUserId)
                viewModel.postImages.observe(viewLifecycleOwner) { bitmaps ->
                    adapter.images = bitmaps.reversed()
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun reversePosition(position : Int) : Int {
        return adapter.itemCount - position - 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}