package com.example.notariax.data.repository

import com.example.notariax.data.remote.BackendApiService
import com.example.notariax.data.remote.dto.FirmaRequest
import com.example.notariax.data.remote.dto.FirmaResponse
import com.example.notariax.data.remote.dto.LoginRequest
import com.example.notariax.data.remote.dto.RecuperarClaveRequest
import com.example.notariax.data.remote.dto.TramiteRequest
import com.example.notariax.data.remote.dto.TramiteResponse
import com.example.notariax.data.remote.dto.UsuarioRequest
import com.example.notariax.data.remote.dto.UsuarioResponse
import retrofit2.Response

class BackendRepository(private val service: BackendApiService) {
    suspend fun registrarUsuario(request: UsuarioRequest): Response<UsuarioResponse> =
        service.registrarUsuario(request)

    suspend fun iniciarSesion(request: LoginRequest): Response<UsuarioResponse> =
        service.iniciarSesion(request)

    suspend fun recuperarClave(request: RecuperarClaveRequest): Response<Unit> =
        service.recuperarClave(request)

    suspend fun obtenerUsuario(rut: String): Response<UsuarioResponse> =
        service.obtenerUsuario(rut)

    suspend fun actualizarUsuario(rut: String, request: UsuarioRequest): Response<UsuarioResponse> =
        service.actualizarUsuario(rut, request)

    suspend fun eliminarUsuario(rut: String): Response<Unit> =
        service.eliminarUsuario(rut)

    suspend fun crearTramite(request: TramiteRequest): Response<TramiteResponse> =
        service.crearTramite(request)

    suspend fun listarTramites(): Response<List<TramiteResponse>> =
        service.listarTramites()

    suspend fun obtenerTramite(id: Long): Response<TramiteResponse> =
        service.obtenerTramite(id)

    suspend fun actualizarTramite(id: Long, request: TramiteRequest): Response<TramiteResponse> =
        service.actualizarTramite(id, request)

    suspend fun eliminarTramite(id: Long): Response<Unit> =
        service.eliminarTramite(id)

    suspend fun crearFirma(request: FirmaRequest): Response<FirmaResponse> =
        service.crearFirma(request)

    suspend fun obtenerFirmaPorTramite(tramiteId: Long): Response<FirmaResponse> =
        service.obtenerFirmaPorTramite(tramiteId)
}
