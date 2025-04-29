package com.example.practice.feature_garden.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.practice.R
import com.example.practice.components.BottomNavBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practice.common.network.rememberNetworkState
import com.example.practice.feature_garden.viewmodel.GardenViewModel
import com.example.practice.feature_garden.data.Plant
import com.example.practice.feature_naturebot.viewmodel.NatureBotViewModel
import com.example.practice.feature_profile.viewmodel.ProfileViewModel
import androidx.compose.ui.Alignment

@Composable
fun GardenScreen(
    navController: NavHostController,
    viewModel: GardenViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
) {
    // Fetch the user's shared plants when the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.getPlants()
    }

    // Observe plants fetched from Firestore
    val sharedPlants by viewModel.mySharedPlants.collectAsState()

    // Get the accessibility mode preference from the ProfileViewModel
    val accessibilityModeEnabled by profileViewModel.accessibilityEnabled.collectAsState()

    // Define the textStyle based on accessibility mode
    val textStyle = if (accessibilityModeEnabled) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp)
    }

    // Build garden slots (initialize with null if no plant is assigned)
    val gardenSlots = remember(sharedPlants) {
        val slots = MutableList<Plant?>(6) { null }
        sharedPlants.forEach { plant ->
            val index = (plant.slot - 1).takeIf { it in 0 until 6 } ?: return@forEach
            slots[index] = plant
        }
        slots
    }

    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // for getting plant info
    val isOnline = rememberNetworkState()  // This gives you whether the user is online or not
    var selectedPlantForDetails by remember { mutableStateOf<Plant?>(null) }

    // Define image resource map for the plant slots
    val imageResMap = mapOf(
        0 to R.drawable.plant1sort,
        1 to R.drawable.plant2sort,
        2 to R.drawable.plant3sort,
        3 to R.drawable.plant4sort,
        4 to R.drawable.plant1sort,
        5 to R.drawable.plant2sort
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController, accessibilityModeEnabled) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Garden title
            Text(
                text = stringResource(id = R.string.garden_title),
                style = MaterialTheme.typography.headlineLarge.copy(color = Color(0xFF2F6B2F)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Create 3 rows, each containing 2 plant slots
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (col in 0 until 2) {
                        val index = row * 2 + col
                        val plant = gardenSlots.getOrNull(index)
                        Box(modifier = Modifier.weight(1f)) {
                            // If the slot has a plant, display it, else show the "No Wi-Fi" icon if offline
                            if (plant != null && plant.name.isNotBlank()) {
                                // Show plant slot with the plant
                                PlantSlot(
                                    hasPlant = true,
                                    imageRes = imageResMap[(plant.slot - 1).coerceIn(0, 5)],
                                    name = plant.name,
                                    onClick = {
                                        selectedPlantForDetails = plant
                                        showDialog = true
                                    },
                                    onDelete = {
                                        viewModel.deletePlant(plant.id)
                                    }
                                )
                            } else {
                                // If no plant is assigned to this slot
                                if (isOnline) {
                                    // Show the add button if online
                                    PlantSlot(
                                        hasPlant = false,
                                        imageRes = null,
                                        name = "",
                                        onClick = {
                                            selectedSlot = index
                                            showDialog = true
                                        },
                                        onDelete = {}
                                    )
                                } else {
                                    // Show a "No Wi-Fi" icon if offline
                                    Box(
                                        modifier = Modifier
                                            .height(120.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.LightGray.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.no_wifi),  // Replace with your "No Wi-Fi" icon
                                            contentDescription = stringResource(id = R.string.no_wifi),
                                            tint = Color.Gray,
                                            modifier = Modifier.size(48.dp)  // Adjust size as needed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Show the plant details dialog if the plant is selected and showDialog is true
    if (showDialog && selectedPlantForDetails != null) {
        val selectedPlant = selectedPlantForDetails!!
        val imageRes = imageResMap[(selectedPlant.slot - 1).coerceIn(0, 5)]  // Get image for selected plant

        PlantDetailDialog(
            plant = selectedPlant,
            imageRes = imageRes,
            onDismiss = {
                showDialog = false
                selectedPlantForDetails = null  // Reset the selected plant
            }
        )
    }

    // Show the plant adding dialog if needed
    if (showDialog && selectedSlot != null) {
        AddPlantDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                val slotNumber = selectedSlot!! + 1
                val imageName = "plant${slotNumber}sort"
                viewModel.addPlant(name, name, imageName, slotNumber)
                showDialog = false
            }
        )
    }
}




@Composable
fun PlantSlot(
    hasPlant: Boolean,
    imageRes: Int?,
    name: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val accessibilityModeEnabled by profileViewModel.accessibilityEnabled.collectAsState()
    val textStyle = if (accessibilityModeEnabled) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF006400), RoundedCornerShape(16.dp))
        ) {
            if (!hasPlant || imageRes == null) {
                // Empty slot
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_plant), tint = Color.Gray)
                }
            } else {
                // Plant present
                Column {
                    // Top section: plant image
                    Box(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFF006400).copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = stringResource(id = R.string.plant_image),
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    // Bottom section: name and delete button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f)),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name,
                                style = textStyle,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete_plant), tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlantDetailDialog(
    plant: Plant,
    imageRes: Int?,
    onDismiss: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val accessibilityModeEnabled by profileViewModel.accessibilityEnabled.collectAsState()
    val textStyle = if (accessibilityModeEnabled) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp)
    }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f), // 90% height
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.close), style = textStyle)
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(), // Make Box take full width
                contentAlignment = Alignment.Center // Center the content horizontally
            ) {
                Text(
                    text = plant.name,
                    style = textStyle.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp) // Make the name bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Display the plant image
                imageRes?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = stringResource(id = R.string.plant_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Display the plant's soil type
                Text("Soil Type:", style = textStyle.copy(fontWeight = FontWeight.Bold)) // Bold variable
                Text(plant.soilType, style = textStyle) // Regular information
                Spacer(modifier = Modifier.height(8.dp))

                // Display watering frequency
                Text("Watering Frequency:", style = textStyle.copy(fontWeight = FontWeight.Bold)) // Bold variable
                Text(plant.wateringFrequency, style = textStyle) // Regular information
                Spacer(modifier = Modifier.height(8.dp))

                // Display sunlight hours
                Text("Sunlight Hours:", style = textStyle.copy(fontWeight = FontWeight.Bold)) // Bold variable
                Text(plant.sunlightHours, style = textStyle) // Regular information
                Spacer(modifier = Modifier.height(8.dp))

                // Display temperature range
                Text("Temperature Range:", style = textStyle.copy(fontWeight = FontWeight.Bold)) // Bold variable
                Text(plant.temperatureRange, style = textStyle) // Regular information
                Spacer(modifier = Modifier.height(8.dp))

                // Display common pests
                Text("Common Pests:", style = textStyle.copy(fontWeight = FontWeight.Bold)) // Bold variable
                plant.commonPests?.let { pests ->
                    pests.forEach { pest ->
                        Text("- $pest", style = textStyle) // Regular information
                    }
                } ?: Text("No pests listed", style = textStyle) // Regular information
                Spacer(modifier = Modifier.height(8.dp))

                // Display care tip
                Text("Care Tip:", style = textStyle.copy(fontWeight = FontWeight.Bold)) // Bold variable
                Text(plant.careTip, style = textStyle) // Regular information
            }
        }
    )
}




@Composable
fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }

    // Get the accessibility mode preference from the ProfileViewModel
    val accessibilityModeEnabled by profileViewModel.accessibilityEnabled.collectAsState()
    val textStyle = if (accessibilityModeEnabled) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onConfirm(name)
            }) { Text(stringResource(id = R.string.add_plant), style = textStyle) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.cancel), style = textStyle) }
        },
        title = { Text(stringResource(id = R.string.add_new_plant), style = textStyle) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.plant_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = textStyle
                )
            }
        }
    )
}
