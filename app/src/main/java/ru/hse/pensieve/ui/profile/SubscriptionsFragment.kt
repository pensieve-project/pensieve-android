package ru.hse.pensieve.ui.profile

import android.content.Context
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
import ru.hse.pensieve.databinding.FragmentSubscriptionsBinding
import ru.hse.pensieve.profiles.ProfileViewModel
import ru.hse.pensieve.search.models.User
import ru.hse.pensieve.subscriptions.SubscriptionsViewModel
import ru.hse.pensieve.ui.users_search.UsersAdapter
import java.util.UUID

enum class SubscriptionType : java.io.Serializable {
    FOLLOWERS, FOLLOWINGS
}

class SubscriptionsFragment : Fragment() {
    private var _binding: FragmentSubscriptionsBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: UsersAdapter
    private val viewModel: SubscriptionsViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var profileContainer: ProfileContainer? = null

    private lateinit var userId: UUID
    private lateinit var subscriptionType: SubscriptionType

    private var usernameCache = mutableMapOf<UUID, String>()
    private var avatarsCache = mutableMapOf<UUID, ByteArray>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubscriptionsBinding.inflate(inflater, container, false)
        userId = requireArguments().getSerializable(ARG_USER_ID, UUID::class.java)
            ?: throw IllegalArgumentException("User ID must be provided and must be UUID")
        subscriptionType = requireArguments().getSerializable(ARG_TYPE, SubscriptionType::class.java)
            ?: throw IllegalArgumentException("Subscription type must be provided")
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileContainer = context as? ProfileContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val getUsername: (User) -> String = { user -> usernameCache[user.userId] ?: "unknown" }
        val getUserAvatar: (User) -> ByteArray? = { user -> avatarsCache[user.userId] }

        adapter = UsersAdapter(emptyList(), { selectedUsers ->
            val user = selectedUsers.lastOrNull()
            if (user != null) {
                val intent = Intent(requireContext(), ForeignProfileActivity::class.java)
                intent.putExtra("USER_ID", user.userId.toString())
                startActivity(intent)
            }
        }, false, getUsername, getUserAvatar)

        binding.usersRecyclerView.adapter = adapter

        val liveData = when (subscriptionType) {
            SubscriptionType.FOLLOWERS -> viewModel.followers
            SubscriptionType.FOLLOWINGS -> viewModel.followings
        }

        liveData.observe(viewLifecycleOwner, { users ->
            lifecycleScope.launch {
                users.map { user ->
                    async {
                        val name = profileViewModel.getUsername(user.userId!!)
                        val avatar = profileViewModel.getAvatar(user.userId)
                        user to Pair(name, avatar)
                    }
                }.awaitAll().forEach { (user, data) ->
                    usernameCache[user.userId!!] = data.first
                    data.second?.let { avatar ->
                        avatarsCache[user.userId] = avatar
                    }
                }

                adapter.updateUsers(users ?: emptyList())
            }
        })

        if (subscriptionType == SubscriptionType.FOLLOWERS) {
            viewModel.getFollowers(userId)
        } else {
            viewModel.getFollowings(userId)
        }

    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            profileContainer?.hideFullscreenContainer()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        profileContainer = null
        super.onDetach()
    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        private const val ARG_TYPE = "type"

        fun newInstance(userId: UUID, type: SubscriptionType) = SubscriptionsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_USER_ID, userId)
                putSerializable(ARG_TYPE, type)
            }
        }
    }
}