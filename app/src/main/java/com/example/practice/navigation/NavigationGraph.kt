package com.example.practice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.practice.feature_feed.ui.FeedScreen
import com.example.practice.feature_garden.ui.GardenScreen
import com.example.practice.feature_login.ui.LoginScreen
import com.example.practice.feature_login.ui.RegisterScreen
import com.example.practice.feature_profile.ui.ProfileScreen
import com.example.practice.feature_naturebot.ui.NatureBotScreen


@Composable
fun NavigationGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("feed") { FeedScreen(navController) }
        composable("garden") { GardenScreen(navController) }
        composable("naturebot") { NatureBotScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}
