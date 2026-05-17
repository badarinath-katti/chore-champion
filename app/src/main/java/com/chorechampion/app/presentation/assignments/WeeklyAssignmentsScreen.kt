package com.chorechampion.app.presentation.assignments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.chorechampion.app.data.local.entity.AssignmentStatus
import com.chorechampion.app.domain.model.WeeklyAssignment
import com.chorechampion.app.presentation.chores.ChoreViewModel
import com.chorechampion.app.presentation.navigation.Screen
import com.chorechampion.app.presentation.theme.StatusCompleted
import com.chorechampion.app.presentation.theme.StatusInProgress
import com.chorechampion.app.presentation.theme.StatusPending
import com.chorechampion.app.presentation.theme.StatusSkipped

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyAssignmentsScreen(
    navController: NavController,
    assignmentViewModel: AssignmentViewModel = hiltViewModel(),
    choreViewModel: ChoreViewModel = hiltViewModel()
) {
    val myAssignments by assignmentViewModel.myAssignments.collectAsState()
    val partnerAssignments by assignmentViewModel.partnerAssignments.collectAsState()
    val allChores by choreViewModel.allChores.collectAsState()
    val myPoints by assignmentViewModel.getMyPoints().collectAsState()
    val partnerPoints by assignmentViewModel.getPartnerPoints().collectAsState()
    
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("This Week's Assignments") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAssignDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Points Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PointsDisplay(
                        label = "Your Points",
                        points = myPoints
                    )
                    Divider(
                        modifier = Modifier
                            .height(60.dp)
                            .width(1.dp)
                    )
                    PointsDisplay(
                        label = "Partner Points",
                        points = partnerPoints
                    )
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("My Tasks (${myAssignments.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Partner Tasks (${partnerAssignments.size})") }
                )
            }

            // Content
            when (selectedTab) {
                0 -> AssignmentList(
                    assignments = myAssignments,
                    chores = allChores,
                    onStatusChange = { assignmentId, status ->
                        assignmentViewModel.updateAssignmentStatus(assignmentId, status)
                    },
                    onComplete = { assignmentId ->
                        navController.navigate(Screen.Completion.createRoute(assignmentId))
                    }
                )
                1 -> AssignmentList(
                    assignments = partnerAssignments,
                    chores = allChores,
                    isPartner = true,
                    onStatusChange = { _, _ -> /* Partner's tasks */ }
                )
            }
        }
    }

    if (showAssignDialog) {
        AssignChoreDialog(
            chores = allChores.filter { chore ->
                // Filter out already assigned chores for this week
                myAssignments.none { it.choreId == chore.id }
            },
            onDismiss = { showAssignDialog = false },
            onConfirm = { choreId, weightage ->
                // TODO: Get current user ID properly
                assignmentViewModel.createAssignment(choreId, "userId", weightage)
                showAssignDialog = false
            }
        )
    }
}

@Composable
fun PointsDisplay(label: String, points: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = points.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AssignmentList(
    assignments: List<WeeklyAssignment>,
    chores: List<com.chorechampion.app.domain.model.Chore>,
    isPartner: Boolean = false,
    onStatusChange: (String, AssignmentStatus) -> Unit,
    onComplete: ((String) -> Unit)? = null
) {
    if (assignments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isPartner) "No assignments yet" else "No tasks assigned yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!isPartner) {
                    Text(
                        text = "Tap + to assign a chore",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments, key = { it.id }) { assignment ->
                val chore = chores.find { it.id == assignment.choreId }
                if (chore != null) {
                    AssignmentCard(
                        assignment = assignment,
                        chore = chore,
                        isPartner = isPartner,
                        onStatusChange = onStatusChange,
                        onComplete = onComplete
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentCard(
    assignment: WeeklyAssignment,
    chore: com.chorechampion.app.domain.model.Chore,
    isPartner: Boolean,
    onStatusChange: (String, AssignmentStatus) -> Unit,
    onComplete: ((String) -> Unit)? = null
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                            maxLines = 1
                        )
                    }
                }
                
                if (!isPartner) {
                    Box {
                        AssistChip(
                            onClick = { showStatusMenu = true },
                            label = { Text(getStatusText(assignment.status)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = getStatusColor(assignment.status),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        )
                        
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false }
                        ) {
                            AssignmentStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(getStatusText(status)) },
                                    onClick = {
                                        onStatusChange(assignment.id, status)
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Circle,
                                            contentDescription = null,
                                            tint = getStatusColor(status),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                } else {
                    AssistChip(
                        onClick = { },
                        label = { Text(getStatusText(assignment.status)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                tint = getStatusColor(assignment.status),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Weightage: ${assignment.targetWeightage}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Max Points: ${assignment.targetWeightage / 10f * 5}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            // Complete button for pending/in-progress assignments
            if (!isPartner && 
                (assignment.status == AssignmentStatus.PENDING || assignment.status == AssignmentStatus.IN_PROGRESS) &&
                onComplete != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onComplete(assignment.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark as Complete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignChoreDialog(
    chores: List<com.chorechampion.app.domain.model.Chore>,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var selectedChoreId by remember { mutableStateOf(chores.firstOrNull()?.id ?: "") }
    var weightage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val selectedChore = chores.find { it.id == selectedChoreId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Chore") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedChore?.title ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Chore") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        chores.forEach { chore ->
                            DropdownMenuItem(
                                text = { Text(chore.title) },
                                onClick = {
                                    selectedChoreId = chore.id
                                    weightage = chore.defaultWeightage.toString()
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
                    label = { Text("Weightage for this week") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        selectedChore?.let {
                            Text("Default: ${it.defaultWeightage}")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weight = weightage.toIntOrNull()?.coerceIn(1, 100)
                        ?: selectedChore?.defaultWeightage ?: 10
                    onConfirm(selectedChoreId, weight)
                },
                enabled = selectedChoreId.isNotBlank()
            ) {
                Text("Assign")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

fun getStatusText(status: AssignmentStatus): String {
    return when (status) {
        AssignmentStatus.PENDING -> "Pending"
        AssignmentStatus.IN_PROGRESS -> "In Progress"
        AssignmentStatus.COMPLETED -> "Completed"
        AssignmentStatus.SKIPPED -> "Skipped"
    }
}

fun getStatusColor(status: AssignmentStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        AssignmentStatus.PENDING -> StatusPending
        AssignmentStatus.IN_PROGRESS -> StatusInProgress
        AssignmentStatus.COMPLETED -> StatusCompleted
        AssignmentStatus.SKIPPED -> StatusSkipped
    }
}
