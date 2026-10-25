package com.example.notariax.view.tramite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

val ColorPrimary = Color(0xFF123D70)
val ColorTextDark = Color(0xFF182D43)
val ColorBorder = Color(0xFFCAD5E0)

@Composable
fun ProcessScreen(navController: NavController) {
    var tipoTramite by remember { mutableStateOf("") }
    var antecedentes by remember { mutableStateOf("") }
    var documentoAdjunto by remember { mutableStateOf(false) }

    var tipoTramiteError by remember { mutableStateOf(false) }
    var antecedentesError by remember { mutableStateOf(false) }

    var mostrarModalConfirmacion by remember { mutableStateOf(false) }
    var simularCargaAnimada by remember { mutableStateOf(false) }
    var tramiteEnviado by remember { mutableStateOf(false) }

    LaunchedEffect(simularCargaAnimada) {
        if (simularCargaAnimada) {
            delay(1500)
            simularCargaAnimada = false
            tramiteEnviado = true
        }
    }

    if (mostrarModalConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarModalConfirmacion = false },
            title = { Text("Confirmar envío") },
            text = { Text("¿Estás seguro de enviar estos antecedentes y documentos al notario para su revisión?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarModalConfirmacion = false
                    simularCargaAnimada = true
                }) {
                    Text("Enviar al Notario", color = ColorPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalConfirmacion = false }) {
                    Text("Cancelar", color = ColorTextDark)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nuevo Trámite",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextDark,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Ingresa los antecedentes del trámite a solicitar.",
            color = ColorTextDark.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = tipoTramite,
            onValueChange = { tipoTramite = it; tipoTramiteError = false },
            label = { Text("Tipo de Trámite (Ej. Declaración Jurada)") },
            isError = tipoTramiteError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (tipoTramiteError) Text("Debe indicar el tipo de trámite", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = antecedentes,
            onValueChange = { antecedentes = it; antecedentesError = false },
            label = { Text("Antecedentes o descripción") },
            isError = antecedentesError,
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimary),
            supportingText = {
                if (antecedentesError) Text("Los antecedentes son obligatorios", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { documentoAdjunto = !documentoAdjunto },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Adjuntar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (documentoAdjunto) "Documento.pdf adjuntado" else "Adjuntar documento base",
                color = if (documentoAdjunto) Color(0xFF2E7D32) else ColorPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                tipoTramiteError = tipoTramite.isBlank()
                antecedentesError = antecedentes.isBlank()

                if (!tipoTramiteError && !antecedentesError && !simularCargaAnimada) {
                    mostrarModalConfirmacion = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp),
            enabled = !tramiteEnviado
        ) {
            Text(if (simularCargaAnimada) "Enviando..." else "Enviar al Notario")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = tramiteEnviado,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Éxito", tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Trámite enviado correctamente al notario.", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProcessScreenPreview() {
    MaterialTheme {
        ProcessScreen(navController = rememberNavController())
    }
}