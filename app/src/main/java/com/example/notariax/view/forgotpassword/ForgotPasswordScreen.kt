package com.example.notariax.view.forgotpassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notariax.view.login.ColorPrimary
import com.example.notariax.view.login.ColorSubtitle
import com.example.notariax.view.login.ColorTextDark

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: ForgotPasswordViewModel = viewModel()) {
    val email by viewModel.email.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val emailSent by viewModel.emailSent.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.MarkEmailRead,
            contentDescription = null,
            tint = ColorPrimary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Recuperar Contraseña", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ingresa el correo electrónico asociado a tu cuenta para enviarte un enlace de recuperación.",
            color = ColorSubtitle,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

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
                if (emailError) Text("Ingrese un correo válido con @", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onSendRecoveryClick() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp),
            enabled = !isSending && !emailSent
        ) {
            Text(
                if (isSending) "Enviando correo..." else if (emailSent) "Correo Enviado" else "Enviar Enlace",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = emailSent,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "¡Listo! Revisa tu bandeja de entrada en $email para restablecer tu contraseña.",
                        color = Color(0xFF1B5E20),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver al Inicio de Sesión", color = ColorPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
