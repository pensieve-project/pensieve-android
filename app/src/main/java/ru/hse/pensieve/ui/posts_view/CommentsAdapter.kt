package ru.hse.pensieve.ui.posts_view

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.R
import ru.hse.pensieve.posts.models.CommentWithAuthor
import ru.hse.pensieve.profiles.repository.ProfileRepository

class CommentsAdapter(private var comments: List<CommentWithAuthor>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    fun updateComments(newComments: List<CommentWithAuthor>) {
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
        fun bind(commentWithAuthor: CommentWithAuthor) {
            if (commentWithAuthor.authorPhoto != null && commentWithAuthor.authorPhoto.isNotEmpty()) {
                val avatarBitmap = BitmapFactory.decodeByteArray(
                    commentWithAuthor.authorPhoto,
                    0,
                    commentWithAuthor.authorPhoto.size
                )
                itemView.findViewById<ImageView>(R.id.avatar).setImageBitmap(avatarBitmap)
            }
            itemView.findViewById<TextView>(R.id.commentAuthor).text =
                commentWithAuthor.authorUsername
            itemView.findViewById<TextView>(R.id.commentText).text = commentWithAuthor.comment.text
        }
    }
}