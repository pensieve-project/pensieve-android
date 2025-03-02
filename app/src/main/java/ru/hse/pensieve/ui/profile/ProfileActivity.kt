package ru.hse.pensieve.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.hse.pensieve.databinding.ActivityProfileBinding

class ProfileActivity :  AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
    }
}