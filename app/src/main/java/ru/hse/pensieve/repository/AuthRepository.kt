interface AuthRepository {
    suspend fun login(request: LoginRequest): User
    suspend fun register(request: RegistrationRequest): User
}