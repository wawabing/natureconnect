package com.example.practice.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import com.example.practice.common.language.updateLocale
import com.example.practice.navigation.NavigationGraph
import com.example.practice.ui.theme.PracticeTheme
import java.util.Locale

// MainActivity.kt
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val code = prefs.getString("languageCode", "en")!!
        val updated = newBase.updateLocale(Locale(code))
        super.attachBaseContext(updated)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                val navController = rememberNavController()
                NavigationGraph(
                    navController = navController,
                    startDestination = intent
                        .getStringExtra("startDestination") ?: "login"
                )
            }
        }
    }
}
