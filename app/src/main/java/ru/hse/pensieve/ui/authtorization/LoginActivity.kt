class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

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

        observeViewModel()
    }

    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
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