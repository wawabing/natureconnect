package com.example.practice.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.practice.R
import com.example.practice.ui.theme.GreenPrimary

@Composable
fun BottomNavBar(
    navController: NavHostController,
    accessibilityModeEnabled: Boolean // Receive the accessibility mode preference
) {
    val items = listOf(
        BottomNavItem(stringResource(R.string.nav_feed), Icons.Filled.Home, "feed"),
        BottomNavItem(stringResource(R.string.nav_garden), Icons.Filled.Eco, "garden"),
        BottomNavItem(stringResource(R.string.nav_bot), Icons.AutoMirrored.Filled.Chat, "naturebot"),
        BottomNavItem(stringResource(R.string.nav_profile), Icons.Filled.Person, "profile")
    )

    BottomAppBar(
        containerColor = GreenPrimary,
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { navController.navigate(item.route) }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    // Conditionally display the text based on the accessibility mode
                    if (accessibilityModeEnabled) {
                        Text(
                            text = item.label,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
