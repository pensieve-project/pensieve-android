package ru.hse.pensieve.ui.users_search

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
import ru.hse.pensieve.databinding.FragmentUsersSearchBinding
import ru.hse.pensieve.profiles.ProfileViewModel
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.search.models.User
import java.util.UUID

class UsersSearchFragment(
    private val onItemClick: (Set<User>) -> Unit,
    private val isMultiSelectMode: Boolean
) : Fragment() {
    private var _binding: FragmentUsersSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: UsersAdapter
    private val viewModel: SearchViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var usernameCache = mutableMapOf<UUID, String>()
    private var avatarsCache = mutableMapOf<UUID, ByteArray>()

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

        val getUsername: (User) -> String = { user -> usernameCache[user.userId] ?: "unknown" }
        val getUserAvatar: (User) -> ByteArray? = { user -> avatarsCache[user.userId] }

        adapter = UsersAdapter(emptyList(), onItemClick, isMultiSelectMode, getUsername, getUserAvatar)

        binding.usersRecyclerView.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner, { users ->
            lifecycleScope.launch {
                val allIds = users?.map { it.userId!! } ?: emptyList()

                allIds.map { id ->
                    async {
                        val name = profileViewModel.getUsername(id)
                        val avatar = profileViewModel.getAvatar(id)
                        id to Pair(name, avatar)
                    }
                }.awaitAll().forEach { (id, data) ->
                    usernameCache[id] = data.first
                    data.second?.let { avatar ->
                        avatarsCache[id] = avatar
                    }
                }

                adapter.updateUsers(users ?: emptyList())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(onItemClick: (Set<User>) -> Unit, isMultiSelectMode: Boolean) = UsersSearchFragment(onItemClick, isMultiSelectMode)
    }
}