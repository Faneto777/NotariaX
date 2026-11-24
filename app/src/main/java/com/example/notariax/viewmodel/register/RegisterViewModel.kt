package com.example.notariax.viewmodel.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.remote.BackendRetrofitClient
import com.example.notariax.data.remote.dto.UsuarioRequest
import com.example.notariax.data.repository.BackendRepository
import com.example.notariax.data.repository.RoomUserRepository
import com.example.notariax.data.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    application: Application,
    private val sessionRepository: UserRepository,
    private val backendRepository: BackendRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        RoomUserRepository(AppDatabase.getDatabase(application).userDao()),
        BackendRepository(BackendRetrofitClient.service)
    )

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

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    fun onRutChanged(input: String) {
        val filtered = input.uppercase().filter { it.isDigit() || it == 'K' }
        if (filtered.matches(Regex("^[0-9]*[K]?$")) && filtered.length <= 9) {
            _rut.value = filtered
            _rutError.value = false
            _errorMessage.value = null
        }
    }

    fun onFullNameChanged(input: String) {
        _fullName.value = input
        _fullNameError.value = false
        _errorMessage.value = null
    }

    fun onEmailChanged(input: String) {
        _email.value = input.trim()
        _emailError.value = false
        _errorMessage.value = null
    }

    fun onPasswordChanged(input: String) {
        _password.value = input
        _passwordError.value = false
        _errorMessage.value = null
    }

    fun onConfirmPasswordChanged(input: String) {
        _confirmPassword.value = input
        _confirmPasswordError.value = false
        _errorMessage.value = null
    }

    fun onRegisterClick() {
        val currentRut = _rut.value
        val currentName = _fullName.value.trim()
        val currentEmail = _email.value
        val currentPassword = _password.value
        val currentConfirm = _confirmPassword.value

        val hasRutErr = currentRut.isBlank() || currentRut.length < 8
        val hasNameErr = currentName.isBlank()
        val hasEmailErr = currentEmail.isBlank() || !currentEmail.contains('@')
        val hasPassErr = currentPassword.isBlank() || currentPassword.length < 6
        val hasConfirmErr = currentConfirm != currentPassword || currentConfirm.isBlank()

        _rutError.value = hasRutErr
        _fullNameError.value = hasNameErr
        _emailError.value = hasEmailErr
        _passwordError.value = hasPassErr
        _confirmPasswordError.value = hasConfirmErr
        _errorMessage.value = null

        if (hasRutErr || hasNameErr || hasEmailErr || hasPassErr || hasConfirmErr || _isRegistering.value) return

        _isRegistering.value = true
        viewModelScope.launch {
            try {
                val response = backendRepository.registrarUsuario(
                    UsuarioRequest(
                        rut = currentRut,
                        nombreCompleto = currentName,
                        email = currentEmail,
                        password = currentPassword
                    )
                )

                if (!response.isSuccessful) {
                    _errorMessage.value = when (response.code()) {
                        409 -> "Ya existe una cuenta registrada con ese RUT."
                        else -> "No se pudo registrar la cuenta. Código: ${response.code()}."
                    }
                    return@launch
                }

                val user = response.body()
                if (user == null) {
                    _errorMessage.value = "El backend no devolvió los datos del usuario."
                    return@launch
                }

                sessionRepository.saveUser(username = user.rut, fullName = user.nombreCompleto)
                _registrationSuccess.value = true
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _errorMessage.value = exception.localizedMessage
                    ?: "No se pudo conectar con el backend."
            } finally {
                _isRegistering.value = false
            }
        }
    }

    fun onNavigatedToHome() {
        _registrationSuccess.value = false
    }
}
