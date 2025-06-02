package ru.hse.pensieve.ui.albums

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import ru.hse.pensieve.albums.AlbumsViewModel
import ru.hse.pensieve.albums.models.Album
import ru.hse.pensieve.databinding.FragmentAlbumsBinding
import ru.hse.pensieve.profiles.ProfileViewModel
import ru.hse.pensieve.ui.profile.SubscriptionType
import ru.hse.pensieve.ui.profile.SubscriptionsFragment
import java.util.UUID

class AlbumsFragment : Fragment() {
    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AlbumsAdapter
    private val viewModel: AlbumsViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var usernameCache = mutableMapOf<UUID, String>()
    private var avatarsCache = mutableMapOf<UUID, ByteArray>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.albumsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val getUserNames: (Album) -> String = { album ->
            album.coAuthors?.joinToString(", ") { id ->
                usernameCache[id] ?: "Unknown"
            } ?: ""
        }
        val getAlbumAvatar: (Album) -> ByteArray? = { album -> avatarsCache[album.albumId] }
        val onItemClick: (Album) -> Unit = { album ->
            Intent(requireActivity(), AlbumActivity::class.java).apply {
                putExtra("ALBUM_ID", album.albumId.toString())
                putExtra("ALBUM_AVATAR", album.avatar)
                putStringArrayListExtra("CO_AUTHORS", ArrayList(album.coAuthors?.map { it.toString() } ?: emptyList()))
                startActivity(this)
            }
        }

        adapter = AlbumsAdapter(emptyList(), onItemClick, getUserNames, getAlbumAvatar)

        binding.albumsRecyclerView.adapter = adapter

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            lifecycleScope.launch {
                albums.map { album ->
                    async {
                        album.coAuthors!!.map { coAuthorId ->
                            async {
                                coAuthorId to profileViewModel.getUsername(coAuthorId)
                            }
                        }.awaitAll().forEach { (id, username) ->
                            usernameCache[id] = username
                        }
                        album to album.avatar
                    }
                }.awaitAll().forEach { (album, data) ->
                    data?.let { avatar ->
                        avatarsCache[album.albumId!!] = avatar
                    }
                }

                adapter.updateUsers(albums ?: emptyList())
            }
        }

        viewModel.getUserAlbums()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: UUID) = AlbumsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_USER_ID, userId)
            }
        }
    }
}