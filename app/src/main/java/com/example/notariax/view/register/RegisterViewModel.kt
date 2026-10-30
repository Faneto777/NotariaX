package com.example.notariax.view.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    private val _rut = MutableStateFlow("")
    val rut: StateFlow<String> = _rut.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _rutError = MutableStateFlow(false)
    val rutError: StateFlow<Boolean> = _rutError.asStateFlow()

    private val _fullNameError = MutableStateFlow(false)
    val fullNameError: StateFlow<Boolean> = _fullNameError.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow(false)
    val confirmPasswordError: StateFlow<Boolean> = _confirmPasswordError.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    fun onRutChanged(input: String) {
        val filtered = input.uppercase().filter { it.isDigit() || it == 'K' }
        if (filtered.matches(Regex("^[0-9]*[K]?$")) && filtered.length <= 9) {
            _rut.value = filtered
            _rutError.value = false
        }
    }

    fun onFullNameChanged(input: String) {
        _fullName.value = input
        _fullNameError.value = false
    }

    fun onEmailChanged(input: String) {
        _email.value = input
        _emailError.value = false
    }

    fun onPasswordChanged(input: String) {
        _password.value = input
        _passwordError.value = false
    }

    fun onConfirmPasswordChanged(input: String) {
        _confirmPassword.value = input
        _confirmPasswordError.value = false
    }

    fun onRegisterClick() {
        val currentRut = _rut.value
        val currentName = _fullName.value
        val currentEmail = _email.value
        val currentPassword = _password.value
        val currentConfirm = _confirmPassword.value

        val hasRutErr = currentRut.isBlank() || currentRut.length < 8
        val hasNameErr = currentName.isBlank()
        val hasEmailErr = currentEmail.isBlank() || !currentEmail.contains("@")
        val hasPassErr = currentPassword.isBlank() || currentPassword.length < 6
        val hasConfirmErr = currentConfirm != currentPassword || currentConfirm.isBlank()

        _rutError.value = hasRutErr
        _fullNameError.value = hasNameErr
        _emailError.value = hasEmailErr
        _passwordError.value = hasPassErr
        _confirmPasswordError.value = hasConfirmErr

        if (!hasRutErr && !hasNameErr && !hasEmailErr && !hasPassErr && !hasConfirmErr) {
            viewModelScope.launch {
                repository.saveUser(username = currentRut, fullName = currentName)
                _registrationSuccess.value = true
            }
        }
    }

    fun onNavigatedToHome() {
        _registrationSuccess.value = false
    }
}
