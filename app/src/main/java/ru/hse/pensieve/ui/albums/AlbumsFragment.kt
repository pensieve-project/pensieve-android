package ru.hse.pensieve.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.albums.AlbumsViewModel
import ru.hse.pensieve.databinding.FragmentAlbumsBinding

class AlbumsFragment : Fragment() {
    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AlbumsAdapter
    private val viewModel: AlbumsViewModel by activityViewModels()

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
        adapter = AlbumsAdapter(emptyList()) {
            // click
        }

        binding.albumsRecyclerView.adapter = adapter

        viewModel.albums.observe(viewLifecycleOwner, { users ->
            adapter.updateUsers(users ?: emptyList())
        })

        viewModel.getUserAlbums()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}