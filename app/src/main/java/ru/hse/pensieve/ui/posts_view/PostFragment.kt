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
import kotlinx.coroutines.launch
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentPostBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.room.repositories.PostRepository
import ru.hse.pensieve.room.repositories.UserRepository
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.room.entities.PostEntity
import ru.hse.pensieve.room.entities.User
import ru.hse.pensieve.themes.repository.ThemeRepository
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

    private val appDatabase by lazy { AppDatabase.getInstance(requireContext()) }
    private val postRepository by lazy { PostRepository(appDatabase.postDao()) }
    private val userRepository by lazy { UserRepository(appDatabase.userDao()) }

    private val profileRepository = ProfileRepository()
    private val themeRepository = ThemeRepository()

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
        lifecycleScope.launch {
            val cachedPost = postRepository.getPostById(postId)
            if (cachedPost != null) {
                viewModel.post.value = cacheToPost(cachedPost)
                val authorId = cachedPost.authorId
                val profile = profileRepository.getProfileByAuthorId(authorId)
                val username = profileRepository.getUsernameByAuthorId(authorId)
                userRepository.upsertUser(User(authorId, username, profile.description, profile.avatar))
                binding.username.text = username
                if (profile.avatar != null && profile.avatar.isNotEmpty()) {
                    binding.avatar.setImageBitmap(BitmapFactory.decodeByteArray(profile.avatar, 0, profile.avatar.size))
                }
                binding.theme.text = cachedPost.themeTitle
                val currentTime = System.currentTimeMillis()
                postRepository.updateLastAccessTime(postId, currentTime)
            } else {
                viewModel.getPostById(postId)
            }
            viewModel.post.observe(viewLifecycleOwner) { post ->
                if (post != null) {
                    lifecycleScope.launch {
                        try {
                            if (cachedPost == null) {
                                println("Post cached")
                                postRepository.insertPost(postToCache(post))
                            }
                        } catch (e: Exception) {
                            println("Error caching post: " + e)
                        }
                    }
                    viewModel.getAuthorData(post.authorId!!)
                    viewModel.authorUsername.observe(viewLifecycleOwner) { authorUsername ->
                        if (authorUsername != null && authorUsername.isNotEmpty()) {
                            binding.username.text = authorUsername
                        }
                    }
                    viewModel.authorAvatar.observe(viewLifecycleOwner) { authorAvatar ->
                        if (authorAvatar != null && authorAvatar.isNotEmpty()) {
                            binding.avatar.setImageBitmap(authorAvatar.toBitmap())
                        }
                    }
                    viewModel.getThemeTitle(post.themeId!!)
                    viewModel.themeTitle.observe(viewLifecycleOwner) { themeTitle ->
                        if (themeTitle != null && themeTitle.isNotEmpty()) {
                            binding.theme.text = themeTitle
                        }
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
                        val likeIconRes =
                            if (isLiked) R.drawable.likes_fill else R.drawable.likes
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
    }

    suspend private fun postToCache(post: Post): PostEntity {
        val themeTitle = themeRepository.getThemeTitle(post.themeId!!)
        println(themeTitle)
        return PostEntity(
            post.postId!!,
            post.themeId,
            themeTitle,
            post.authorId!!,
            post.photo,
            post.text,
            post.timeStamp!!,
            post.location,
            post.coAuthors,
            post.albumId,
            post.likesCount!!,
            post.commentsCount!!,
            false
        )
    }

    private fun cacheToPost(post: PostEntity): Post {
        return Post(
            post.postId,
            post.themeId,
            post.authorId,
            post.photo,
            post.text,
            post.timeStamp,
            post.location,
            post.coAuthors,
            post.albumId,
            post.likesCount,
            post.commentsCount
        )
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