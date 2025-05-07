package ru.hse.pensieve.ui.postcreation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.FragmentResultBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
        setPhoto()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        } // ?

        binding.btnPrev.setOnClickListener {
            (requireActivity() as CreatePostActivity).prevStep()
        }

        binding.btnPublish.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createPost(uriToFile(viewModel.postPhoto.value!!))
                requireActivity().finish()
            }
        }
    }

    private fun updateNavigationButtons() {
        binding.btnPrev.visibility = View.VISIBLE
    }

    private fun fillTextLables() {
        binding.themeName.setText(viewModel.postThemeTitle.value)
        binding.description.setText(viewModel.postText.value)
        if (viewModel.postLocation.value != null) {
            binding.location.setText(viewModel.postLocation.value!!.placeName)
            binding.location.visibility = View.VISIBLE
        }
    }

    private fun setPhoto() {
        binding.imgPhoto.setImageURI(viewModel.postPhoto.value)
    }

    private fun uriToFile(uri: Uri): File {
        val file = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            println("Exception: " + e.message)
        }
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}