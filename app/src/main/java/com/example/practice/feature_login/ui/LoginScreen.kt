package com.example.practice.feature_login.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.example.practice.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    // State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isOnline by remember { mutableStateOf(true) }

    // Resources
    val greenPrimary = colorResource(id = R.color.green_primary)
    val lightGreen = colorResource(id = R.color.light_green)
    val errorRed = colorResource(id = R.color.error_red)
    val titleColour = colorResource(id = R.color.title_colour)
    val loginTitle = stringResource(id = R.string.login_title)
    val emailHint = stringResource(id = R.string.email_hint)
    val passwordHint = stringResource(id = R.string.password_hint)
    val enterFieldsError = stringResource(id = R.string.error_enter_fields)
    val noWifiMsg = stringResource(id = R.string.no_wifi_message)
    val loginBtn = stringResource(id = R.string.login_button)
    val loginFailedMsg = stringResource(id = R.string.login_failed)
    val registerPrompt = stringResource(id = R.string.login_register_prompt)


    // Check connectivity
    fun isWifiConnected(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nw) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
    LaunchedEffect(Unit) { isOnline = isWifiConnected(context) }

    // Text style
    val inputTextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = loginTitle,
            style = MaterialTheme.typography.headlineLarge.copy(color = titleColour)
        )
        Spacer(Modifier.height(10.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(emailHint) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(5.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            textStyle = inputTextStyle,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            )
        )
        Spacer(Modifier.height(20.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(passwordHint) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(5.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = inputTextStyle,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            )
        )
        Spacer(Modifier.height(20.dp))

        errorMessage?.let {
            Text(it, color = errorRed, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(10.dp))
        }

        if (!isOnline) {
            Text(noWifiMsg, color = errorRed, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = {
                when {
                    !isOnline -> {
                        Toast.makeText(context, noWifiMsg, Toast.LENGTH_SHORT).show()
                    }
                    email.isBlank() || password.isBlank() -> {
                        errorMessage = enterFieldsError
                    }
                    else -> {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                navController.navigate("feed") {
                                    popUpTo(0); launchSingleTop = true
                                }
                            }
                            .addOnFailureListener {
                                errorMessage = it.message
                                Toast.makeText(context, "$loginFailedMsg: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOnline) greenPrimary else lightGreen.copy(alpha = 0.5f)
            ),
            enabled = isOnline
        ) {
            Text(loginBtn, color = Color.White, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = { navController.navigate("register") }, modifier = Modifier.fillMaxWidth()) {
            Text(registerPrompt, color = greenPrimary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
