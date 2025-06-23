package ru.hse.pensieve.ui.themes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.FragmentThemesSearchBinding
import ru.hse.pensieve.search.SearchViewModel
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.themes.models.Theme
import java.util.UUID

class ThemesSearchFragment (
    private val onItemClick: (Theme) -> Unit
) : Fragment() {
    private var _binding: FragmentThemesSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: ThemeAdapter
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val themesViewModel: ThemesViewModel by activityViewModels()

    private var authorUsernameCache = mutableMapOf<UUID, String>()
    private var likedThemesCache = emptySet<UUID>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentThemesSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.themesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ThemeAdapter(
            themes = emptyList(),
            onItemClick = { theme -> onItemClick(theme) },
            onLikeClick = { themeId, isLiked ->
                themesViewModel.toggleLike(themeId, isLiked)
                if (isLiked) {
                    likedThemesCache = likedThemesCache - themeId
                } else {
                    likedThemesCache = likedThemesCache + themeId
                }
                adapter.updateLikedThemes(likedThemesCache)
            },
            likedThemes = likedThemesCache,
            authorUsernames = authorUsernameCache
        )

        binding.themesRecyclerView.adapter = adapter

        searchViewModel.themes.observe(viewLifecycleOwner) { themes ->
            if (themes != null) {
                val authorIds = themes.mapNotNull { it.authorId }.toSet()
                if (authorIds.isNotEmpty()) {
                    themesViewModel.loadAuthorUsernames(authorIds)
                }
                lifecycleScope.launch {
                    authorIds.map { id ->
                        async {
                            val name = getCachedOrLoadUsername(id)
                            id to name
                        }
                    }.awaitAll().forEach { (id, name) ->
                        authorUsernameCache[id] = name
                    }
                    adapter.updateData(
                        newThemes = themes,
                        newAuthorUsernames = authorUsernameCache
                    )
                }
            }
        }

        themesViewModel.likedThemes.observe(viewLifecycleOwner) { likedThemes ->
            likedThemesCache = likedThemes?.mapNotNull { it.themeId }?.toSet() ?: emptySet()
            adapter.updateLikedThemes(likedThemesCache)
        }

        themesViewModel.authorUsernames.observe(viewLifecycleOwner) { usernamesMap ->
            authorUsernameCache.putAll(usernamesMap ?: emptyMap())
            adapter.updateAuthorUsernames(authorUsernameCache)
        }
    }

    private fun getCachedOrLoadUsername(authorId: UUID): String {
        return authorUsernameCache[authorId]
            ?: themesViewModel.authorUsernames.value?.get(authorId)
            ?: "Unknown"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(onItemClick: (Theme) -> Unit) =
            ThemesSearchFragment(onItemClick)
    }
}
