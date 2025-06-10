package ru.hse.pensieve.ui.authorization

import ru.hse.pensieve.authentication.AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.hse.pensieve.databinding.ActivityLoginBinding
import ru.hse.pensieve.room.repositories.UserRepository
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.ui.feed.PopularPostsFeedActivity
import ru.hse.pensieve.utils.UserPreferences
import ru.hse.pensieve.utils.ValidationOfInput

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val userRepository by lazy { UserRepository(appDatabase.userDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                authViewModel.login(email, password)
            }
        }

        binding.goToRegistrationText.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        observeViewModel()
    }

    private fun validateInput(email: String, password: String) : Boolean {
        when {
            email.isEmpty() -> {
                binding.emailEditText.error = "Email field can not be empty"
            }
            !ValidationOfInput.validateEmail(email) -> {
                binding.emailEditText.error = "Please enter a valid email address"
            }
            else -> binding.emailEditText.error = null
        }

        when {
            password.isEmpty() -> {
                binding.passwordEditText.error = "Password field can not be empty"
            }
            !ValidationOfInput.validatePassword(password) -> {
                binding.passwordEditText.error = "Password must be at least 8 characters long and contain at least one digit and one letter"
            }
            else -> binding.passwordEditText.error = null
        }
        return binding.emailEditText.error == null && binding.passwordEditText.error == null
    }

    private fun observeViewModel() {
        authViewModel.user.observe(this, { user ->
            if (user != null) {
                UserPreferences.saveUserId(user.id!!)
                UserPreferences.saveUserUsername(user.id, user.username!!)
                lifecycleScope.launch {
                    userRepository.currentUserId = user.id
                }
                startActivity(Intent(this, PopularPostsFeedActivity::class.java))
                finish()
            }
        })

        // TODO: ошибки: email не найден, неверный пароль

        authViewModel.error.observe(this, { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }
}