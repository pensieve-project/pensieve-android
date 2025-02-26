package ru.hse.pensieve

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.hse.pensieve.databinding.ActivityMainBinding
import ru.hse.pensieve.ui.authorization.RegistrationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.responseTextView.text = "Loading..."
        goToRegistrationActivity()
    }

    private fun goToRegistrationActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        binding.responseTextView.text = message
    }
}