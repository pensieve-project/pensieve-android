package ru.hse.pensieve.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.pensieve.R
import ru.hse.pensieve.databinding.FragmentEditProfileBinding
import ru.hse.pensieve.profiles.EditProfileViewModel
import ru.hse.pensieve.profiles.repository.ProfileRepository
import ru.hse.pensieve.room.repositories.UserRepository
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.room.entities.User
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import ru.hse.pensieve.utils.UserPreferences

class EditProfileFragment: Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditProfileViewModel
    private val profileRepository = ProfileRepository()
    private val userId = UserPreferences.getUserId()
    private val appDatabase by lazy { AppDatabase.getInstance(requireContext()) }
    private val userRepository by lazy { UserRepository(appDatabase.userDao()) }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.avatar.setImageURI(imageUri)
                viewModel.avatar.value = imageUri
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = EditProfileViewModel(userRepository)
        setupButtons()
        loadCurrentProfile()
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            (activity as? ProfileActivity)?.hideFullscreenContainer()
        }

        binding.avatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.description.value = binding.description.text.toString()
                val currentProfile = withContext(Dispatchers.IO) {
                    profileRepository.getProfileByAuthorId(userId!!)
                }
                val avatarUri = when {
                    viewModel.avatar.value != null -> viewModel.avatar.value
                    currentProfile.avatar != null && currentProfile.avatar.isNotEmpty() -> {
                        val tempFile = File.createTempFile("current_avatar", ".jpg", requireContext().cacheDir)
                        FileOutputStream(tempFile).use { fos ->
                            fos.write(currentProfile.avatar)
                        }
                        Uri.fromFile(tempFile)
                    }
                    else -> null
                }
                viewModel.editProfile(uriToFile(avatarUri!!))
                activity?.supportFragmentManager?.popBackStack()
                (activity as? ProfileActivity)?.run {
                    loadProfile(userId!!)
                    hideFullscreenContainer()
                }
            }
        }
    }

    private fun loadCurrentProfile() {
        lifecycleScope.launch {
            try {
                val cachedProfile = userRepository.getUserById(userId!!)
                if (cachedProfile == null) {
                    val profile = withContext(Dispatchers.IO) {
                        profileRepository.getProfileByAuthorId(userId)
                    }
                    userRepository.insertUser(User(userId, UserPreferences.getUsername(userId)!!, profile.description, profile.avatar))
                    println("Added " + userRepository.getUsernameById(userId))
                    binding.description.setText(profile.description)
                    if (profile.avatar == null || profile.avatar.isEmpty()) {
                        binding.avatar.setImageResource(R.drawable.default_avatar)
                    } else {
                        val avatarBitmap = BitmapFactory.decodeByteArray(profile.avatar, 0, profile.avatar.size)
                        binding.avatar.setImageBitmap(avatarBitmap)
                    }
                }
                else {
                    println("Add from cache")
                    binding.description.setText(cachedProfile.description)
                    if (cachedProfile.avatar == null || cachedProfile.avatar!!.isEmpty()) {
                        binding.avatar.setImageResource(R.drawable.default_avatar)
                    } else {
                        val avatarBitmap =
                            BitmapFactory.decodeByteArray(cachedProfile.avatar, 0, cachedProfile.avatar!!.size)
                        binding.avatar.setImageBitmap(avatarBitmap)
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
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
}