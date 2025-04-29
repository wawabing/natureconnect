package com.example.practice.feature_profile.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.practice.R
import com.example.practice.components.BottomNavBar
import com.example.practice.feature_profile.viewmodel.ProfileViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val localAvatarFileName by profileViewModel.localAvatarFileName.collectAsState()
    var showAvatarSelector by remember { mutableStateOf(false) }

    val predefinedAvatars = listOf(
        "profile_male_1", "profile_female_1",
        "profile_male_2", "profile_female_2"
    )

    // dynamic text style
    val textStyle = if (profileViewModel.accessibilityEnabled.collectAsState().value) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp)
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, profileViewModel.accessibilityEnabled.collectAsState().value) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = colorResource(R.color.profile_title_green)
                        )
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    // Avatar picker
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray, shape = CircleShape)
                            .clickable { showAvatarSelector = true },
                        contentAlignment = Alignment.Center
                    ) {
                        val avatarRes = when (localAvatarFileName) {
                            "profile_male_1" -> R.drawable.profile_male_1
                            "profile_female_1" -> R.drawable.profile_female_1
                            "profile_male_2" -> R.drawable.profile_male_2
                            "profile_female_2" -> R.drawable.profile_female_2
                            else -> R.drawable.profile_default
                        }
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = stringResource(R.string.avatar_selected),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Username box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .background(
                                color = colorResource(R.color.username_box_background),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile?.username ?: stringResource(R.string.loading),
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email
                    Text(
                        text = userProfile?.email ?: stringResource(R.string.loading),
                        style = textStyle
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Accessibility + Logout container
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                Color.LightGray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.accessibility_mode),
                                    modifier = Modifier.weight(1f),
                                    style = textStyle
                                )
                                Switch(
                                    checked = profileViewModel.accessibilityEnabled.collectAsState().value,
                                    onCheckedChange = { profileViewModel.setAccessibility(it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Language Selector with Flags
                            var expanded by remember { mutableStateOf(false) }

                            val languages = listOf(
                                "English" to "en",
                                "French" to "fr",
                                "Spanish" to "es",
                                "Mandarin" to "zh",
                                "Arabic" to "ar",
                                "Italian" to "it",
                                "Persian (Iran)" to "fa"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true }
                                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.language) + ": " +
                                            (languages.find { it.second == profileViewModel.languageCode.collectAsState().value }?.first ?: "English"),
                                    style = textStyle
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    languages.forEach { (name, code) ->
                                        val flagRes = when (code) {
                                            "en" -> R.drawable.english_flag
                                            "fr" -> R.drawable.french_flag
                                            "es" -> R.drawable.spanish_flag
                                            "zh" -> R.drawable.chinese_flag
                                            "ar" -> R.drawable.arabic_flag
                                            "it" -> R.drawable.italian_flag
                                            "fa" -> R.drawable.iranian_flag
                                            else -> R.drawable.profile_default
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Image(
                                                        painter = painterResource(id = flagRes),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(name, style = textStyle)
                                                }
                                            },
                                            onClick = {
                                                expanded = false
                                                profileViewModel.setLanguage(code)
                                                Toast.makeText(context, "$name selected", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    profileViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.logout_button)
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.logout),
                                    color = Color.White,
                                    style = textStyle
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Avatar selector dialog
    if (showAvatarSelector) {
        AlertDialog(
            onDismissRequest = { showAvatarSelector = false },
            confirmButton = {},
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    predefinedAvatars.forEach { avatarFileName ->
                        val res = when (avatarFileName) {
                            "profile_male_1" -> R.drawable.profile_male_1
                            "profile_female_1" -> R.drawable.profile_female_1
                            "profile_male_2" -> R.drawable.profile_male_2
                            "profile_female_2" -> R.drawable.profile_female_2
                            else -> R.drawable.profile_default
                        }
                        Image(
                            painter = painterResource(id = res),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clickable {
                                    profileViewModel.setProfilePic(avatarFileName)
                                    showAvatarSelector = false
                                }
                        )
                    }
                }
            }
        )
    }
}
