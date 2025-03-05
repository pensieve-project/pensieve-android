package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.pensieve.databinding.FragmentCreateDescriptionBinding
import ru.hse.pensieve.posts.CreatePostViewModel

class CreateDescriptionFragment : Fragment() {

    private var _binding: FragmentCreateDescriptionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CreatePostViewModel::class.java)

        setupButtons()
        updateNavigationButtons()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        } // ?

        binding.btnNext.setOnClickListener {
            viewModel.postText.value = binding.description.text.toString()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}