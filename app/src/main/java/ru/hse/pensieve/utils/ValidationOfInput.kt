package ru.hse.pensieve.utils

object ValidationOfInput {
    fun validateUsername(username: String): Boolean {
        return username.length >= 3
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }
}