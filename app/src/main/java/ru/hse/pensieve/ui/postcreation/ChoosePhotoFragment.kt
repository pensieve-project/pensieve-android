package ru.hse.pensieve.ui.postcreation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.pensieve.databinding.FragmentChoosePhotoBinding
import ru.hse.pensieve.posts.CreatePostViewModel

class ChoosePhotoFragment : Fragment() {

    private var _binding: FragmentChoosePhotoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreatePostViewModel

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.imgPhoto.setImageURI(imageUri)
                binding.imgPhoto.visibility = View.VISIBLE
                binding.imgCameraIcon.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                viewModel.postPhoto.value = imageUri
            }
        }
    }

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
            (requireActivity() as CreatePostActivity).nextStep()
        }

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).prevStep()
        }

        binding.btnChoosePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = if (viewModel.postPhoto.value != null) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}