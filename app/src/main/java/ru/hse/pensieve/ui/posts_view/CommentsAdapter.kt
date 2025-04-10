package ru.hse.pensieve.ui.posts_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.R
import ru.hse.pensieve.posts.models.Comment

class CommentsAdapter(private var comments: List<Comment?>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    fun updateComments(newComments: List<Comment?>) {
        this.comments = newComments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        println(comments.size)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        comments[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: Comment) {
            itemView.findViewById<TextView>(R.id.commentAuthor).text = comment.authorId.toString()
            itemView.findViewById<TextView>(R.id.commentText).text = comment.text
        }
    }
}