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
import com.example.notariax.viewmodel.home.ExternalApiUiState
import com.example.notariax.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val navigateToLogin by viewModel.navigateToLogin.collectAsState()
    val externalApiState by viewModel.externalApiState.collectAsState()

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

        ExternalApiSection(
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
private fun ExternalApiSection(
    state: ExternalApiUiState,
    onRefreshClick: () -> Unit,
    onCreatePostClick: () -> Unit
) {
    var showEndpointDetails by remember { mutableStateOf(false) }
    val overview = state.overview

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
                text = "Datos externos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ColorTextDark
            )
            Text(
                text = "Contenido de demostración desde JSONPlaceholder.",
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextDark.copy(alpha = 0.75f)
            )

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Text(
                text = when {
                    overview != null -> "Publicación de ejemplo #${overview.featuredPost.id} cargada"
                    state.isLoading -> "Consultando contenido…"
                    state.errorMessage != null -> "No se pudo cargar el contenido."
                    else -> "Actualiza para consultar contenido de ejemplo."
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextDark
            )

            state.errorMessage?.let { message ->
                Text(
                    text = "Error de conexión: $message",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRefreshClick,
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Actualizar GET")
                }
                Button(
                    onClick = {
                        showEndpointDetails = true
                        onCreatePostClick()
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    Text("Probar POST")
                }
            }

            TextButton(
                onClick = { showEndpointDetails = !showEndpointDetails },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(if (showEndpointDetails) "Ocultar detalles" else "Ver endpoints y respuestas")
            }

            if (showEndpointDetails) {
                HorizontalDivider()
                Text(
                    text = "Consultas GET",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                overview?.let { resultOverview ->
                    Text(
                        text = resultOverview.featuredPost.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                    Text(
                        text = resultOverview.featuredPost.body,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                    resultOverview.endpointResults.forEach { result ->
                        Text(
                            text = "${result.route} · ${result.resultSummary}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Text(
                    text = "POST /posts · respuesta de prueba; no se guarda permanentemente.",
                    style = MaterialTheme.typography.bodySmall
                )

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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(navController = rememberNavController())
    }
}
