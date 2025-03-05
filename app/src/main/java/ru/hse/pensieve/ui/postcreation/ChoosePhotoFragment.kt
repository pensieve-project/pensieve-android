package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.pensieve.databinding.FragmentChoosePhotoBinding
import ru.hse.pensieve.posts.CreatePostViewModel

class ChoosePhotoFragment : Fragment() {

    private var _binding: FragmentChoosePhotoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosePhotoBinding.inflate(inflater, container, false)
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
            // save photo in viewmodel
            (requireActivity() as CreatePostActivity).nextStep()
        }

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).prevStep()
        }

        binding.btnChoosePhoto.setOnClickListener {
            viewModel.postPhotoChosen.value = true
            binding.btnNext.visibility = View.VISIBLE
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = if (viewModel.postPhotoChosen.value == true) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}