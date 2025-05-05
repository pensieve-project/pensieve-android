package ru.hse.pensieve.ui.albums

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.R
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.databinding.ItemAlbumBinding
import ru.hse.pensieve.search.models.User
import java.util.UUID

class AlbumsAdapter(
    private var albums: List<Album>,
    private val onItemClick: (Album) -> Unit,
    private val getUserNames: (Album) -> String,
    private val getAlbumAvatar: (Album) -> ByteArray?
) : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {

    override fun getItemCount(): Int = albums.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAlbumBinding.inflate(inflater, parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)

        holder.itemView.setOnClickListener {
            onItemClick(album)
        }
    }

    fun updateUsers(newAlbums: List<Album>) {
        albums = newAlbums
        notifyDataSetChanged()
    }

    inner class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            binding.usernamesTextView.text = getUserNames(album)
            val avatar = getAlbumAvatar(album)
            if (avatar == null) {
                binding.avatarImageView.setImageResource(R.drawable.default_avatar)
            } else {
                val avatarBitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                binding.avatarImageView.setImageBitmap(avatarBitmap)
            }
        }
    }
}