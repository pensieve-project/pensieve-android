package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.pensieve.databinding.FragmentThemesBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import ru.hse.pensieve.themes.ThemesViewModel
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.ui.themes.ThemeAdapter

class ThemesFragment : Fragment() {

    private var _binding: FragmentThemesBinding? = null
    private val binding get() = _binding!!

    private lateinit var themesViewModel: ThemesViewModel
    private lateinit var postViewModel: CreatePostViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ThemeAdapter

    private lateinit var selectedTheme: Theme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themesViewModel = ViewModelProvider(requireActivity()).get(ThemesViewModel::class.java)
        postViewModel = ViewModelProvider(requireActivity()).get(CreatePostViewModel::class.java)

        setupRecyclerView()
        setupButtons()
        updateNavigationButtons()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ThemeAdapter(emptyList()) { theme ->
            binding.btnNext.visibility = View.VISIBLE
            selectedTheme = theme
        }

        binding.recyclerView.adapter = adapter

        themesViewModel.themes.observe(viewLifecycleOwner, { themes ->
            if (themes != null) {
                adapter = ThemeAdapter(themes) { theme ->
                    binding.btnNext.visibility = View.VISIBLE
                    selectedTheme = theme
                }
                binding.recyclerView.adapter = adapter
            }
        })

        themesViewModel.getAllThemes()
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