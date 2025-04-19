package ru.hse.pensieve.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.databinding.FragmentUsersSearchBinding
import ru.hse.pensieve.search.SearchViewModel

class UsersSearchFragment : Fragment() {
    private var _binding: FragmentUsersSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UsersAdapter
    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UsersAdapter(emptyList()) {
            // click
        }

        binding.usersRecyclerView.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner, { users ->
            adapter.updateUsers(users ?: emptyList())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = UsersSearchFragment()
    }
}