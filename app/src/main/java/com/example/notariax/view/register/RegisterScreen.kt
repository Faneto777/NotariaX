package com.example.notariax.view.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notariax.view.Rutas
import com.example.notariax.view.login.ColorBorder
import com.example.notariax.view.login.ColorPrimary
import com.example.notariax.view.login.ColorSubtitle
import com.example.notariax.view.login.ColorTextDark

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val rut by viewModel.rut.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val rutError by viewModel.rutError.collectAsState()
    val fullNameError by viewModel.fullNameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()

    val registrationSuccess by viewModel.registrationSuccess.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            navController.navigate(Rutas.HOME) {
                popUpTo(Rutas.LOGIN) { inclusive = true }
            }
            viewModel.onNavigatedToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Crear Cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
        Text(text = "Regístrate para comenzar a usar NotaXo.", color = ColorSubtitle, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "RUT", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = rut,
            onValueChange = { viewModel.onRutChanged(it) },
            placeholder = { Text("Ej: 12345678K") },
            isError = rutError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (rutError) Text("RUT inválido (mínimo 8 caracteres)", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Nombre Completo", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = fullName,
            onValueChange = { viewModel.onFullNameChanged(it) },
            placeholder = { Text("Ej: Juan Pérez") },
            isError = fullNameError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (fullNameError) Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Correo Electrónico", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onEmailChanged(it) },
            placeholder = { Text("ejemplo@correo.com") },
            isError = emailError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (emailError) Text("Ingrese un correo válido", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Contraseña", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            placeholder = { Text("Mínimo 6 caracteres") },
            singleLine = true,
            isError = passwordError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (passwordError) Text("Mínimo 6 caracteres requeridos", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Confirmar Contraseña", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
            placeholder = { Text("Repite tu contraseña") },
            singleLine = true,
            isError = confirmPasswordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (confirmPasswordError) Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.onRegisterClick() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("¿Ya tienes cuenta? Inicia Sesión", color = ColorPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
