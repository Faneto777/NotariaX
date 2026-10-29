package com.example.notariax.view.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    private val _rut = MutableStateFlow("")
    val rut: StateFlow<String> = _rut.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _rutError = MutableStateFlow(false)
    val rutError: StateFlow<Boolean> = _rutError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    init {
        checkIfUserIsLogged()
    }

    private fun checkIfUserIsLogged() {
        viewModelScope.launch {
            val user = repository.getLoggedUser()
            if (user != null && user.isLoggedIn) {
                _navigateToHome.value = true
            }
        }
    }

    fun onRutChanged(newRut: String) {
        val rutFiltrado = newRut.uppercase().filter { it.isDigit() || it == 'K' }
        if (rutFiltrado.matches(Regex("^[0-9]*[K]?$")) && rutFiltrado.length <= 9) {
            _rut.value = rutFiltrado
            _rutError.value = false
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = false
    }

    fun onLoginClick() {
        val currentRut = _rut.value
        val currentPassword = _password.value

        val hasRutError = currentRut.isBlank() || currentRut.length < 8
        val hasPasswordError = currentPassword.isBlank()

        _rutError.value = hasRutError
        _passwordError.value = hasPasswordError

        if (!hasRutError && !hasPasswordError) {
            viewModelScope.launch {
                repository.saveUser(username = currentRut, fullName = "Usuario Prueba")
                _navigateToHome.value = true
            }
        }
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }
}
