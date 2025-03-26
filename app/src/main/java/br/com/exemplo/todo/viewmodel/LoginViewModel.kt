package br.com.exemplo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.exemplo.todo.data.model.LoginState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChange(email: String) {
        _loginState.value = _loginState.value.copy(email = email, error = null, isSuccess = false)
    }

    fun onPasswordChange(password: String) {
        _loginState.value =
            _loginState.value.copy(password = password, error = null, isSuccess = false)
    }

    fun loginUser() {
        val email = _loginState.value.email
        val password = _loginState.value.password

        if (email.isBlank() || password.isBlank()) {
            _loginState.value = _loginState.value.copy(error = "Please fill in all fields.")
            return
        }

        // Set loading state
        _loginState.value =
            _loginState.value.copy(isLoading = true, error = null, isSuccess = false)

        // Perform login
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _loginState.value =
                    _loginState.value.copy(isSuccess = true, error = null, isLoading = false)
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    error = e.localizedMessage ?: "An error occurred.",
                    isLoading = false,
                    isSuccess = false
                )
            }
        }
    }
}