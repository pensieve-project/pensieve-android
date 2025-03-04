package ru.hse.pensieve.ui.authorization

import ru.hse.pensieve.authentication.AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.hse.pensieve.databinding.ActivityRegistrationBinding
import ru.hse.pensieve.ui.profile.ProfileActivity
import ru.hse.pensieve.utils.Hashing
import ru.hse.pensieve.utils.ValidationOfInput

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(username, email, password)) {
                val hashedPassword = Hashing.hashWithSha256(password)
                authViewModel.register(username, email, hashedPassword)
            }
        }

        binding.goToLoginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        observeViewModel()
    }

    private fun validateInput(username: String, email: String, password: String) : Boolean {
        when {
            username.isEmpty() -> {
                binding.usernameEditText.error = "Username field can not be empty"
            }
            !ValidationOfInput.validateUsername(username) -> {
                binding.usernameEditText.error = "Username must be at least 3 characters long"
            }
            else -> binding.usernameEditText.error = null
        }

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
        return binding.usernameEditText.error == null && binding.emailEditText.error == null && binding.passwordEditText.error == null
    }

    private fun observeViewModel() {
        authViewModel.user.observe(this, { user ->
            if (user != null) {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
        })

        // TODO: ошибки: username уже используется, email уже используется

        authViewModel.error.observe(this, { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }
}