package ru.hse.pensieve.ui.posts_view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.R
import ru.hse.pensieve.posts.models.Post
import java.util.UUID

class ImageAdapter(
    var posts: List<Post>,
    private val onItemClick: (UUID) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val post = posts[position]
        holder.imageView.setImageBitmap(post.photo?.toBitmap())

        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageWidth = screenWidth / 3

        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = imageWidth
        holder.itemView.layoutParams = layoutParams

        holder.itemView.setOnClickListener {
            println("Clicked on image at position $position " + post.postId!!)
            onItemClick(post.postId)
        }
    }

    private fun ByteArray.toBitmap(): Bitmap? {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun getItemCount(): Int = posts.size
}