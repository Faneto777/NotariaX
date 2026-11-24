package com.example.notariax.viewmodel.login

import android.app.Application
import com.example.notariax.MainDispatcherRule
import com.example.notariax.model.UserEntity
import com.example.notariax.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<UserRepository>(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        coEvery { repository.getLoggedUser() } returns null
        viewModel = LoginViewModel(application, repository)
    }

    @Test
    fun rutInputKeepsOnlyDigitsAndTrailingK() {
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
    fun savedLoggedInUserRequestsHomeNavigation() = runTest {
        val loggedInRepository = mockk<UserRepository>(relaxed = true)
        coEvery { loggedInRepository.getLoggedUser() } returns UserEntity(
            username = "12345678K",
            fullName = "Juan Perez",
            isLoggedIn = true
        )
        val loggedInViewModel = LoginViewModel(application, loggedInRepository)

        advanceUntilIdle()

        assertTrue(loggedInViewModel.navigateToHome.value)
    }

    @Test
    fun invalidLoginShowsFieldErrorsAndDoesNotSaveUser() = runTest {
        advanceUntilIdle()

        viewModel.onLoginClick()

        assertTrue(viewModel.rutError.value)
        assertTrue(viewModel.passwordError.value)
        coVerify(exactly = 0) { repository.saveUser(any(), any()) }
    }

    @Test
    fun validLoginSavesUserAndRequestsNavigation() = runTest {
        advanceUntilIdle()
        viewModel.onRutChanged("12345678K")
        viewModel.onPasswordChanged("clave123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.saveUser("12345678K", "Usuario Prueba") }
        assertTrue(viewModel.navigateToHome.value)

        viewModel.onNavigatedToHome()
        assertFalse(viewModel.navigateToHome.value)
    }
}
