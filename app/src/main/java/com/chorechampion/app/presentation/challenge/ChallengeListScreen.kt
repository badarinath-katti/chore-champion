package com.chorechampion.app.presentation.challenge

import androidx.compose.foundation.clickable
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
import com.chorechampion.app.domain.model.Challenge
import com.chorechampion.app.domain.model.ChallengeStatus
import com.chorechampion.app.presentation.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeListScreen(
    navController: NavController,
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    val activeChallenges by viewModel.activeChallenges.collectAsState()
    val upcomingChallenges by viewModel.upcomingChallenges.collectAsState()
    val completedChallenges by viewModel.completedChallenges.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "Upcoming", "Completed")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chores Challenges") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateChallenge.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Challenge")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> ChallengeList(
                    challenges = activeChallenges,
                    emptyMessage = "No active challenges.\nCreate one to get started!",
                    onChallengeClick = { challenge ->
                        navController.navigate(Screen.ChallengeDetail.createRoute(challenge.id))
                    }
                )
                1 -> ChallengeList(
                    challenges = upcomingChallenges,
                    emptyMessage = "No upcoming challenges",
                    onChallengeClick = { challenge ->
                        navController.navigate(Screen.ChallengeDetail.createRoute(challenge.id))
                    }
                )
                2 -> ChallengeList(
                    challenges = completedChallenges,
                    emptyMessage = "No completed challenges yet",
                    onChallengeClick = { challenge ->
                        navController.navigate(Screen.ChallengeDetail.createRoute(challenge.id))
                    }
                )
            }
        }
    }
}

@Composable
fun ChallengeList(
    challenges: List<Challenge>,
    emptyMessage: String,
    onChallengeClick: (Challenge) -> Unit
) {
    if (challenges.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challenges) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onClick = { onChallengeClick(challenge) }
                )
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with name and type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = challenge.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // Solo or Partner indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        if (challenge.isSolo()) Icons.Default.Person else Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (challenge.isSolo()) "Solo" else "Partner",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Description
            challenge.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            // Date range and progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${dateFormat.format(Date(challenge.startDate))} - ${dateFormat.format(Date(challenge.endDate))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${challenge.duration()} days total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status indicator
                if (challenge.isActive()) {
                    val daysRemaining = challenge.daysRemaining()
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "$daysRemaining days left",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                } else if (!challenge.hasStarted()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Starts soon",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Status chip
            StatusChip(status = challenge.status)
        }
    }
}

@Composable
fun StatusChip(status: ChallengeStatus) {
    val (color, text) = when (status) {
        ChallengeStatus.PENDING -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            "Pending"
        )
        ChallengeStatus.ACTIVE -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            "Active"
        )
        ChallengeStatus.COMPLETED -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            "Completed"
        )
        ChallengeStatus.CANCELLED -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            "Cancelled"
        )
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
