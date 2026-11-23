package com.example.notariax


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notariax.ui.navigation.AppNavigation
import com.example.notariax.ui.theme.NotariaXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotariaXTheme {
                AppNavigation()
            }
        }
    }
}
