package com.example.notariax.viewmodel.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.remote.BackendRetrofitClient
import com.example.notariax.data.remote.ExternalRetrofitClient
import com.example.notariax.data.remote.dto.UsuarioResponse
import com.example.notariax.data.repository.BackendRepository
import com.example.notariax.data.repository.ExternalApiOverview
import com.example.notariax.data.repository.ExternalApiRepository
import com.example.notariax.data.repository.RoomUserRepository
import com.example.notariax.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExternalApiUiState(
    val isLoading: Boolean = false,
    val overview: ExternalApiOverview? = null,
    val postCreatedMessage: String? = null,
    val errorMessage: String? = null
)

data class BackendUserUiState(
    val isLoading: Boolean = false,
    val user: UsuarioResponse? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val backendRepository: BackendRepository,
    private val externalApiRepository: ExternalApiRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        RoomUserRepository(AppDatabase.getDatabase(application).userDao()),
        BackendRepository(BackendRetrofitClient.service),
        ExternalApiRepository(ExternalRetrofitClient.service)
    )

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    private val _externalApiState = MutableStateFlow(ExternalApiUiState())
    val externalApiState: StateFlow<ExternalApiUiState> = _externalApiState.asStateFlow()

    private val _backendUserState = MutableStateFlow(BackendUserUiState(isLoading = true))
    val backendUserState: StateFlow<BackendUserUiState> = _backendUserState.asStateFlow()

    init {
        loadExternalApiData()
        loadBackendUser()
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            userRepository.clearUser()
            _navigateToLogin.value = true
        }
    }

    fun onRefreshExternalApiClick() {
        loadExternalApiData()
    }

    fun onRefreshBackendUserClick() {
        loadBackendUser()
    }

    private fun loadBackendUser() {
        viewModelScope.launch {
            _backendUserState.value = _backendUserState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                val sessionUser = userRepository.getLoggedUser()
                if (sessionUser == null) {
                    _backendUserState.value = BackendUserUiState(
                        errorMessage = "No hay una sesión local para consultar el perfil."
                    )
                    return@launch
                }

                val response = backendRepository.obtenerUsuario(sessionUser.username)
                if (!response.isSuccessful) {
                    _backendUserState.value = BackendUserUiState(
                        errorMessage = "No se pudo cargar el perfil. Código: ${response.code()}."
                    )
                    return@launch
                }

                val user = response.body()
                _backendUserState.value = if (user == null) {
                    BackendUserUiState(errorMessage = "El backend no devolvió los datos del usuario.")
                } else {
                    BackendUserUiState(user = user)
                }
            } catch (exception: kotlinx.coroutines.CancellationException) {
                throw exception
            } catch (_: Exception) {
                _backendUserState.value = BackendUserUiState(
                    errorMessage = "No se pudo cargar el perfil. Comprueba que el servicio esté disponible."
                )
            }
        }
    }

    fun onCreateDemoPostClick(comment: String) {
        if (comment.isBlank() || _externalApiState.value.isLoading) return

        viewModelScope.launch {
            _externalApiState.value = _externalApiState.value.copy(
                isLoading = true,
                postCreatedMessage = null,
                errorMessage = null
            )
            runCatching { externalApiRepository.createDemoPost(comment) }
                .onSuccess {
                    _externalApiState.value = _externalApiState.value.copy(
                        isLoading = false,
                        postCreatedMessage = "Gracias por compartir tu sugerencia. La respuesta fue recibida en modo de demostración."
                    )
                }
                .onFailure {
                    _externalApiState.value = _externalApiState.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo enviar la sugerencia. Inténtalo nuevamente."
                    )
                }
        }
    }

    private fun loadExternalApiData() {
        viewModelScope.launch {
            _externalApiState.value = _externalApiState.value.copy(
                isLoading = true,
                postCreatedMessage = null,
                errorMessage = null
            )
            runCatching { externalApiRepository.loadOverview() }
                .onSuccess { overview ->
                    _externalApiState.value = ExternalApiUiState(overview = overview)
                }
                .onFailure {
                    _externalApiState.value = ExternalApiUiState(
                        errorMessage = "No se pudo actualizar la información. Inténtalo nuevamente."
                    )
                }
        }
    }

    fun onNavigatedToLogin() {
        _navigateToLogin.value = false
    }
}
