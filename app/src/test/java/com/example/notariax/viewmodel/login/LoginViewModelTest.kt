package com.example.notariax.viewmodel.login

import android.app.Application
import com.example.notariax.MainDispatcherRule
import com.example.notariax.data.remote.dto.LoginRequest
import com.example.notariax.data.remote.dto.UsuarioResponse
import com.example.notariax.data.repository.BackendRepository
import com.example.notariax.model.UserEntity
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<UserRepository>(relaxed = true)
    private val backendRepository = mockk<BackendRepository>(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        coEvery { repository.getLoggedUser() } returns null
        viewModel = LoginViewModel(application, repository, backendRepository)
    }

    @Test
    fun invalidEmailShowsEmailError() {
        viewModel.onEmailChanged("correo-invalido")
        viewModel.onPasswordChanged("clave123")

        viewModel.onLoginClick()

        assertTrue(viewModel.emailError.value)
        assertFalse(viewModel.passwordError.value)
    }

    @Test
    fun savedLoggedInUserRequestsHomeNavigation() = runTest {
        val loggedInRepository = mockk<UserRepository>(relaxed = true)
        coEvery { loggedInRepository.getLoggedUser() } returns UserEntity(
            username = "12345678K",
            fullName = "Juan Perez",
            isLoggedIn = true
        )
        val loggedInViewModel = LoginViewModel(application, loggedInRepository, backendRepository)

        advanceUntilIdle()

        assertTrue(loggedInViewModel.navigateToHome.value)
    }

    @Test
    fun savedUserWithoutActiveSessionDoesNotNavigate() = runTest {
        val inactiveRepository = mockk<UserRepository>(relaxed = true)
        coEvery { inactiveRepository.getLoggedUser() } returns UserEntity(
            username = "12345678K",
            fullName = "Juan Perez",
            isLoggedIn = false
        )
        val inactiveViewModel = LoginViewModel(application, inactiveRepository, backendRepository)

        advanceUntilIdle()

        assertFalse(inactiveViewModel.navigateToHome.value)
    }

    @Test
    fun invalidLoginShowsFieldErrorsAndDoesNotSaveUser() = runTest {
        advanceUntilIdle()

        viewModel.onLoginClick()

        assertTrue(viewModel.emailError.value)
        assertTrue(viewModel.passwordError.value)
        coVerify(exactly = 0) { repository.saveUser(any(), any()) }
        coVerify(exactly = 0) { backendRepository.iniciarSesion(any()) }
    }

    @Test
    fun validLoginSavesUserAndRequestsNavigation() = runTest {
        advanceUntilIdle()
        coEvery {
            backendRepository.iniciarSesion(LoginRequest("juan@example.com", "clave123"))
        } returns Response.success(UsuarioResponse("12345678K", "Juan Perez", "juan@example.com"))
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { backendRepository.iniciarSesion(LoginRequest("juan@example.com", "clave123")) }
        coVerify(exactly = 1) { repository.saveUser("12345678K", "Juan Perez") }
        assertTrue(viewModel.navigateToHome.value)

        viewModel.onNavigatedToHome()
        assertFalse(viewModel.navigateToHome.value)
    }

    @Test
    fun unauthorizedLoginShowsCredentialError() = runTest {
        advanceUntilIdle()
        val unauthorizedResponse = mockk<Response<UsuarioResponse>>()
        every { unauthorizedResponse.isSuccessful } returns false
        every { unauthorizedResponse.code() } returns 401
        coEvery {
            backendRepository.iniciarSesion(LoginRequest("juan@example.com", "incorrecta"))
        } returns unauthorizedResponse
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("incorrecta")

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals("El correo o la contraseña no coinciden.", viewModel.errorMessage.value)
        assertFalse(viewModel.navigateToHome.value)
        coVerify(exactly = 0) { repository.saveUser(any(), any()) }
    }

    @Test
    fun serverFailureShowsStatusCode() = runTest {
        advanceUntilIdle()
        val serverErrorResponse = mockk<Response<UsuarioResponse>>()
        every { serverErrorResponse.isSuccessful } returns false
        every { serverErrorResponse.code() } returns 500
        coEvery {
            backendRepository.iniciarSesion(LoginRequest("juan@example.com", "clave123"))
        } returns serverErrorResponse
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals("No se pudo iniciar sesión. Código: 500.", viewModel.errorMessage.value)
    }

    @Test
    fun successfulResponseWithoutUserShowsError() = runTest {
        advanceUntilIdle()
        val emptySuccessResponse = mockk<Response<UsuarioResponse>>()
        every { emptySuccessResponse.isSuccessful } returns true
        every { emptySuccessResponse.body() } returns null
        coEvery {
            backendRepository.iniciarSesion(LoginRequest("juan@example.com", "clave123"))
        } returns emptySuccessResponse
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals("El backend no devolvió los datos del usuario.", viewModel.errorMessage.value)
        assertFalse(viewModel.navigateToHome.value)
    }

    @Test
    fun networkFailureShowsFriendlyError() = runTest {
        advanceUntilIdle()
        coEvery {
            backendRepository.iniciarSesion(LoginRequest("juan@example.com", "clave123"))
        } throws IllegalStateException("connection refused")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals(
            "No se pudo iniciar sesión. Comprueba tu conexión e inténtalo nuevamente.",
            viewModel.errorMessage.value
        )
        assertFalse(viewModel.isLoading.value)
    }
}
