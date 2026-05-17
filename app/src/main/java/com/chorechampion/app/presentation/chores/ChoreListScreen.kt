package com.chorechampion.app.presentation.chores

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chorechampion.app.R
import com.chorechampion.app.domain.model.Chore
import com.chorechampion.app.domain.model.ChoreCategory
import com.chorechampion.app.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreListScreen(
    navController: NavController,
    viewModel: ChoreViewModel = hiltViewModel()
) {
    val chores by viewModel.filteredChores.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_chores)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_chore))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Filter
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.filterByCategory(null) },
                        label = { Text("All") }
                    )
                }
                items(categories) { category ->
                    CategoryFilterChip(
                        category = category,
                        selected = selectedCategory == category.id,
                        onClick = {
                            viewModel.filterByCategory(
                                if (selectedCategory == category.id) null else category.id
                            )
                        }
                    )
                }
            }

            Divider()

            // Chore List
            if (chores.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No chores yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Tap + to add your first chore",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chores, key = { it.id }) { chore ->
                        ChoreItem(
                            chore = chore,
                            onClick = { navController.navigate(Screen.ChoreDetail.createRoute(chore.id)) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateChoreDialog(
            categories = categories,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, description, categoryId, weightage ->
                viewModel.createChore(title, description, categoryId, weightage)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CategoryFilterChip(
    category: ChoreCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(category.icon)
                Spacer(modifier = Modifier.width(4.dp))
                Text(category.name)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreItem(
    chore: Chore,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chore.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (chore.description?.isNotBlank() == true) {
                    Text(
                        text = chore.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                Text(
                    text = "Weightage: ${chore.defaultWeightage}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class DefaultChoreTemplate(
    val name: String,
    val description: String,
    val categoryName: String,
    val weightage: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChoreDialog(
    categories: List<ChoreCategory>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String, Int) -> Unit
) {
    // Default chore templates
    val defaultChores = remember {
        listOf(
            DefaultChoreTemplate("Vacuum Living Room", "Vacuum all carpets and rugs in living room", "Cleaning", 15),
            DefaultChoreTemplate("Mop Kitchen Floor", "Sweep and mop entire kitchen floor", "Cleaning", 12),
            DefaultChoreTemplate("Clean Bathroom", "Clean toilet, sink, shower/tub, and mirrors", "Cleaning", 20),
            DefaultChoreTemplate("Dust Furniture", "Dust all surfaces, shelves, and decorations", "Cleaning", 10),
            DefaultChoreTemplate("Take Out Trash", "Take out all trash bags and replace liners", "Cleaning", 8),
            DefaultChoreTemplate("Cook Dinner", "Prepare full dinner meal for both", "Cooking", 25),
            DefaultChoreTemplate("Meal Prep", "Prepare meals for the week", "Cooking", 30),
            DefaultChoreTemplate("Wash Dishes", "Wash, dry, and put away all dishes", "Cooking", 10),
            DefaultChoreTemplate("Grocery Shopping", "Buy groceries for the week", "Shopping", 20),
            DefaultChoreTemplate("Do Laundry", "Wash, dry, fold, and put away clothes", "Laundry", 18),
            DefaultChoreTemplate("Change Bed Sheets", "Remove and wash bed sheets, remake bed", "Laundry", 12),
            DefaultChoreTemplate("Iron Clothes", "Iron work clothes and dress shirts", "Laundry", 15),
            DefaultChoreTemplate("Fix Leaky Faucet", "Repair any dripping faucets", "Maintenance", 25),
            DefaultChoreTemplate("Change Air Filter", "Replace HVAC air filters", "Maintenance", 10),
            DefaultChoreTemplate("Yard Work", "Mow lawn, trim hedges, rake leaves", "Maintenance", 30),
            DefaultChoreTemplate("Walk Dog", "Take dog for 30-minute walk", "Other", 8),
            DefaultChoreTemplate("Water Plants", "Water all indoor and outdoor plants", "Other", 5)
        )
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var weightage by remember { mutableStateOf("10") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var templateExpanded by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<DefaultChoreTemplate?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_chore)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Default Chore Templates Dropdown
                ExposedDropdownMenuBox(
                    expanded = templateExpanded,
                    onExpandedChange = { templateExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTemplate?.name ?: "Select a default chore...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Quick Select") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = templateExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = templateExpanded,
                        onDismissRequest = { templateExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Custom Chore", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)) },
                            onClick = {
                                selectedTemplate = null
                                title = ""
                                description = ""
                                weightage = "10"
                                templateExpanded = false
                            }
                        )
                        defaultChores.forEach { template ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(template.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            template.categoryName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTemplate = template
                                    title = template.name
                                    description = template.description
                                    weightage = template.weightage.toString()
                                    // Find and set the category
                                    val categoryId = categories.find { it.name == template.categoryName }?.id
                                    if (categoryId != null) {
                                        selectedCategoryId = categoryId
                                    }
                                    templateExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it
                        selectedTemplate = null // Clear template when manually editing
                    },
                    label = { Text(stringResource(R.string.chore_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { 
                        description = it
                        selectedTemplate = null // Clear template when manually editing
                    },
                    label = { Text(stringResource(R.string.chore_description)) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategoryId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.chore_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${category.icon} ${category.name}") },
                                onClick = {
                                    selectedCategoryId = category.id
                                    categoryExpanded = false
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
                            selectedTemplate = null // Clear template when manually editing
                        }
                    },
                    label = { Text(stringResource(R.string.chore_weightage)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("1-100 scale (default: 10)") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weightageValue = weightage.toIntOrNull()?.coerceIn(1, 100) ?: 10
                    onConfirm(
                        title,
                        description.ifBlank { null },
                        selectedCategoryId,
                        weightageValue
                    )
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
