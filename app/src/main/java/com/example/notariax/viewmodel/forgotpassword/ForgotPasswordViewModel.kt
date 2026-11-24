package com.example.notariax.viewmodel.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.remote.BackendRetrofitClient
import com.example.notariax.data.remote.dto.RecuperarClaveRequest
import com.example.notariax.data.repository.BackendRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val backendRepository: BackendRepository) : ViewModel() {
    constructor() : this(BackendRepository(BackendRetrofitClient.service))

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _emailSent = MutableStateFlow(false)
    val emailSent: StateFlow<Boolean> = _emailSent.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onEmailChanged(input: String) {
        _email.value = input.trim()
        _emailError.value = false
        _emailSent.value = false
        _errorMessage.value = null
    }

    fun onSendRecoveryClick() {
        val currentEmail = _email.value
        val hasError = currentEmail.isBlank() || !currentEmail.contains('@')
        _emailError.value = hasError
        _errorMessage.value = null

        if (hasError || _isSending.value) return

        _isSending.value = true
        viewModelScope.launch {
            try {
                val response = backendRepository.recuperarClave(
                    RecuperarClaveRequest(email = currentEmail)
                )
                if (!response.isSuccessful) {
                    _errorMessage.value = "No se pudo procesar la solicitud. Código: ${response.code()}."
                    return@launch
                }
                response.body()?.errorMessage()?.let { message ->
                    _errorMessage.value = message
                    return@launch
                }
                _emailSent.value = true
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _errorMessage.value = "No se pudo enviar la solicitud. Comprueba tu conexión e inténtalo nuevamente."
            } finally {
                _isSending.value = false
            }
        }
    }
}
