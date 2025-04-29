// SplashActivity.kt
package com.example.practice.feature_splash.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.practice.app.MainActivity
import com.example.practice.feature_splash.viewmodel.SplashScreenViewModel
import com.example.practice.ui.theme.PracticeTheme
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : ComponentActivity() {

    private val splashVM: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in and push down appropriate path
        FirebaseAuth.getInstance().currentUser?.uid
            ?.let { splashVM.fetchLanguageCode(it) }
            ?: navigateToMain("login", "en") // no user → go straight to login

        // 2) Render a Compose spinner and wait for splashVM.languageCode to emit
        setContent {
            PracticeTheme {
                val language by splashVM.languageCode.collectAsState()
                val ctx = LocalContext.current

                // as soon as we get language code, persist & navigate:
                LaunchedEffect(language) {
                    if (language.isNotBlank()) {
                        // save for MainActivity.attachBaseContext
                        ctx.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                            .edit()
                            .putString("languageCode", language)
                            .apply()

                        // now fire up MainActivity
                        navigateToMain("feed", language)
                    }
                }

                // simple splash UI
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFFA8E6CF)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        Modifier.size(50.dp),
                        color = Color.White
                    )
                }
            }
        }
    }

    private fun navigateToMain(startDestination: String, languageCode: String) {
        Intent(this, MainActivity::class.java).run {
            putExtra("startDestination", startDestination)
            putExtra("languageCode", languageCode)
            startActivity(this)
            finish()
        }
    }
}
