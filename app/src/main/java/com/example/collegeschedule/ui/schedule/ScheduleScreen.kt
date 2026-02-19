package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.local.FavoritesManager
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.ui.components.GroupDropdown
import com.example.collegeschedule.utils.getWeekDateRange

@Composable
fun ScheduleScreen(
    repository: ScheduleRepository,
    selectedGroupGlobal: String?,
    onSelectedGroupChange: (String) -> Unit
) {

    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }

    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var groups by remember { mutableStateOf<List<String>>(emptyList()) }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var isFavoriteState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            groups = RetrofitInstance.api.getGroups()

            if (groups.isNotEmpty() && selectedGroupGlobal == null) {
                onSelectedGroupChange(groups[0])
            }

        } catch (e: Exception) {
            error = e.message
        }
    }

    LaunchedEffect(selectedGroupGlobal) {
        if (selectedGroupGlobal != null) {
            isFavoriteState = favoritesManager.isFavorite(selectedGroupGlobal)
        }
    }

    LaunchedEffect(selectedGroupGlobal) {
        selectedGroupGlobal ?: return@LaunchedEffect

        loading = true
        val (start, end) = getWeekDateRange()

        try {
            schedule = RetrofitInstance.api.getSchedule(selectedGroupGlobal, start, end)
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            GroupDropdown(
                groups = groups,
                selectedGroup = selectedGroupGlobal,
                onGroupSelected = { group ->
                    onSelectedGroupChange(group)
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(6.dp))

            if (selectedGroupGlobal != null) {
                IconButton(
                    onClick = {
                        val group = selectedGroupGlobal

                        if (group != null) {
                            if (isFavoriteState) {
                                favoritesManager.removeFavorite(group)
                            } else {
                                favoritesManager.addFavorite(group)
                            }
                            isFavoriteState = !isFavoriteState
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavoriteState)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavoriteState)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> CircularProgressIndicator()
            error != null -> Text("Ошибка: $error")
            else -> ScheduleList(schedule)
        }
    }
}
