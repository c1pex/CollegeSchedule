package com.example.collegeschedule.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDropdown(
    groups: List<String>,
    selectedGroup: String?,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup != null && selectedGroup != text) {
            text = selectedGroup
        }
    }

    val filtered = groups.filter {
        it.contains(text, ignoreCase = true)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                expanded = true
            },
            label = { Text("Группа") },
            modifier = modifier.menuAnchor(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filtered.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group) },
                    onClick = {
                        text = group
                        expanded = false
                        onGroupSelected(group)
                    }
                )
            }
        }
    }
}
