package com.example.notariax.view.tramite

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notariax.view.Rutas
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

val ColorPrimary = Color(0xFF123D70)
val ColorTextDark = Color(0xFF182D43)
val ColorBorder = Color(0xFFCAD5E0)

@Composable
fun ProcessScreen(navController: NavController, viewModel: ProcessViewModel = viewModel()) {
    val context = LocalContext.current
    val tipoTramite by viewModel.tipoTramite.collectAsState()
    val antecedentes by viewModel.antecedentes.collectAsState()
    val documentoAdjunto by viewModel.documentoAdjunto.collectAsState()
    
    val tipoTramiteError by viewModel.tipoTramiteError.collectAsState()
    val antecedentesError by viewModel.antecedentesError.collectAsState()
    
    val mostrarModalConfirmacion by viewModel.mostrarModalConfirmacion.collectAsState()
    val simularCargaAnimada by viewModel.simularCargaAnimada.collectAsState()
    val tramiteEnviado by viewModel.tramiteEnviado.collectAsState()
    val fakeTramiteId by viewModel.fakeTramiteId.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        viewModel.onDocumentoCapturado(exito)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.createImageFileUri(context)
            cameraLauncher.launch(uri)
        }
    }

    if (mostrarModalConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarEnvio() },
            title = { Text("Confirmar envío") },
            text = { Text("¿Estás seguro de enviar estos antecedentes y documentos al notario para su revisión?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmarEnvio() }) {
                    Text("Enviar al Notario", color = ColorPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarEnvio() }) {
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
            onValueChange = { viewModel.onTipoTramiteChanged(it) },
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
            onValueChange = { viewModel.onAntecedentesChanged(it) },
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
            onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    val uri = viewModel.createImageFileUri(context)
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Capturar con Cámara")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (documentoAdjunto) "Foto documento adjuntada" else "Capturar documento base",
                color = if (documentoAdjunto) Color(0xFF2E7D32) else ColorPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onPrepararEnvio() },
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Éxito", tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trámite enviado correctamente al notario.", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("ID Trámite: $fakeTramiteId", color = ColorTextDark, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { navController.navigate(Rutas.HOME) { popUpTo(Rutas.HOME) { inclusive = true } } }) {
                    Text("Volver al Inicio", color = ColorPrimary, fontWeight = FontWeight.Bold)
                }
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