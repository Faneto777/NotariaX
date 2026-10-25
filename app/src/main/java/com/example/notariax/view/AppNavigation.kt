package com.example.notariax.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notariax.view.firma.SignDocScreen
import com.example.notariax.view.home.HomeScreen
import com.example.notariax.view.login.LoginScreen
import com.example.notariax.view.tramite.ProcessScreen

object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val TRAMITE = "tramite"
    const val FIRMA = "firma"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(Rutas.HOME) {
                HomeScreen(navController = navController)
        }

        composable(route = Rutas.TRAMITE) {
            ProcessScreen(navController = navController)
        }

        composable(Rutas.FIRMA) {
            SignDocScreen(navController = navController)
        }
    }
}