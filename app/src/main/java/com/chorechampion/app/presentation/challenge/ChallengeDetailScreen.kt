package com.chorechampion.app.presentation.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chorechampion.app.domain.model.*
import com.chorechampion.app.presentation.assignments.AssignmentViewModel
import com.chorechampion.app.presentation.chores.ChoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    challengeId: String,
    navController: NavController,
    viewModel: ChallengeViewModel = hiltViewModel(),
    choreViewModel: ChoreViewModel = hiltViewModel(),
    assignmentViewModel: AssignmentViewModel = hiltViewModel()
) {
    val challenge by viewModel.selectedChallenge.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val challengeCreator by viewModel.challengeCreator.collectAsState()
    val challengePartner by viewModel.challengePartner.collectAsState()
    val availableChores by choreViewModel.allChores.collectAsState()
    val challengeAssignments by assignmentViewModel.challengeAssignments.collectAsState()
    
    var showAssignChoreDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(challengeId) {
        viewModel.loadChallengeById(challengeId)
        assignmentViewModel.loadAssignmentsForChallenge(challengeId)
    }
    
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenge Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            if (challenge?.isActive() == true) {
                                DropdownMenuItem(
                                    text = { Text("End Challenge") },
                                    onClick = {
                                        viewModel.endChallenge(challengeId)
                                        showMoreMenu = false
                                        navController.navigateUp()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Cancel Challenge") },
                                onClick = {
                                    viewModel.cancelChallenge(challengeId)
                                    showMoreMenu = false
                                    navController.navigateUp()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Cancel, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showDeleteDialog = true
                                    showMoreMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (challenge?.isActive() == true || challenge?.status == ChallengeStatus.PENDING) {
                FloatingActionButton(
                    onClick = { showAssignChoreDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Assign Chore")
                }
            }
        }
    ) { padding ->
        challenge?.let { currentChallenge ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Challenge Info Card
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentChallenge.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusChip(status = currentChallenge.status)
                            }

                            currentChallenge.description?.let { desc ->
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Divider()

                            // Date Range
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = "Duration",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${dateFormat.format(Date(currentChallenge.startDate))} - ${dateFormat.format(Date(currentChallenge.endDate))}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${currentChallenge.duration()} days total",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Days Remaining (if active)
                            if (currentChallenge.isActive()) {
                                val daysRemaining = currentChallenge.daysRemaining()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = "$daysRemaining days remaining",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Challenge Type
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (currentChallenge.isSolo()) Icons.Default.Person else Icons.Default.People,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = if (currentChallenge.isSolo()) "Solo Challenge" else "Partner Challenge",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Invite Code (only for creator)
                            if (currentChallenge.creatorUserId == currentUser?.id) {
                                Divider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = "Invite Code",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = currentChallenge.inviteCode,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = androidx.compose.ui.unit.TextUnit(4f, androidx.compose.ui.unit.TextUnitType.Sp)
                                        )
                                        Text(
                                            text = "Share with your partner to join",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Participants Card
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Participants",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Creator
                            challengeCreator?.let { creator ->
                                ParticipantRow(
                                    user = creator,
                                    role = "Creator",
                                    isCurrentUser = creator.id == currentUser?.id
                                )
                            }

                            // Partner
                            if (challengePartner != null) {
                                Divider()
                                ParticipantRow(
                                    user = challengePartner!!,
                                    role = "Partner",
                                    isCurrentUser = challengePartner!!.id == currentUser?.id
                                )
                            } else if (!currentChallenge.isSolo()) {
                                Divider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                    Column {
                                        Text(
                                            text = "Waiting for partner",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Share the invite code to invite someone",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assigned Chores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${challengeAssignments.size} total",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Assigned Chores List
                if (challengeAssignments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Assignment,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "No chores assigned yet",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Tap the + button to assign chores",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val sharedChoreIds = challengeAssignments
                        .groupBy { it.choreId }
                        .filter { it.value.size > 1 }
                        .keys
                    items(challengeAssignments) { assignment ->
                        val participants = listOfNotNull(challengeCreator, challengePartner)
                        val choreName = availableChores
                            .firstOrNull { it.id == assignment.choreId }?.title
                            ?: "Unknown Chore"
                        val assignedToName = participants
                            .firstOrNull { it.id == assignment.assignedToUserId }?.name
                            ?: assignment.assignedToUserId.take(8)
                        ChallengeAssignmentCard(
                            assignment = assignment,
                            choreName = choreName,
                            assignedToName = assignedToName,
                            isShared = assignment.choreId in sharedChoreIds,
                            onDelete = { assignmentViewModel.deleteAssignment(assignment.id) }
                        )
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Assign Chore Dialog
    if (showAssignChoreDialog && challenge != null) {
        AssignChoreDialog(
            challenge = challenge!!,
            availableChores = availableChores,
            currentUser = currentUser,
            onDismiss = { showAssignChoreDialog = false },
            onAssign = { choreId, userIds, startDate, endDate ->
                assignmentViewModel.assignChoreToChallenge(
                    choreId = choreId,
                    userIds = userIds,
                    challengeId = challengeId,
                    startDate = startDate,
                    endDate = endDate
                )
                showAssignChoreDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Challenge?") },
            text = { Text("This will delete the challenge and all associated chore assignments. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteChallenge(challengeId)
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
fun ParticipantRow(user: com.chorechampion.app.domain.model.User, role: String, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = user.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (isCurrentUser) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "You",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = "$role · ${user.email}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChallengeAssignmentCard(
    assignment: WeeklyAssignment,
    choreName: String,
    assignedToName: String,
    isShared: Boolean = false,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = choreName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isShared) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Shared",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "Assigned to: $assignedToName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isShared) "${assignment.targetWeightage} pts (split)" else "${assignment.targetWeightage} points",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // Status Badge
                Surface(
                    color = when (assignment.status) {
                        com.chorechampion.app.data.local.entity.AssignmentStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                        com.chorechampion.app.data.local.entity.AssignmentStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
                        com.chorechampion.app.data.local.entity.AssignmentStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                        com.chorechampion.app.data.local.entity.AssignmentStatus.SKIPPED -> MaterialTheme.colorScheme.errorContainer
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = assignment.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete assignment",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Assignment?") },
            text = { Text("This will remove this chore assignment from the challenge.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignChoreDialog(
    challenge: Challenge,
    availableChores: List<Chore>,
    currentUser: User?,
    onDismiss: () -> Unit,
    onAssign: (choreId: String, userIds: List<String>, startDate: Long, endDate: Long) -> Unit
) {
    var selectedChore by remember { mutableStateOf<Chore?>(null) }
    var choreExpanded by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf(currentUser?.id ?: "") }
    var assignToBoth by remember { mutableStateOf(false) }
    var weekStartDate by remember { mutableStateOf(challenge.startDate) }
    var weekEndDate by remember { mutableStateOf(challenge.startDate + (7 * 24 * 60 * 60 * 1000L)) }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Assign Chore to Challenge",
                    style = MaterialTheme.typography.titleLarge
                )

                // Chore Selection
                ExposedDropdownMenuBox(
                    expanded = choreExpanded,
                    onExpandedChange = { choreExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedChore?.title ?: "Select a chore...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chore") },
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = choreExpanded) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = choreExpanded,
                        onDismissRequest = { choreExpanded = false }
                    ) {
                        availableChores.forEach { chore ->
                            DropdownMenuItem(
                                text = { Text(chore.title) },
                                onClick = {
                                    selectedChore = chore
                                    choreExpanded = false
                                }
                            )
                        }
                    }
                }

                // User Selection (for partner challenges)
                if (!challenge.isSolo()) {
                    Text(
                        text = "Assign to:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !assignToBoth && selectedUserId == challenge.creatorUserId,
                            onClick = { assignToBoth = false; selectedUserId = challenge.creatorUserId },
                            label = { Text("Me") }
                        )
                        challenge.partnerUserId?.let { partnerId ->
                            FilterChip(
                                selected = !assignToBoth && selectedUserId == partnerId,
                                onClick = { assignToBoth = false; selectedUserId = partnerId },
                                label = { Text("Partner") }
                            )
                            FilterChip(
                                selected = assignToBoth,
                                onClick = { assignToBoth = true; selectedUserId = "" },
                                label = { Text("Both (split points)") }
                            )
                        }
                    }
                    if (assignToBoth) {
                        val halfPoints = (selectedChore?.defaultWeightage ?: 10) / 2
                        Text(
                            text = "Each participant earns $halfPoints pts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Week: ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(weekStartDate))} - ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(weekEndDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            selectedChore?.let { chore ->
                                val userIds = if (assignToBoth) {
                                    listOfNotNull(challenge.creatorUserId, challenge.partnerUserId)
                                } else {
                                    listOf(selectedUserId)
                                }
                                onAssign(chore.id, userIds, weekStartDate, weekEndDate)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedChore != null && (assignToBoth || selectedUserId.isNotBlank())
                    ) {
                        Text("Assign")
                    }
                }
            }
        }
    }
}
