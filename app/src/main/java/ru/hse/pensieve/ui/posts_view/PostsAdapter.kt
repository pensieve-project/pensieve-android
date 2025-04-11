package ru.hse.pensieve.ui.posts_view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentPostBinding
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.models.Post

class PostsAdapter(private val viewModel: PostViewModel, private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    var posts: List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class PostViewHolder(val binding: FragmentPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = FragmentPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            username.text = post.authorId.toString()
            viewModel.getThemeTitle(post.postId!!)
            viewModel.themeTitle.observe(lifecycleOwner) {
                    themeTitle -> theme.text = themeTitle
            }
            description.text = post.text
            imgPhoto.setImageBitmap(post.photo?.toBitmap())

            val likesCount = post.likesCount
            val commentsCount = post.commentsCount
            likesAndComments.likeCount.text = likesCount.toString()
            likesAndComments.commentCount.text = commentsCount.toString()
        }
    }

    private fun ByteArray.toBitmap(): Bitmap? {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun getItemCount() = posts.size
}