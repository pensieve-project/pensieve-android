package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.pensieve.databinding.FragmentResultBinding
import ru.hse.pensieve.posts.CreatePostViewModel

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CreatePostViewModel::class.java)

        setupButtons()
        updateNavigationButtons()
        fillTextLables()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        } // ?

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).prevStep()
        }

        binding.btnPublish.setOnClickListener {
            viewModel.createPost()
            requireActivity().finish()
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
    }

    private fun fillTextLables() {
        binding.themeName.setText(viewModel.postThemeTitle.value)
        binding.description.setText(viewModel.postText.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}