package ru.hse.pensieve.ui.postcreation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.hse.pensieve.databinding.ActivityCreatePostBinding
import ru.hse.pensieve.posts.CreatePostViewModel
import ru.hse.pensieve.ui.map.LocationSearchFragment

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private val postViewModel: CreatePostViewModel by viewModels()

    private val fragmentSteps: List<Fragment> = listOf(
        ChooseThemeFragment(),
        ChoosePhotoFragment(),
        CreateDescriptionFragment(),
        LocationSearchFragment(),
        ResultFragment()
    )

    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            goToStep(0)
        }
    }

    fun goToStep(nextFragment: Fragment) {
        val step = fragmentSteps.indexOf(nextFragment)
        if (step != -1) {
            currentStep = step
        }

        supportFragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainer.id,
                nextFragment
            )
            .addToBackStack(null)
            .commit()
    }

    fun goToStep(step: Int) {
        if (step in fragmentSteps.indices) {
            currentStep = step
            goToStep(fragmentSteps[step])
        }
    }

    fun nextStep() {
        goToStep(currentStep + 1)
    }

    fun prevStep() {
        goToStep(currentStep - 1)
    }

    fun goToChooseThemeFragment() {
        goToStep(0)
    }
}