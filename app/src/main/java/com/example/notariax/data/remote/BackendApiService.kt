package com.example.notariax.data.remote

import com.example.notariax.data.remote.dto.FirmaRequest
import com.example.notariax.data.remote.dto.FirmaResponse
import com.example.notariax.data.remote.dto.LoginRequest
import com.example.notariax.data.remote.dto.RecuperarClaveRequest
import com.example.notariax.data.remote.dto.TramiteRequest
import com.example.notariax.data.remote.dto.TramiteResponse
import com.example.notariax.data.remote.dto.XanoMessageResponse
import com.example.notariax.data.remote.dto.UsuarioRequest
import com.example.notariax.data.remote.dto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BackendApiService {
    @POST("usuarios/registro")
    suspend fun registrarUsuario(@Body request: UsuarioRequest): Response<UsuarioResponse>

    @POST("usuarios/login")
    suspend fun iniciarSesion(@Body request: LoginRequest): Response<UsuarioResponse>

    @POST("usuarios/recuperar")
    suspend fun recuperarClave(@Body request: RecuperarClaveRequest): Response<XanoMessageResponse>

    @GET("usuarios/{rut}")
    suspend fun obtenerUsuario(@Path("rut") rut: String): Response<UsuarioResponse>

    @PUT("usuarios/{rut}")
    suspend fun actualizarUsuario(
        @Path("rut") rut: String,
        @Body request: UsuarioRequest
    ): Response<UsuarioResponse>

    @DELETE("usuarios/{rut}")
    suspend fun eliminarUsuario(@Path("rut") rut: String): Response<XanoMessageResponse>

    @POST("tramites")
    suspend fun crearTramite(@Body request: TramiteRequest): Response<TramiteResponse>

    @GET("tramites")
    suspend fun listarTramites(): Response<List<TramiteResponse>>

    @GET("tramites/{id}")
    suspend fun obtenerTramite(@Path("id") id: Long): Response<TramiteResponse>

    @PUT("tramites/{id}")
    suspend fun actualizarTramite(
        @Path("id") id: Long,
        @Body request: TramiteRequest
    ): Response<TramiteResponse>

    @DELETE("tramites/{id}")
    suspend fun eliminarTramite(@Path("id") id: Long): Response<XanoMessageResponse>

    @POST("firmas")
    suspend fun crearFirma(@Body request: FirmaRequest): Response<FirmaResponse>

    @GET("firmas-por-tramite")
    suspend fun obtenerFirmaPorTramite(
        @Query("tramiteId") tramiteId: Long
    ): Response<List<FirmaResponse>>
}
