package ru.hse.pensieve.ui.posts_view

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.R

class ImageAdapter(
    var images: List<Bitmap>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.imageView.setImageBitmap(image)

        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageWidth = screenWidth / 3

        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = imageWidth
        holder.itemView.layoutParams = layoutParams

        holder.itemView.setOnClickListener {
            println("Clicked on image at position $position")
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = images.size
}