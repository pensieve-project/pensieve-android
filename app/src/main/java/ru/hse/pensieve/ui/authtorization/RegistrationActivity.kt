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
                authViewModel.register(email, username, password)
            }
        }

        observeViewModel()
    }

    private fun validateInput(email: String, username: String, password: String): Boolean {
        return email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()
    }

    private fun observeViewModel() {
        authViewModel.user.observe(this, { user ->
            if (user != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })

        authViewModel.error.observe(this, { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }
}