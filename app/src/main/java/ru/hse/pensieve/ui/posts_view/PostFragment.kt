package ru.hse.pensieve.ui.posts_view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentPostBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.repository.PostRepository
import ru.hse.pensieve.ui.profile.ProfileActivity
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID


class PostFragment : Fragment() {
    private val postRepository = PostRepository()
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val currentUserId = UserPreferences.getUserId()

    private val viewModel: PostViewModel by activityViewModels()

    private var postNumber: Int = 0

    companion object {
        private const val ARG_POST_NUMBER = "postNumber"

        fun newInstance(postNumber: Int): PostFragment {
            val fragment = PostFragment()
            val args = Bundle().apply {
                putInt(ARG_POST_NUMBER, postNumber)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postNumber = it.getInt(ARG_POST_NUMBER, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()

        var postId: UUID

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts != null) {
                binding.username.text = posts[postNumber]?.authorId.toString() // достать username
                binding.theme.text = posts[postNumber]?.themeId.toString() // достать тему
                val photoByteArray = posts[postNumber]?.photo
                val bitmap = photoByteArray?.toBitmap()
                binding.imgPhoto.setImageBitmap(bitmap)
                binding.description.text = posts[postNumber]?.text

                postId = posts[postNumber]?.postId!!
                val likesCount = posts[postNumber]?.likesCount
                binding.likesAndComments.likeCount.text = likesCount.toString()

                viewModel.loadCommentsCount(postId)

                viewModel.commentsCount.observe(viewLifecycleOwner) { commentsCount ->
                    binding.likesAndComments.commentCount.text = commentsCount.toString()
                }

                viewModel.loadLikeStatus(postId, currentUserId!!)

                viewModel.isLiked.observe(viewLifecycleOwner) { isLiked ->
                    val likeIconRes = if (isLiked) R.drawable.likes_fill else R.drawable.likes
                    binding.likesAndComments.likeIcon.setImageResource(likeIconRes)
                }

                viewModel.likesCount.observe(viewLifecycleOwner) { likesCount ->
                    binding.likesAndComments.likeCount.text = likesCount.toString()
                }

                binding.likesAndComments.likeIcon.setOnClickListener {
                    val isLiked = viewModel.isLiked.value ?: false
                    viewModel.toggleLike(postId, isLiked)
                }

                binding.likesAndComments.commentIcon.setOnClickListener {
                    showCommentsBottomSheet(postId)
                }
            } else {
                binding.description.text = "No posts found"
            }
        }
    }

    private fun showCommentsBottomSheet(postId: UUID) {
        CommentsFragment.newInstance(postId).show(
            parentFragmentManager,
            "comments_bottom_sheet"
        )
    }

    private fun ByteArray.toBitmap(): Bitmap? {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    private fun setupButtons() {
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnPrev.setOnClickListener {
            (requireActivity() as ProfileActivity).showGrid()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
