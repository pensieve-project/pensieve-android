package ru.hse.pensieve

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.api.GreetingResponse
import ru.hse.pensieve.databinding.ActivityMainBinding
import ru.hse.pensieve.ui.authorization.RegistrationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.responseTextView.text = "Loading..."
        fetchGreeting()
    }

    private fun fetchGreeting() {
        Client.instance.getGreeting().enqueue(object : Callback<GreetingResponse> {
            override fun onResponse(call: Call<GreetingResponse>, response: Response<GreetingResponse>) {
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        goToRegistrationActivity()
                    }, 1000)
                } else {
                    showError("Failed to get response")
                }
            }

            override fun onFailure(call: Call<GreetingResponse>, t: Throwable) {
                showError("Error: ${t.localizedMessage}")
            }
        })
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