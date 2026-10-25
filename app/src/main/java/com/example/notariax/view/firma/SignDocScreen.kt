package com.example.notariax.view.firma

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

val ColorPrimary = Color(0xFF123D70)
val ColorTextDark = Color(0xFF182D43)
val ColorBorder = Color(0xFFCAD5E0)
val ColorBackground = Color(0xFFFFFFFF)

@Composable
fun SignDocScreen(navController: NavController) {
    var tokenPin by remember { mutableStateOf("") }
    var tokenError by remember { mutableStateOf(false) }
    var documentoFirmado by remember { mutableStateOf(false) }
    var procesandoFirma by remember { mutableStateOf(false) }

    LaunchedEffect(procesandoFirma) {
        if (procesandoFirma) {
            delay(1500)
            procesandoFirma = false
            documentoFirmado = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Firma de Documento",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextDark,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Autoriza el trámite mediante tu E-Token.",
            color = ColorTextDark.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Documento",
                    tint = ColorPrimary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Declaración Jurada",
                    fontWeight = FontWeight.Bold,
                    color = ColorTextDark
                )
                Text(
                    text = "Estado: Pendiente de firma",
                    fontSize = 12.sp,
                    color = Color(0xFFB71C1C)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "PIN de Seguridad (E-Token)",
            color = ColorTextDark,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = tokenPin,
            onValueChange = {
                if (it.length <= 6) {
                    tokenPin = it
                    tokenError = false
                }
            },
            placeholder = { Text("Ingresa tu PIN de 6 dígitos") },
            isError = tokenError,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (tokenError) {
                    Text("El PIN es obligatorio para firmar", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                tokenError = tokenPin.isBlank() || tokenPin.length < 4
                if (!tokenError && !documentoFirmado) {
                    procesandoFirma = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp),
            enabled = !documentoFirmado
        ) {
            Icon(imageVector = Icons.Default.Create, contentDescription = "Firmar", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (procesandoFirma) "Validando credenciales..." else "Firmar Documento")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = documentoFirmado,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¡Documento firmado exitosamente!",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { navController.navigate("home") }) {
                    Text("Volver al Inicio", color = ColorPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignDocScreenPreview() {
    MaterialTheme {
        SignDocScreen(navController = rememberNavController())
    }
}