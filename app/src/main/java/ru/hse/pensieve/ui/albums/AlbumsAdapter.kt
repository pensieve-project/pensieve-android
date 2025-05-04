package ru.hse.pensieve.ui.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.databinding.ItemAlbumBinding
import ru.hse.pensieve.search.models.User

class AlbumsAdapter(
    private var albums: List<Album>,
    private val onItemClick: (Album) -> Unit
) : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {

    override fun getItemCount(): Int = albums.size
    private var selectedPosition = RecyclerView.NO_POSITION

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

    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            binding.usernamesTextView.text = album.coAuthors.toString()
        }
    }
}