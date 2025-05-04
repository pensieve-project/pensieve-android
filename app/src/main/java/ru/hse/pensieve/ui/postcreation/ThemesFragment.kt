package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.pensieve.databinding.FragmentThemesBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.ui.themes.ThemeAdapter

class ThemesFragment : Fragment() {

    private var _binding: FragmentThemesBinding? = null
    private val binding get() = _binding!!

    private lateinit var themesViewModel: ThemesViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var postViewModel: CreatePostViewModel
    private lateinit var adapter: ThemeAdapter

    private lateinit var selectedTheme: Theme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themesViewModel = ViewModelProvider(requireActivity())[ThemesViewModel::class.java]
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        postViewModel = ViewModelProvider(requireActivity())[CreatePostViewModel::class.java]

        setupRecyclerView()
        setupSearchView()
        setupButtons()
        updateNavigationButtons()
        setupObservers()

        searchViewModel.getAllThemes()
        themesViewModel.getLikedThemes()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ThemeAdapter(
            themes = emptyList(),
            onItemClick = { theme ->
                binding.btnNext.visibility = View.VISIBLE
                selectedTheme = theme
            },
            onLikeClick = { themeId, isLiked ->
                if (!isLiked) {
                    val currentLiked = adapter.currentLikedThemes - themeId
                    adapter.updateData(newLikedThemes = currentLiked)
                }
                themesViewModel.toggleLike(themeId, isLiked)
            },
            likedThemes = emptySet(),
            authorUsernames = emptyMap()
        )

        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        themesViewModel.themes.observe(viewLifecycleOwner) { themes ->
            themes?.let {
                val authorIds = it.mapNotNull { theme -> theme.authorId }.toSet()
                if (authorIds.isNotEmpty()) {
                    themesViewModel.loadAuthorUsernames(authorIds)
                }
                adapter.updateData(newThemes = it)
            }
        }

        themesViewModel.authorUsernames.observe(viewLifecycleOwner) { usernamesMap ->
            usernamesMap?.let {
                adapter.updateData(newAuthorUsernames = usernamesMap)
            }
        }

        themesViewModel.likedThemes.observe(viewLifecycleOwner) { likedThemes ->
            likedThemes?.let {
                val likedThemeIds = it.mapNotNull { theme -> theme.themeId }.toSet()
                adapter.updateData(newLikedThemes = likedThemeIds)
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchThemes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchViewModel.searchThemes(it)
                }
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            searchViewModel.getAllThemes()
            false
        }
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        } // ?

        binding.btnNext.setOnClickListener {
            postViewModel.postTheme.value = selectedTheme
            postViewModel.postThemeTitle.value = selectedTheme.title
            (requireActivity() as CreatePostActivity).goToChooseThemeFragment()
        }

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).goToChooseThemeFragment()
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}