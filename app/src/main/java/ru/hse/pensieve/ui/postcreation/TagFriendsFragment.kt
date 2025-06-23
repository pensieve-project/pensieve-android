package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentTagFriendsBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.ui.users_search.UsersSearchFragment
import java.util.UUID

class TagFriendsFragment : Fragment() {

    private var _binding: FragmentTagFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var searchViewModel: SearchViewModel

    private lateinit var usersSearchFragment: UsersSearchFragment

    private var coAuthors: Set<UUID> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CreatePostViewModel::class.java]
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        usersSearchFragment = UsersSearchFragment.newInstance({
            users -> coAuthors = users.map { it.userId!! }.toSet()
            println(users)
        }, true)

        childFragmentManager.beginTransaction()
            .replace(R.id.usersSearchContainer, usersSearchFragment)
            .commit()

        setupButtons()
        updateNavigationButtons()
        setupSearchView()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        } // ?

        binding.btnNext.setOnClickListener {
            viewModel.postCoAuthors.value = coAuthors
            (requireActivity() as CreatePostActivity).nextStep()
        }

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).prevStep()
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchViewModel.searchUsers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    return true
                }
                searchViewModel.searchUsers(newText)
                return true
            }
        })

        searchView.setOnCloseListener {
            binding.searchView.clearFocus()
            searchViewModel.clearUsersSearch()
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}