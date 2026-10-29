package com.example.notariax.view.firma

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignDocViewModel(application: Application) : AndroidViewModel(application) {

    private val _tokenPin = MutableStateFlow("")
    val tokenPin: StateFlow<String> = _tokenPin.asStateFlow()

    private val _tokenError = MutableStateFlow(false)
    val tokenError: StateFlow<Boolean> = _tokenError.asStateFlow()

    private val _documentoFirmado = MutableStateFlow(false)
    val documentoFirmado: StateFlow<Boolean> = _documentoFirmado.asStateFlow()

    private val _procesandoFirma = MutableStateFlow(false)
    val procesandoFirma: StateFlow<Boolean> = _procesandoFirma.asStateFlow()

    private val context = application.applicationContext
    private val channelId = "notariax_channel"

    init {
        createNotificationChannel()
    }

    fun onTokenChanged(newVal: String) {
        if (newVal.length <= 6) {
            _tokenPin.value = newVal
            _tokenError.value = false
        }
    }

    fun onSignClick() {
        val hasError = _tokenPin.value.isBlank() || _tokenPin.value.length < 4
        _tokenError.value = hasError

        if (!hasError && !_documentoFirmado.value) {
            _procesandoFirma.value = true

            viewModelScope.launch {
                delay(1500)
                _procesandoFirma.value = false
                _documentoFirmado.value = true
                sendSuccessNotification()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Firma"
            val descriptionText = "Notificaciones al firmar documentos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendSuccessNotification() {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Documento Firmado")
            .setContentText("Tu declaración jurada se firmó exitosamente.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Se envía con un ID fijo (1)
        notificationManager.notify(1, builder.build())
    }
}