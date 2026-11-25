package com.example.notariax.viewmodel.register

import android.app.Application
import com.example.notariax.MainDispatcherRule
import com.example.notariax.data.remote.dto.UsuarioRequest
import com.example.notariax.data.remote.dto.UsuarioResponse
import com.example.notariax.data.repository.BackendRepository
import com.example.notariax.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<UserRepository>(relaxed = true)
    private val backendRepository = mockk<BackendRepository>(relaxed = true)
    private val viewModel = RegisterViewModel(application, repository, backendRepository)

    @Test
    fun rutInputIsFilteredAndClearsItsError() {
        viewModel.onRutChanged("12.345.678-k")

        assertEquals("12345678K", viewModel.rut.value)
        assertFalse(viewModel.rutError.value)
    }

    @Test
    fun invalidRutFormatDoesNotReplaceTheCurrentValue() {
        viewModel.onRutChanged("12345678K")

        viewModel.onRutChanged("1234567KK")

        assertEquals("12345678K", viewModel.rut.value)
    }

    @Test
    fun invalidRegistrationShowsValidationErrorsAndDoesNotSave() = runTest {
        viewModel.onFullNameChanged(" ")
        viewModel.onEmailChanged("correo-invalido")
        viewModel.onPasswordChanged("123")
        viewModel.onConfirmPasswordChanged("otra")

        viewModel.onRegisterClick()

        assertTrue(viewModel.rutError.value)
        assertTrue(viewModel.fullNameError.value)
        assertTrue(viewModel.emailError.value)
        assertTrue(viewModel.passwordError.value)
        assertTrue(viewModel.confirmPasswordError.value)
        coVerify(exactly = 0) { repository.saveUser(any(), any(), any()) }
    }

    @Test
    fun editingFieldsClearsTheirValidationErrors() = runTest {
        viewModel.onRegisterClick()

        viewModel.onRutChanged("12345678K")
        viewModel.onFullNameChanged("Juan Perez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")
        viewModel.onConfirmPasswordChanged("clave123")

        assertFalse(viewModel.rutError.value)
        assertFalse(viewModel.fullNameError.value)
        assertFalse(viewModel.emailError.value)
        assertFalse(viewModel.passwordError.value)
        assertFalse(viewModel.confirmPasswordError.value)
    }

    @Test
    fun validRegistrationSavesUserAndCanClearSuccessAfterNavigation() = runTest {
        coEvery {
            backendRepository.registrarUsuario(
                UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
            )
        } returns Response.success(201, UsuarioResponse("12345678K", "Juan Perez", "juan@example.com"))
        viewModel.onRutChanged("12345678K")
        viewModel.onFullNameChanged("Juan Perez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")
        viewModel.onConfirmPasswordChanged("clave123")

        viewModel.onRegisterClick()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            backendRepository.registrarUsuario(
                UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
            )
        }
        coVerify(exactly = 1) { repository.saveUser("12345678K", "Juan Perez", "juan@example.com") }
        assertTrue(viewModel.registrationSuccess.value)

        viewModel.onNavigatedToHome()
        assertFalse(viewModel.registrationSuccess.value)
    }

    @Test
    fun duplicateRutShowsConflictMessage() = runTest {
        val request = UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
        val conflictResponse = mockk<Response<UsuarioResponse>>()
        every { conflictResponse.isSuccessful } returns false
        every { conflictResponse.code() } returns 409
        coEvery { backendRepository.registrarUsuario(request) } returns conflictResponse
        enterValidForm()

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertEquals("Ya existe una cuenta registrada con ese RUT.", viewModel.errorMessage.value)
        assertFalse(viewModel.registrationSuccess.value)
        coVerify(exactly = 0) { repository.saveUser(any(), any(), any()) }
    }

    @Test
    fun xanoDuplicateRutWithHttp200ShowsPayload() = runTest {
        val request = UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
        coEvery { backendRepository.registrarUsuario(request) } returns Response.success(
            UsuarioResponse(payload = "RUT ya registrado", statement = "Throw Error")
        )
        enterValidForm()

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertEquals("RUT ya registrado", viewModel.errorMessage.value)
        assertFalse(viewModel.registrationSuccess.value)
        coVerify(exactly = 0) { repository.saveUser(any(), any(), any()) }
    }

    @Test
    fun serverFailureShowsStatusCode() = runTest {
        val request = UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
        val serverErrorResponse = mockk<Response<UsuarioResponse>>()
        every { serverErrorResponse.isSuccessful } returns false
        every { serverErrorResponse.code() } returns 500
        coEvery { backendRepository.registrarUsuario(request) } returns serverErrorResponse
        enterValidForm()

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertEquals("No se pudo registrar la cuenta. Código: 500.", viewModel.errorMessage.value)
    }

    @Test
    fun successfulResponseWithoutUserShowsError() = runTest {
        val request = UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
        val emptySuccessResponse = mockk<Response<UsuarioResponse>>()
        every { emptySuccessResponse.isSuccessful } returns true
        every { emptySuccessResponse.body() } returns null
        coEvery { backendRepository.registrarUsuario(request) } returns emptySuccessResponse
        enterValidForm()

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertEquals("El backend no devolvió los datos del usuario.", viewModel.errorMessage.value)
        assertFalse(viewModel.registrationSuccess.value)
    }

    @Test
    fun networkFailureShowsFriendlyError() = runTest {
        val request = UsuarioRequest("12345678K", "Juan Perez", "juan@example.com", "clave123")
        coEvery { backendRepository.registrarUsuario(request) } throws IllegalStateException("offline")
        enterValidForm()

        viewModel.onRegisterClick()
        advanceUntilIdle()

        assertEquals(
            "No se pudo crear la cuenta. Comprueba tu conexión e inténtalo nuevamente.",
            viewModel.errorMessage.value
        )
        assertFalse(viewModel.isRegistering.value)
    }

    private fun enterValidForm() {
        viewModel.onRutChanged("12345678K")
        viewModel.onFullNameChanged("Juan Perez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")
        viewModel.onConfirmPasswordChanged("clave123")
    }
}
