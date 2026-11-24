package com.example.notariax.viewmodel.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.AppDatabase
import com.example.notariax.data.remote.ExternalRetrofitClient
import com.example.notariax.data.repository.ExternalApiOverview
import com.example.notariax.data.repository.ExternalApiRepository
import com.example.notariax.data.repository.RoomUserRepository
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

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val userRepository = RoomUserRepository(userDao)
    private val externalApiRepository = ExternalApiRepository(ExternalRetrofitClient.service)

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    private val _externalApiState = MutableStateFlow(ExternalApiUiState())
    val externalApiState: StateFlow<ExternalApiUiState> = _externalApiState.asStateFlow()

    init {
        loadExternalApiData()
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

    fun onCreateDemoPostClick() {
        viewModelScope.launch {
            _externalApiState.value = _externalApiState.value.copy(
                isLoading = true,
                postCreatedMessage = null,
                errorMessage = null
            )
            runCatching { externalApiRepository.createDemoPost() }
                .onSuccess { response ->
                    _externalApiState.value = _externalApiState.value.copy(
                        isLoading = false,
                        postCreatedMessage = "POST /posts respondió con ID ${response.id}. Es una respuesta de prueba; no se guarda permanentemente."
                    )
                }
                .onFailure { error ->
                    _externalApiState.value = _externalApiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "No se pudo conectar con la API externa."
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
                .onFailure { error ->
                    _externalApiState.value = ExternalApiUiState(
                        errorMessage = error.localizedMessage ?: "No se pudo conectar con la API externa."
                    )
                }
        }
    }

    fun onNavigatedToLogin() {
        _navigateToLogin.value = false
    }
}
