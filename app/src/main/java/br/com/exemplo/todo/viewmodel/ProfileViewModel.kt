package br.com.exemplo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.exemplo.todo.data.model.ProfileState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Observes changes in login state
    private val authStateListener = FirebaseAuth.AuthStateListener {
        val currentUser = it.currentUser
        updateProfileState(currentUser)
    }

    init {
        auth.addAuthStateListener(authStateListener)
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        val currentUser = auth.currentUser
        updateProfileState(currentUser)
    }

    private fun updateProfileState(user: FirebaseUser?) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                user = user,
                isLoading = false,
                error = if (user == null) "User not logged in." else null
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                isLoading = true,
                error = null
            )
            auth.signOut() // Logs out user
            _profileState.value = _profileState.value.copy(
                user = null,
                isLoading = false,
                error = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
