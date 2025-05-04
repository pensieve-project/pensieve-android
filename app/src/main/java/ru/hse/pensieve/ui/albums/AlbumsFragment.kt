package ru.hse.pensieve.ui.albums

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
import ru.hse.pensieve.databinding.FragmentAlbumsBinding
import ru.hse.pensieve.profiles.ProfileViewModel
import java.util.UUID

class AlbumsFragment : Fragment() {
    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AlbumsAdapter
    private val viewModel: AlbumsViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var usernameCache = mutableMapOf<UUID, String>()

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
        adapter = AlbumsAdapter(
            emptyList(),
            {},
            { coAuthorIds ->
                coAuthorIds?.joinToString(", ") { id ->
                    usernameCache[id] ?: "Unknown"
                } ?: ""
            }
        )

        binding.albumsRecyclerView.adapter = adapter

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            lifecycleScope.launch {
                val allIds = albums
                    ?.flatMap { it.coAuthors ?: emptySet() }
                    ?.toSet()
                    ?: emptySet()

                allIds.map { id ->
                    async {
                        id to profileViewModel.getUsername(id)
                    }
                }.awaitAll().forEach { (id, name) ->
                    usernameCache[id] = name
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
}