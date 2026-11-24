package com.example.notariax.data.remote.dto

import com.google.gson.annotations.SerializedName

interface XanoResponse {
    val payload: String?
    val statement: String?

    fun errorMessage(): String? {
        if (statement.isNullOrBlank()) return null
        return payload?.takeIf(String::isNotBlank) ?: "El servicio no pudo completar la solicitud."
    }
}

data class XanoMessageResponse(
    override val payload: String? = null,
    override val statement: String? = null
) : XanoResponse

data class UsuarioRequest(
    val rut: String,
    val nombreCompleto: String,
    val email: String,
    val password: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RecuperarClaveRequest(
    val email: String
)

data class UsuarioResponse(
    val rut: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val id: Long? = null,
    @SerializedName("created_at") val createdAt: Long? = null,
    val password: String? = null,
    override val payload: String? = null,
    override val statement: String? = null
) : XanoResponse

data class TramiteRequest(
    val tipoTramite: String,
    val antecedentes: String,
    val documentoAdjunto: Boolean,
    val estado: String
)

data class TramiteResponse(
    val id: Long? = null,
    val tipoTramite: String? = null,
    val antecedentes: String? = null,
    val documentoAdjunto: Boolean? = null,
    val estado: String? = null,
    val fechaCreacion: String? = null,
    override val payload: String? = null,
    override val statement: String? = null
) : XanoResponse

data class FirmaRequest(
    val tramiteId: Long,
    val firmado: Boolean
)

data class FirmaResponse(
    val id: Long? = null,
    val tramiteId: Long? = null,
    val firmado: Boolean? = null,
    val fechaFirma: String? = null,
    override val payload: String? = null,
    override val statement: String? = null
) : XanoResponse
