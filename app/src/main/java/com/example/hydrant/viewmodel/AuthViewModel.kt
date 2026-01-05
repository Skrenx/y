package com.example.hydrant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydrant.data.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val userEmail: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = repository.currentUser
        _uiState.value = _uiState.value.copy(
            isAuthenticated = currentUser != null,
            userEmail = currentUser?.email
        )
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userEmail = user.email,
                        error = null
                    )
                }
                .onFailure { exception ->
                    val errorMessage = getSignInErrorMessage(exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
        }
    }

    private fun getSignInErrorMessage(exception: Throwable): String {
        // First, check exception type for more accurate detection
        return when (exception) {
            is FirebaseAuthInvalidUserException -> {
                // User doesn't exist
                "No account found with this email. Please sign up first."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Wrong password or invalid email format
                val errorCode = exception.errorCode
                when (errorCode) {
                    "ERROR_WRONG_PASSWORD" ->
                        "Wrong password. Try again or click \"Forgot password?\" for more options."
                    "ERROR_INVALID_EMAIL" ->
                        "Please enter a valid email address."
                    else ->
                        // For invalid-credential (covers both wrong password and non-existent email)
                        "Invalid email or password. Please check your credentials or sign up for a new account."
                }
            }
            else -> {
                // Fallback to message-based detection
                val message = exception.message?.lowercase() ?: ""
                when {
                    // User not found
                    message.contains("user-not-found") ||
                            message.contains("no user") ||
                            message.contains("user not found") ||
                            message.contains("no user record") ||
                            message.contains("auth/user-not-found") ->
                        "No account found with this email. Please sign up first."

                    // Invalid email format
                    message.contains("invalid-email") ||
                            message.contains("invalid email") ||
                            message.contains("badly formatted") ||
                            message.contains("auth/invalid-email") ->
                        "Please enter a valid email address."

                    // Too many attempts
                    message.contains("too-many-requests") ||
                            message.contains("too many") ||
                            message.contains("blocked") ||
                            message.contains("auth/too-many-requests") ->
                        "Too many failed attempts. Please try again later or reset your password."

                    // Network error
                    message.contains("network") ||
                            message.contains("network-request-failed") ->
                        "Network error. Please check your internet connection."

                    // Account disabled
                    message.contains("user-disabled") ||
                            message.contains("disabled") ->
                        "This account has been disabled. Please contact support."

                    // Invalid credential - Firebase returns this for BOTH wrong password AND non-existent email
                    message.contains("invalid-credential") ||
                            message.contains("invalid credential") ||
                            message.contains("invalid_credential") ||
                            message.contains("supplied auth credential is incorrect") ||
                            message.contains("credential is incorrect") ->
                        "Invalid email or password. Please check your credentials or sign up for a new account."

                    // Wrong password (older Firebase versions)
                    message.contains("wrong-password") ||
                            message.contains("wrong password") ||
                            message.contains("password") && message.contains("invalid") ||
                            message.contains("password is invalid") ||
                            message.contains("incorrect password") ||
                            message.contains("auth/wrong-password") ||
                            message.contains("error_wrong_password") ->
                        "Wrong password. Try again or click \"Forgot password?\" for more options."

                    // Default error - generic message
                    else -> "Login failed. Please check your email and password."
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.signUpWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userEmail = user.email,
                        error = null
                    )
                }
                .onFailure { exception ->
                    val errorMessage = getSignUpErrorMessage(exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
        }
    }

    private fun getSignUpErrorMessage(exception: Throwable): String {
        // First, check exception type for more accurate detection
        return when (exception) {
            is FirebaseAuthUserCollisionException -> {
                "An account with this email already exists. Please login instead."
            }
            is FirebaseAuthWeakPasswordException -> {
                "Password is too weak. Please choose a stronger password."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                "Please enter a valid email address."
            }
            else -> {
                // Fallback to message-based detection
                val message = exception.message?.lowercase() ?: ""
                when {
                    // Email already in use
                    message.contains("email-already-in-use") ||
                            message.contains("already in use") ||
                            message.contains("auth/email-already-in-use") ->
                        "An account with this email already exists. Please login instead."

                    // Invalid email
                    message.contains("invalid-email") ||
                            message.contains("invalid email") ||
                            message.contains("badly formatted") ->
                        "Please enter a valid email address."

                    // Weak password
                    message.contains("weak-password") ||
                            message.contains("weak password") ->
                        "Password is too weak. Please choose a stronger password."

                    // Network error
                    message.contains("network") ->
                        "Network error. Please check your internet connection."

                    // Default error
                    else -> exception.message ?: "Sign up failed. Please try again."
                }
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Reset auth state after sign-up so user is redirected to login
    fun resetAuthState() {
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            userEmail = null
        )
    }
}