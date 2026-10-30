package com.example.notariax.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notariax.view.Rutas
import com.example.notariax.view.tramite.ColorPrimary
import com.example.notariax.view.tramite.ColorTextDark
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val navigateToLogin by viewModel.navigateToLogin.collectAsState()

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

        Spacer(modifier = Modifier.height(48.dp))

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

        Spacer(modifier = Modifier.weight(1f))

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(navController = rememberNavController())
    }
}