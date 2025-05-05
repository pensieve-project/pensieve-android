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
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.FragmentPostsGridBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.ui.posts_view.PostsOnMapFragment.Companion
import ru.hse.pensieve.ui.profile.ProfileActivity
import ru.hse.pensieve.utils.UserPreferences

class PostsGridFragment : Fragment() {
    private var _binding: FragmentPostsGridBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var adapter: ImageAdapter
    private lateinit var postsType: String
    private lateinit var id: UUID

    companion object {
        private const val ARG_POSTS_TYPE = "posts_type"
        private const val ARG_ID = "id"

        fun newInstance(postsType: String, id: UUID): PostsGridFragment {
            return PostsGridFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POSTS_TYPE, postsType)
                    putString(ARG_ID, id.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postsType = requireArguments().getString(PostsGridFragment.ARG_POSTS_TYPE)!!
        id = UUID.fromString(requireArguments().getString(PostsGridFragment.ARG_ID))
    }

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
            if (postsType == "USERS_POSTS") {
                viewModel.getAllUsersPosts(id)
                viewModel.posts.observe(viewLifecycleOwner) { posts ->
                    adapter.posts = posts?.sortedByDescending { it!!.timeStamp } as List<Post>
                    adapter.notifyDataSetChanged()
                }
            }
            else if (postsType == "THEMES_POSTS") {
                viewModel.getAllThemesPosts(id)
                viewModel.posts.observe(viewLifecycleOwner) { posts ->
                    adapter.posts = posts?.sortedByDescending { it!!.likesCount } as List<Post>
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