package ru.hse.pensieve.ui.authorization

import ru.hse.pensieve.viewmodels.AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.hse.pensieve.MainActivity
import ru.hse.pensieve.databinding.ActivityRegistrationBinding
import ru.hse.pensieve.utils.Hashing

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, username, password)) {
                val hashedPassword = Hashing.hashWithSha256(password)
                authViewModel.register(email, username, hashedPassword)
            }
        }
        binding.goToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        observeViewModel()
    }

    private fun validateInput(email: String, username: String, password: String): Boolean {
        return email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()
    }

    private fun observeViewModel() {
        authViewModel.user.observe(this, { user ->
            if (user != null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })

        authViewModel.error.observe(this, { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }
}