package ru.hse.pensieve.ui.posts_view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentPostBinding
import ru.hse.pensieve.posts.PostViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID


class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var postId: UUID

    private val viewModel: PostViewModel by activityViewModels()

    companion object {
        private const val ARG_POST_ID = "post_id"

        fun newInstance(postId: UUID): PostFragment {
            return PostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_ID, postId.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = UUID.fromString(requireArguments().getString(ARG_POST_ID)!!)
        println("Opening " + postId)
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
        viewModel.getPostById(postId)
        viewModel.post.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                // binding.avatar.
                viewModel.getAuthorUsername(post.authorId!!)
                viewModel.authorUsername.observe(viewLifecycleOwner) {
                        authorUsername -> binding.username.text = authorUsername
                }
                viewModel.getThemeTitle(post.themeId!!)
                viewModel.themeTitle.observe(viewLifecycleOwner) {
                        themeTitle -> binding.theme.text = themeTitle
                }
                if (post.location != null) {
                    binding.location.text = post.location.placeName
                    binding.location.visibility = View.VISIBLE
                }

                viewModel.loadCoAuthorsUsernames(post.coAuthors ?: emptySet())
                viewModel.coAuthorsUsernames.observe(viewLifecycleOwner) { names ->
                    if (names.isNotEmpty()) {
                        binding.coAuthors.text = names.joinToString(", ")
                        binding.coAuthors.visibility = View.VISIBLE
                    } else {
                        binding.coAuthors.text = ""
                        binding.coAuthors.visibility = View.GONE
                    }
                }

                val photoByteArray = post.photo
                val bitmap = photoByteArray?.toBitmap()
                binding.imgPhoto.setImageBitmap(bitmap)
                binding.description.text = post.text
                binding.date.text = formatInstant(post.timeStamp!!)

                viewModel.loadLikesCount(postId)
                viewModel.likesCount.observe(viewLifecycleOwner) { likesCount ->
                    binding.likesAndComments.likeCount.text = likesCount.toString()
                }

                viewModel.loadCommentsCount(postId)
                viewModel.commentsCount.observe(viewLifecycleOwner) { commentsCount ->
                    binding.likesAndComments.commentCount.text = commentsCount.toString()
                }

                viewModel.loadLikeStatus(postId)

                viewModel.isLiked.observe(viewLifecycleOwner) { isLiked ->
                    val likeIconRes = if (isLiked) R.drawable.likes_fill else R.drawable.likes
                    binding.likesAndComments.likeIcon.setImageResource(likeIconRes)
                }

                binding.likesAndComments.likeIcon.setOnClickListener {
                    val isLiked = viewModel.isLiked.value ?: false
                    viewModel.toggleLike(postId, isLiked)
                }

                binding.likesAndComments.commentIcon.setOnClickListener {
                    showCommentsBottomSheet(postId)
                }

            } else {
                println("No posts found")
                binding.description.text = "No posts found"
            }
        }
    }

    private fun formatInstant(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}