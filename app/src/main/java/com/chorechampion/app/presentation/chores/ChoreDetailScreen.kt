package com.chorechampion.app.presentation.chores

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chorechampion.app.R
import com.chorechampion.app.domain.model.Chore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreDetailScreen(
    choreId: String,
    navController: NavController,
    viewModel: ChoreViewModel = hiltViewModel()
) {
    val chore by viewModel.getChoreById(choreId).collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (chore == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chore Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val currentChore = chore!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentChore.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleLarge
                    )

                    DetailRow(
                        label = "Title",
                        value = currentChore.title
                    )

                    if (currentChore.description?.isNotBlank() == true) {
                        DetailRow(
                            label = "Description",
                            value = currentChore.description
                        )
                    }

                    DetailRow(
                        label = "Category",
                        value = categories.find { it.id == currentChore.categoryId }?.let {
                            "${it.icon} ${it.name}"
                        } ?: "Unknown"
                    )

                    DetailRow(
                        label = "Default Weightage",
                        value = "${currentChore.defaultWeightage}"
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Scoring Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text(
                        text = "• Completion 0-20% = 1 point × ${currentChore.defaultWeightage / 10f} = ${currentChore.defaultWeightage / 10f}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Completion 21-40% = 2 points × ${currentChore.defaultWeightage / 10f} = ${currentChore.defaultWeightage / 10f * 2}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Completion 41-60% = 3 points × ${currentChore.defaultWeightage / 10f} = ${currentChore.defaultWeightage / 10f * 3}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Completion 61-80% = 4 points × ${currentChore.defaultWeightage / 10f} = ${currentChore.defaultWeightage / 10f * 4}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Completion 81-100% = 5 points × ${currentChore.defaultWeightage / 10f} = ${currentChore.defaultWeightage / 10f * 5}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        EditChoreDialog(
            chore = currentChore,
            categories = categories,
            onDismiss = { showEditDialog = false },
            onConfirm = { updated ->
                viewModel.updateChore(updated)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Chore") },
            text = { Text("Are you sure you want to delete \"${currentChore.title}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteChore(currentChore)
                        showDeleteDialog = false
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChoreDialog(
    chore: Chore,
    categories: List<com.chorechampion.app.domain.model.ChoreCategory>,
    onDismiss: () -> Unit,
    onConfirm: (Chore) -> Unit
) {
    var title by remember { mutableStateOf(chore.title) }
    var description by remember { mutableStateOf(chore.description ?: "") }
    var selectedCategoryId by remember { mutableStateOf(chore.categoryId) }
    var weightage by remember { mutableStateOf(chore.defaultWeightage.toString()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.chore_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.chore_description)) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategoryId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.chore_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${category.icon} ${category.name}") },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = weightage,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            weightage = it
                        }
                    },
                    label = { Text(stringResource(R.string.chore_weightage)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("1-100 scale") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weightageValue = weightage.toIntOrNull()?.coerceIn(1, 100) ?: chore.defaultWeightage
                    val updated = chore.copy(
                        title = title,
                        description = description.ifBlank { null },
                        categoryId = selectedCategoryId,
                        defaultWeightage = weightageValue
                    )
                    onConfirm(updated)
                },
                enabled = title.isNotBlank() && selectedCategoryId.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
