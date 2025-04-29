package com.example.practice.feature_naturebot.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.practice.R
import com.example.practice.components.BottomNavBar
import com.example.practice.feature_naturebot.viewmodel.NatureBotViewModel
import com.example.practice.feature_profile.viewmodel.ProfileViewModel
import com.example.practice.common.network.rememberNetworkState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NatureBotScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    natureBotViewModel: NatureBotViewModel = viewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var userQuestion by remember { mutableStateOf("") }

    val factMessage = natureBotViewModel.factMessage.value
    val accessibilityModeEnabled by profileViewModel.accessibilityEnabled.collectAsState()
    val errorMessage by natureBotViewModel.errorMessage // Observing error message
    val textStyle = if (accessibilityModeEnabled) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp)
    }

    val scrollState = rememberScrollState()
    val isOnline = rememberNetworkState()

    Scaffold(
        bottomBar = { BottomNavBar(navController, accessibilityModeEnabled) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.ai_nature_friend),
                style = MaterialTheme.typography.headlineLarge.copy(color = colorResource(R.color.green_primary)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Use the same style for the TextField
                TextField(
                    value = userQuestion,
                    onValueChange = { userQuestion = it },
                    label = {
                        Text(
                            stringResource(id = R.string.ask_a_question),
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF006400),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color(0xFF006400),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.1
                    )
                )

                Button(
                    onClick = {
                        // Validate input before calling API
                        if (natureBotViewModel.validateInput(userQuestion, isOnline)) {
                            isLoading = true
                            natureBotViewModel.getAiResponse(userQuestion)
                            isLoading = false
                        }
                    },
                    enabled = isOnline, // Disable button if offline
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .align(Alignment.Bottom),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
                ) {
                    Text(
                        stringResource(id = R.string.send),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                }
            }

            // Show error message if validation fails
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Red),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    color = colorResource(R.color.green_primary),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                if (isOnline) {
                    // If online, show the fact message or default prompt
                    if (factMessage.isNotEmpty()) {
                        Text(
                            text = factMessage,
                            style = textStyle.copy(color = Color.Black)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.default_prompt),
                            style = textStyle.copy(color = Color.Gray),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // If offline, show the offline message
                    Text(
                        text = stringResource(R.string.offline_message),
                        style = textStyle.copy(color = Color.Red),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun FunFactScreenPreview() {
    NatureBotScreen(navController = NavHostController(LocalContext.current))
}
