package com.spotapp.mobile.ui.feature.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotapp.mobile.data.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    private val viewModelState: MutableStateFlow<SignInViewModelState> =
        MutableStateFlow(SignInViewModelState())

    val uiState = viewModelState.asStateFlow()

    fun onSignIn(email: String, password: String) {
        if (
            password.isBlank() ||
            password.length < 5 ||
            !isValidEmail(email) ||
            email.isBlank()
        ) {
            viewModelState.update {
                it.copy(errorMessage = "Email or Password invalid")
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                usersRepository.signInUserFirebase(email, password)
            }.onSuccess {
                viewModelState.update {
                    it.copy(
                        isSuccessfullySignIn = true,
                        errorMessage = null,
                        isLoading = false
                    )
                }
            }.onFailure { throwable ->
                viewModelState.update {
                    it.copy(
                        errorMessage = throwable.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun cleanError() {
        viewModelState.update { it.copy(errorMessage = null) }
    }

    private fun isValidEmail(email: String?): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email?.matches(emailRegex) ?: false
    }
}