package com.example.notariax.viewmodel.firma

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.PendingTramiteStore
import com.example.notariax.data.remote.BackendRetrofitClient
import com.example.notariax.data.remote.dto.FirmaRequest
import com.example.notariax.data.repository.BackendRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignDocViewModel(application: Application) : AndroidViewModel(application) {

    private val backendRepository = BackendRepository(BackendRetrofitClient.service)
    private val pendingTramiteStore = PendingTramiteStore(application)
    private val context = application.applicationContext
    private val channelId = "notariax_channel"

    private val _tokenPin = MutableStateFlow("")
    val tokenPin: StateFlow<String> = _tokenPin.asStateFlow()

    private val _tokenError = MutableStateFlow(false)
    val tokenError: StateFlow<Boolean> = _tokenError.asStateFlow()

    private val _documentoFirmado = MutableStateFlow(false)
    val documentoFirmado: StateFlow<Boolean> = _documentoFirmado.asStateFlow()

    private val _procesandoFirma = MutableStateFlow(false)
    val procesandoFirma: StateFlow<Boolean> = _procesandoFirma.asStateFlow()

    private val _errorFirma = MutableStateFlow<String?>(null)
    val errorFirma: StateFlow<String?> = _errorFirma.asStateFlow()

    init {
        createNotificationChannel()
    }

    fun onTokenChanged(newVal: String) {
        if (newVal.length <= 6) {
            _tokenPin.value = newVal
            _tokenError.value = false
            _errorFirma.value = null
        }
    }

    fun onSignClick() {
        val hasError = _tokenPin.value.isBlank() || _tokenPin.value.length < 4
        _tokenError.value = hasError
        _errorFirma.value = null

        if (hasError || _documentoFirmado.value || _procesandoFirma.value) return

        val tramiteId = pendingTramiteStore.get()
        if (tramiteId == null) {
            _errorFirma.value = "Primero debes crear un trámite para poder firmarlo."
            return
        }

        _procesandoFirma.value = true
        viewModelScope.launch {
            try {
                val response = backendRepository.crearFirma(
                    FirmaRequest(tramiteId = tramiteId, firmado = true)
                )
                if (!response.isSuccessful) {
                    _errorFirma.value = "No se pudo firmar el trámite. Código: ${response.code()}."
                    return@launch
                }

                response.body()?.errorMessage()?.let { message ->
                    _errorFirma.value = message
                    return@launch
                }
                pendingTramiteStore.clear()
                _documentoFirmado.value = true
                sendSuccessNotification()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _errorFirma.value = "No se pudo firmar el documento. Comprueba tu conexión e inténtalo nuevamente."
            } finally {
                _procesandoFirma.value = false
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Firma",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones al firmar documentos"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendSuccessNotification() {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Documento Firmado")
            .setContentText("Tu declaración jurada se firmó exitosamente.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
