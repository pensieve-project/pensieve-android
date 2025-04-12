package ru.hse.pensieve.ui.posts_view

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.posts.PostViewModel
import ru.hse.pensieve.posts.models.Post

class PostsAdapter(
    private val fragmentManager: FragmentManager,
    private val viewModel: PostViewModel
) : RecyclerView.Adapter<FragmentViewHolder>() {

    private var posts: List<Post> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentViewHolder {
        val post = posts[viewType]
        return FragmentViewHolder.create(parent, fragmentManager, post)
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = posts.size

    override fun getItemViewType(position: Int): Int = position

    fun submitList(newPosts: List<Post?>) {
        posts = newPosts as List<Post>
        notifyDataSetChanged()
    }
}