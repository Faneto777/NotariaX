package com.example.notariax.data.remote.dto

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class BackendDtosTest {
    private val gson = Gson()

    @Test
    fun xanoUserFieldsAreMappedFromJson() {
        val user = gson.fromJson(
            """{"id":7,"created_at":1732114552,"rut":"12345678K","nombreCompleto":"Juan Perez","email":"juan@example.invalid","password":"test-password"}""",
            UsuarioResponse::class.java
        )

        assertEquals(7L, user.id)
        assertEquals(1732114552L, user.createdAt)
        assertEquals("12345678K", user.rut)
        assertEquals("Juan Perez", user.nombreCompleto)
        assertEquals("juan@example.invalid", user.email)
        assertEquals("test-password", user.password)
    }

    @Test
    fun xanoHttp200ErrorBodyIsRecognized() {
        val response = gson.fromJson(
            """{"payload":"RUT ya registrado","statement":"Throw Error"}""",
            UsuarioResponse::class.java
        )

        assertEquals("RUT ya registrado", response.errorMessage())
        assertEquals("", response.rut)
    }
}
