package com.example.notariax.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.notariax.view.firma.SignDocScreen
import com.example.notariax.view.forgotpassword.ForgotPasswordScreen
import com.example.notariax.view.home.HomeScreen
import com.example.notariax.view.login.LoginScreen
import com.example.notariax.view.register.RegisterScreen
import com.example.notariax.view.tramite.ProcessScreen

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

        composable(Rutas.REGISTER) {
            RegisterScreen(navController = navController)
        }

        composable(Rutas.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }
    }
}
