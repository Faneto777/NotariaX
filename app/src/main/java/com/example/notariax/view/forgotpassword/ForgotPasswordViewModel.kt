package com.example.notariax.view.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _emailSent = MutableStateFlow(false)
    val emailSent: StateFlow<Boolean> = _emailSent.asStateFlow()

    fun onEmailChanged(input: String) {
        _email.value = input
        _emailError.value = false
        _emailSent.value = false
    }

    fun onSendRecoveryClick() {
        val currentEmail = _email.value
        val hasError = currentEmail.isBlank() || !currentEmail.contains("@")
        _emailError.value = hasError

        if (!hasError) {
            _isSending.value = true
            viewModelScope.launch {
                delay(1200)
                _isSending.value = false
                _emailSent.value = true
            }
        }
    }
}
