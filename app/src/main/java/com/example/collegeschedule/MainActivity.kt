package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.api.ScheduleApi
import com.example.collegeschedule.data.local.FavoritesManager
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp()
            }
        }
    }
}

@Composable
fun CollegeScheduleApp() {

    var currentDestination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }

    // 🔥 Это "глобально выбранная группа"
    var selectedGroupGlobal by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5071/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }

    NavigationSuiteScaffold(
        containerColor = Color(0xFF004D40),
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {

        when (currentDestination) {

            AppDestinations.HOME -> {
                ScheduleScreen(
                    repository = repository,
                    selectedGroupGlobal = selectedGroupGlobal,
                    onSelectedGroupChange = { selectedGroupGlobal = it }
                )
            }

            AppDestinations.FAVORITES -> {

                val context = LocalContext.current
                val favoritesManager = remember { FavoritesManager(context) }

                var favorites by remember {
                    mutableStateOf(favoritesManager.getFavorites().toList())
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Избранные группы",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (favorites.isEmpty()) {
                        Text("Нет избранных групп")
                    } else {

                        favorites.forEach { group ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        selectedGroupGlobal = group
                                        currentDestination = AppDestinations.HOME
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(group)

                                    IconButton(onClick = {
                                        favoritesManager.removeFavorite(group)
                                        favorites = favoritesManager.getFavorites().toList()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Remove favorite",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AppDestinations.PROFILE -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Профиль студента")
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}