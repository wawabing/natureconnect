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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.practice.R
import com.example.practice.feature_login.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, viewModel: AuthViewModel = AuthViewModel()) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isOnline by remember { mutableStateOf(true) }  // Track Wi-Fi status

    // Define string resource IDs as variables
    val noWifiMessage = stringResource(id = R.string.no_wifi_message)
    val fieldsRequired = stringResource(id = R.string.fields_required)
    val passwordMismatch = stringResource(id = R.string.password_mismatch)
    val passwordLength = stringResource(id = R.string.password_length)
    val registerTitle = stringResource(id = R.string.register)
    val loginPrompt = stringResource(id = R.string.login_prompt)
    val registerFailed = stringResource(id = R.string.registration_failed)

    //colours from colours.xml
    val titleColour = colorResource(id = R.color.title_colour)



    // Helper function to check for Wi-Fi connection
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    // Check if the device is connected to Wi-Fi
    isOnline = isWifiConnected(context)

    // Fetch colors and strings from resources
    val greenPrimary = Color(ContextCompat.getColor(context, R.color.green_register))
    val lightGreen = Color(ContextCompat.getColor(context, R.color.light_green))
    val errorColor = Color(ContextCompat.getColor(context, R.color.error_red))

    // Input text style
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
            text = registerTitle,
            style = MaterialTheme.typography.headlineLarge.copy(color = titleColour),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Username TextField
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.username)) },
            modifier = Modifier.fillMaxWidth().background(Color.White),
            textStyle = inputTextStyle,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFFFFFFF),
                unfocusedBorderColor = Color(0xFFFFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Email TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier.fillMaxWidth().background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            textStyle = inputTextStyle,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFFFFFFF),
                unfocusedBorderColor = Color(0xFFFFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = inputTextStyle,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFFFFFFF),
                unfocusedBorderColor = Color(0xFFFFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password TextField
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(id = R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = inputTextStyle,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFFFFFFF),
                unfocusedBorderColor = Color(0xFFFFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Show error message
        errorMessage?.let {
            Text(
                text = it,
                color = errorColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Show error message if not connected to Wi-Fi
        if (!isOnline) {
            Text(
                text = noWifiMessage,
                color = errorColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Show loading indicator
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Register Button
        Button(
            onClick = {
                // Check network connection first
                if (!isOnline) {
                    Toast.makeText(context, noWifiMessage, Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Validate input
                if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = fieldsRequired
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = passwordMismatch
                    return@Button
                }
                if (password.length < 6) {
                    errorMessage = passwordLength
                    return@Button
                }

                isLoading = true
                viewModel.registerUser(email, password, username, onSuccess = {
                    isLoading = false  // Stop loading
                    navController.navigate("login")  // Navigate to Login Screen
                }, onFailure = { error ->
                    isLoading = false  // Stop loading on failure
                    errorMessage = error
                    Toast.makeText(context, "$registerFailed: $error", Toast.LENGTH_SHORT).show()
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOnline) greenPrimary else lightGreen.copy(alpha = 0.5f)
            ),
            enabled = isOnline // Disable the button if offline
        ) {
            Text(registerTitle)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigate to Login Screen
        TextButton(
            onClick = { navController.navigate("login") }
        ) {
            Text(loginPrompt)
        }
    }
}
