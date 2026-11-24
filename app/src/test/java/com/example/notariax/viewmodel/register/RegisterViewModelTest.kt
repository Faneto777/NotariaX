package com.example.notariax.viewmodel.register

import android.app.Application
import com.example.notariax.MainDispatcherRule
import com.example.notariax.data.repository.UserRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<UserRepository>(relaxed = true)
    private val viewModel = RegisterViewModel(application, repository)

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
        coVerify(exactly = 0) { repository.saveUser(any(), any()) }
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
        viewModel.onRutChanged("12345678K")
        viewModel.onFullNameChanged("Juan Perez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onPasswordChanged("clave123")
        viewModel.onConfirmPasswordChanged("clave123")

        viewModel.onRegisterClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.saveUser("12345678K", "Juan Perez") }
        assertTrue(viewModel.registrationSuccess.value)

        viewModel.onNavigatedToHome()
        assertFalse(viewModel.registrationSuccess.value)
    }
}
