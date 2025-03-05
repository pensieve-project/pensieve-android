package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.pensieve.databinding.FragmentChooseThemeBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import ru.hse.pensieve.themes.ThemesViewModel

class ChooseThemeFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var themeViewModel: ThemesViewModel

    private var _binding: FragmentChooseThemeBinding? = null
    private val binding get() = _binding!!

    private var isUserInput = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CreatePostViewModel::class.java)
        themeViewModel = ViewModelProvider(requireActivity()).get(ThemesViewModel::class.java)

        isUserInput = false
        binding.edittextNewTheme.text.clear()
        binding.edittextNewTheme.post {
            binding.edittextNewTheme.setText(viewModel.postThemeTitle.value)
        }
        binding.edittextNewTheme.post {
            isUserInput = true
        }

        setupButtons()
        updateNavigationButtons()
        setupEditTextListener()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            binding.edittextNewTheme.setText(viewModel.postThemeTitle.value, TextView.BufferType.EDITABLE)
            requireActivity().finish()
        } // ?

        binding.btnNext.setOnClickListener {
            viewModel.postThemeTitle.value = binding.edittextNewTheme.text.toString()
            println(viewModel.postTheme.value)
            println(viewModel.postThemeTitle.value)
            (requireActivity() as CreatePostActivity).nextStep()
        }

        binding.btnChooseTheme.setOnClickListener {
            (requireActivity() as CreatePostActivity).goToStep(ThemesFragment())
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.GONE
        binding.btnNext.visibility = if (binding.edittextNewTheme.text.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupEditTextListener() {
        binding.edittextNewTheme.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnNext.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (isUserInput) {
                    viewModel.postTheme.value = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}