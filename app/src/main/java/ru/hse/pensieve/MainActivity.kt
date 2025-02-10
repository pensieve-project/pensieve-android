package ru.hse.pensieve

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hse.pensieve.api.*

class MainActivity : AppCompatActivity() {
    private lateinit var responseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        responseTextView = findViewById(R.id.responseTextView)
        fetchGreeting()
    }

    private fun fetchGreeting() {
        Client.instance.getGreeting().enqueue(object : Callback<GreetingResponse> {
            override fun onResponse(call: Call<GreetingResponse>, response: Response<GreetingResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()?.message ?: "No response body"
                    responseTextView.text = body
                } else {
                    responseTextView.text = "Failed to get response"
                }
            }

            override fun onFailure(call: Call<GreetingResponse>, t: Throwable) {
                responseTextView.text = "Error: ${t.localizedMessage}"
            }
        })
    }
}
