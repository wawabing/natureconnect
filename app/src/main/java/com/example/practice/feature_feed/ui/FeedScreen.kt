package com.example.practice.feature_feed.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.practice.R
import com.example.practice.components.BottomNavBar
import com.example.practice.feature_feed.data.Post
import com.example.practice.feature_feed.viewmodel.FeedViewModel
import com.example.practice.feature_profile.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.practice.common.network.rememberNetworkState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FeedScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    feedViewModel: FeedViewModel = viewModel()
) {
    val posts by feedViewModel.posts.collectAsState()
    val isOnline = rememberNetworkState()
    val accessibilityModeEnabled by profileViewModel.accessibilityEnabled.collectAsState()

    var postContent by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val postEmptyError = stringResource(id = R.string.error_empty_post)
    val offlineError = stringResource(id = R.string.error_offline)

    Scaffold(
        bottomBar = { BottomNavBar(navController, accessibilityModeEnabled) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.nature_feed_title),
                        style = MaterialTheme.typography.headlineLarge.copy(color = Color(0xFF2F6B2F)),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isOnline) {
                    Text(
                        text = stringResource(R.string.error_offline),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = postContent,
                        onValueChange = { postContent = it },
                        label = {
                            Text(
                                stringResource(id = R.string.post_content_placeholder),
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
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

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val trimmedContent = postContent.trim()
                            errorMessage = feedViewModel.validatePostContent(trimmedContent)
                                ?: if (!isOnline) offlineError else null

                            if (errorMessage == null) {
                                feedViewModel.addPost(trimmedContent)
                                postContent = ""
                            }
                        },
                        enabled = isOnline,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
                    ) {
                        Text(
                            stringResource(id = R.string.post_button_text),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            items(posts.reversed()) { post ->
                PostItem(post)
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    val avatarDrawableId = when (post.localAvatarFileName) {
        "profile_male_1" -> R.drawable.profile_male_1
        "profile_female_1" -> R.drawable.profile_female_1
        "profile_male_2" -> R.drawable.profile_male_2
        "profile_female_2" -> R.drawable.profile_female_2
        else -> R.drawable.profile_default
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFebede8))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = avatarDrawableId),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = post.username,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.1,
                        color = Color.Black
                    )
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(post.timestamp)),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize * 1.1,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.1,
                        color = Color.Black
                    )
                )
            }
        }
    }
}