package com.example.notariax.viewmodel.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.remote.ExternalRetrofitClient
import com.example.notariax.data.repository.ExternalApiOverview
import com.example.notariax.data.repository.ExternalApiRepository
import com.example.notariax.data.repository.RoomUserRepository
import com.example.notariax.data.repository.UserRepository
import com.example.notariax.model.UserEntity
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

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val externalApiRepository: ExternalApiRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        RoomUserRepository(AppDatabase.getDatabase(application).userDao()),
        ExternalApiRepository(ExternalRetrofitClient.service)
    )

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    private val _externalApiState = MutableStateFlow(ExternalApiUiState())
    val externalApiState: StateFlow<ExternalApiUiState> = _externalApiState.asStateFlow()

    private val _userProfileState = MutableStateFlow(UserProfileUiState(isLoading = true))
    val userProfileState: StateFlow<UserProfileUiState> = _userProfileState.asStateFlow()

    init {
        loadExternalApiData()
        loadUserProfile()
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

    fun onRefreshProfileClick() {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _userProfileState.value = _userProfileState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                val sessionUser = userRepository.getLoggedUser()
                if (sessionUser == null || !sessionUser.isLoggedIn) {
                    _userProfileState.value = UserProfileUiState(
                        errorMessage = "No hay una sesión local para mostrar el perfil."
                    )
                    return@launch
                }

                _userProfileState.value = UserProfileUiState(user = sessionUser)
            } catch (exception: kotlinx.coroutines.CancellationException) {
                throw exception
            } catch (_: Exception) {
                _userProfileState.value = UserProfileUiState(
                    errorMessage = "No se pudo cargar el perfil guardado en el dispositivo."
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
