package com.example.notariax.viewmodel.tramite

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notariax.data.local.PendingTramiteStore
import com.example.notariax.data.remote.BackendRetrofitClient
import com.example.notariax.data.remote.dto.TramiteRequest
import com.example.notariax.data.repository.BackendRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProcessViewModel(application: Application) : AndroidViewModel(application) {

    private val backendRepository = BackendRepository(BackendRetrofitClient.service)
    private val pendingTramiteStore = PendingTramiteStore(application)

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

    private val _enviandoTramite = MutableStateFlow(false)
    val enviandoTramite: StateFlow<Boolean> = _enviandoTramite.asStateFlow()

    private val _tramiteEnviado = MutableStateFlow(false)
    val tramiteEnviado: StateFlow<Boolean> = _tramiteEnviado.asStateFlow()

    private val _tramiteId = MutableStateFlow("")
    val tramiteId: StateFlow<String> = _tramiteId.asStateFlow()

    private val _errorEnvio = MutableStateFlow<String?>(null)
    val errorEnvio: StateFlow<String?> = _errorEnvio.asStateFlow()

    private val _fotoUri = MutableStateFlow<Uri?>(null)
    val fotoUri: StateFlow<Uri?> = _fotoUri.asStateFlow()

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

        if (!hasTipoError && !hasAntecedentesError && !_enviandoTramite.value) {
            _errorEnvio.value = null
            _mostrarModalConfirmacion.value = true
        }
    }

    fun onCancelarEnvio() {
        _mostrarModalConfirmacion.value = false
    }

    fun onConfirmarEnvio() {
        _mostrarModalConfirmacion.value = false
        if (_enviandoTramite.value) return

        _enviandoTramite.value = true
        _errorEnvio.value = null

        viewModelScope.launch {
            try {
                val response = backendRepository.crearTramite(
                    TramiteRequest(
                        tipoTramite = _tipoTramite.value.trim(),
                        antecedentes = _antecedentes.value.trim(),
                        documentoAdjunto = _documentoAdjunto.value,
                        estado = "PENDIENTE"
                    )
                )

                if (!response.isSuccessful) {
                    _errorEnvio.value = "No se pudo crear el trámite. Código: ${response.code()}."
                    return@launch
                }

                val createdId = response.body()?.id
                if (createdId == null) {
                    _errorEnvio.value = "El backend no devolvió el ID del trámite."
                    return@launch
                }

                pendingTramiteStore.save(createdId)
                _tramiteId.value = createdId.toString()
                _tramiteEnviado.value = true
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _errorEnvio.value = "No se pudo enviar el trámite. Comprueba tu conexión e inténtalo nuevamente."
            } finally {
                _enviandoTramite.value = false
            }
        }
    }

    fun createImageFileUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
