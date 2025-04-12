package ru.hse.pensieve.ui.posts_view

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.posts.models.Post

class FragmentViewHolder(
    val container: FrameLayout,
    val fragment: Fragment
) : RecyclerView.ViewHolder(container) {
    companion object {
        fun create(
            parent: ViewGroup,
            fragmentManager: FragmentManager,
            post: Post
        ): FragmentViewHolder {
            val container = FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            val fragment = PostFragment.newInstance(post.postId!!)
            return FragmentViewHolder(container, fragment)
        }
    }
}