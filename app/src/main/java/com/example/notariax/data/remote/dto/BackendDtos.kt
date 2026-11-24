package com.example.notariax.data.remote.dto

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
    val rut: String,
    val nombreCompleto: String,
    val email: String
)

data class TramiteRequest(
    val tipoTramite: String,
    val antecedentes: String,
    val documentoAdjunto: Boolean,
    val estado: String
)

data class TramiteResponse(
    val id: Long?,
    val tipoTramite: String?,
    val antecedentes: String?,
    val documentoAdjunto: Boolean?,
    val estado: String?,
    val fechaCreacion: String?
)

data class FirmaRequest(
    val tramiteId: Long,
    val firmado: Boolean
)

data class FirmaResponse(
    val id: Long?,
    val tramiteId: Long?,
    val firmado: Boolean?,
    val fechaFirma: String?
)
