package com.example.notariax.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.remote.BackendRetrofitClient
import com.example.notariax.data.remote.dto.LoginRequest
import com.example.notariax.data.repository.BackendRepository
import com.example.notariax.data.repository.RoomUserRepository
import com.example.notariax.data.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val sessionRepository: UserRepository,
    private val backendRepository: BackendRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        RoomUserRepository(AppDatabase.getDatabase(application).userDao()),
        BackendRepository(BackendRetrofitClient.service)
    )

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    init {
        checkIfUserIsLogged()
    }

    private fun checkIfUserIsLogged() {
        viewModelScope.launch {
            val user = sessionRepository.getLoggedUser()
            if (user != null && user.isLoggedIn) {
                _navigateToHome.value = true
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail.trim()
        _emailError.value = false
        _errorMessage.value = null
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = false
        _errorMessage.value = null
    }

    fun onLoginClick() {
        val currentEmail = _email.value
        val currentPassword = _password.value
        val hasEmailError = currentEmail.isBlank() || !currentEmail.contains('@')
        val hasPasswordError = currentPassword.isBlank()

        _emailError.value = hasEmailError
        _passwordError.value = hasPasswordError
        _errorMessage.value = null

        if (hasEmailError || hasPasswordError || _isLoading.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = backendRepository.iniciarSesion(
                    LoginRequest(email = currentEmail, password = currentPassword)
                )

                if (!response.isSuccessful) {
                    _errorMessage.value = when (response.code()) {
                        401 -> "El correo o la contraseña no coinciden."
                        else -> "No se pudo iniciar sesión. Código: ${response.code()}."
                    }
                    return@launch
                }

                val user = response.body()
                if (user == null) {
                    _errorMessage.value = "El backend no devolvió los datos del usuario."
                    return@launch
                }

                sessionRepository.saveUser(username = user.rut, fullName = user.nombreCompleto)
                _navigateToHome.value = true
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _errorMessage.value = "No se pudo iniciar sesión. Comprueba tu conexión e inténtalo nuevamente."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }
}
