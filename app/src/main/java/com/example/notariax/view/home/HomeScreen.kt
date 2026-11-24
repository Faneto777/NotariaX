package com.example.notariax.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.notariax.ui.navigation.Rutas
import com.example.notariax.ui.theme.ColorPrimary
import com.example.notariax.ui.theme.ColorTextDark
import com.example.notariax.viewmodel.home.BackendUserUiState
import com.example.notariax.viewmodel.home.ExternalApiUiState
import com.example.notariax.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val navigateToLogin by viewModel.navigateToLogin.collectAsState()
    val externalApiState by viewModel.externalApiState.collectAsState()
    val backendUserState by viewModel.backendUserState.collectAsState()

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navController.navigate(Rutas.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.onNavigatedToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Panel Principal",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextDark,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Bienvenido a NotaXo. ¿Qué deseas hacer hoy?",
            fontSize = 14.sp,
            color = ColorTextDark.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BackendUserSection(
            state = backendUserState,
            onRefreshClick = viewModel::onRefreshBackendUserClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate(Rutas.TRAMITE) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Trámite")
            Spacer(modifier = Modifier.width(12.dp))
            Text("Iniciar Nuevo Trámite", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Rutas.FIRMA) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Create, contentDescription = "Firma")
            Spacer(modifier = Modifier.width(12.dp))
            Text("Firmar Documento Pendiente", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        HelpAndFeedbackSection(
            state = externalApiState,
            onRefreshClick = viewModel::onRefreshExternalApiClick,
            onCreatePostClick = viewModel::onCreateDemoPostClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { viewModel.onLogoutClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BackendUserSection(
    state: BackendUserUiState,
    onRefreshClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Perfil de usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            when {
                state.isLoading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                state.user != null -> {
                    Text(state.user.nombreCompleto, style = MaterialTheme.typography.bodyLarge)
                    Text("RUT: ${state.user.rut}", style = MaterialTheme.typography.bodySmall)
                    Text("Correo: ${state.user.email}", style = MaterialTheme.typography.bodySmall)
                }
                else -> {
                    state.errorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    OutlinedButton(onClick = onRefreshClick) {
                        Text("Volver a cargar perfil")
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpAndFeedbackSection(
    state: ExternalApiUiState,
    onRefreshClick: () -> Unit,
    onCreatePostClick: (String) -> Unit
) {
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(state.postCreatedMessage) {
        if (state.postCreatedMessage != null) comment = ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Centro de ayuda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ColorTextDark
            )
            Text(
                text = "Prepara tus antecedentes y documentos antes de iniciar un trámite.",
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextDark.copy(alpha = 0.75f)
            )

            Text(
                text = "Consulta información de apoyo y comparte sugerencias sobre la aplicación.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextDark
            )

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Text(
                text = when {
                    state.isLoading -> "Actualizando información…"
                    state.overview != null -> "La información de apoyo está disponible."
                    state.errorMessage != null -> "No fue posible actualizar la información."
                    else -> "Actualiza para consultar la información disponible."
                },
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextDark.copy(alpha = 0.75f)
            )

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            OutlinedButton(
                onClick = onRefreshClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar información")
            }

            HorizontalDivider()

            Text(
                text = "¿Tienes una sugerencia?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextDark
            )
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe tu comentario") },
                minLines = 2,
                maxLines = 3,
                enabled = !state.isLoading
            )
            Button(
                onClick = { onCreatePostClick(comment.trim()) },
                enabled = !state.isLoading && comment.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text(if (state.isLoading) "Enviando…" else "Enviar sugerencia")
            }

            state.postCreatedMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(navController = rememberNavController())
    }
}
