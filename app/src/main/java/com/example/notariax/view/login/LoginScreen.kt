package com.example.notariax.view.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notariax.view.Rutas
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

val ColorPrimary = Color(0xFF123D70)
val ColorTextDark = Color(0xFF182D43)
val ColorSubtitle = Color(0xFF7A828C)
val ColorBorder = Color(0xFFCAD5E0)


@Composable
fun LoginScreen(navController: NavController) {
    var rut by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rutError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "NotaXo", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = ColorPrimary)
        Text(text = "Trámites simples. Nuevas posibilidades.", color = ColorSubtitle, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Text(text = "Te damos la bienvenida", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorTextDark, modifier = Modifier.align(Alignment.Start))
        Text(text = "Ingresa y continúa con tus trámites.", color = ColorSubtitle, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "RUT", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = rut,
            onValueChange = { input ->
                val rutFiltrado = input.uppercase().filter { it.isDigit() || it == 'K' }
                if (rutFiltrado.matches(Regex("^[0-9]*[K]?$")) && rutFiltrado.length <= 9) {
                    rut = rutFiltrado
                    rutError = false
                }
            },
            placeholder = { Text("Ej: 12345678K") },
            isError = rutError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPrimary,
                unfocusedBorderColor = ColorBorder
            ),
            supportingText = {
                if (rutError) Text("Ingrese un RUT válido (mín. 8 caracteres)", color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contraseña", color = ColorTextDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            placeholder = { Text("Ingresa tu contraseña") },
            singleLine = true,
            isError = passwordError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPrimary,
                unfocusedBorderColor = ColorBorder
            ),
            supportingText = {
                if (passwordError) Text("La contraseña es obligatoria", color = MaterialTheme.colorScheme.error)
            }
        )

        TextButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
            Text("Olvidé mi contraseña", color = ColorPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                rutError = rut.isBlank() || rut.length < 8
                passwordError = password.isBlank()

                if (!rutError && !passwordError) {
                    navController.navigate(Rutas.HOME)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { }) {
            Text("Crear cuenta", color = ColorPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = ColorSubtitle, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Tu notaría, más cerca.", color = ColorSubtitle, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(navController = rememberNavController())
    }
}