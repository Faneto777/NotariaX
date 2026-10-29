package com.example.notariax.view.tramite

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ProcessViewModel : ViewModel() {

    private val _tipoTramite = MutableStateFlow("")
    val tipoTramite: StateFlow<String> = _tipoTramite.asStateFlow()

    private val _antecedentes = MutableStateFlow("")
    val antecedentes: StateFlow<String> = _antecedentes.asStateFlow()

    private val _documentoAdjunto = MutableStateFlow(false)
    val documentoAdjunto: StateFlow<Boolean> = _documentoAdjunto.asStateFlow()

    private val _tipoTramiteError = MutableStateFlow(false)
    val tipoTramiteError: StateFlow<Boolean> = _tipoTramiteError.asStateFlow()

    private val _antecedentesError = MutableStateFlow(false)
    val antecedentesError: StateFlow<Boolean> = _antecedentesError.asStateFlow()

    private val _mostrarModalConfirmacion = MutableStateFlow(false)
    val mostrarModalConfirmacion: StateFlow<Boolean> = _mostrarModalConfirmacion.asStateFlow()

    private val _simularCargaAnimada = MutableStateFlow(false)
    val simularCargaAnimada: StateFlow<Boolean> = _simularCargaAnimada.asStateFlow()

    private val _tramiteEnviado = MutableStateFlow(false)
    val tramiteEnviado: StateFlow<Boolean> = _tramiteEnviado.asStateFlow()

    private val _fotoUri = MutableStateFlow<Uri?>(null)
    val fotoUri: StateFlow<Uri?> = _fotoUri.asStateFlow()

    // Manejo de datos ficticios
    private val _fakeTramiteId = MutableStateFlow("")
    val fakeTramiteId: StateFlow<String> = _fakeTramiteId.asStateFlow()

    fun onTipoTramiteChanged(newVal: String) {
        _tipoTramite.value = newVal
        _tipoTramiteError.value = false
    }

    fun onAntecedentesChanged(newVal: String) {
        _antecedentes.value = newVal
        _antecedentesError.value = false
    }

    fun onDocumentoCapturado(exito: Boolean) {
        if (exito) {
            _documentoAdjunto.value = true
        }
    }

    fun onPrepararEnvio() {
        val hasTipoError = _tipoTramite.value.isBlank()
        val hasAntecedentesError = _antecedentes.value.isBlank()

        _tipoTramiteError.value = hasTipoError
        _antecedentesError.value = hasAntecedentesError

        if (!hasTipoError && !hasAntecedentesError && !_simularCargaAnimada.value) {
            _mostrarModalConfirmacion.value = true
        }
    }

    fun onCancelarEnvio() {
        _mostrarModalConfirmacion.value = false
    }

    fun onConfirmarEnvio() {
        _mostrarModalConfirmacion.value = false
        _simularCargaAnimada.value = true

        viewModelScope.launch {
            delay(1500)

            // Generar datos ficticios
            _fakeTramiteId.value = "TRM-" + UUID.randomUUID().toString().substring(0, 6).uppercase()
            _simularCargaAnimada.value = false
            _tramiteEnviado.value = true
        }
    }

    fun createImageFileUri(context: Context): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(imageFileName, ".jpg", storageDir)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        _fotoUri.value = uri
        return uri
    }
}