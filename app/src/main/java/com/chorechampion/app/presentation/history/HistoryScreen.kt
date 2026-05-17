package com.chorechampion.app.presentation.history

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
import com.chorechampion.app.domain.model.WeeklyEvaluation
import com.chorechampion.app.presentation.evaluation.EvaluationViewModel
import com.chorechampion.app.presentation.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: EvaluationViewModel = hiltViewModel()
) {
    val recentEvaluations by viewModel.recentEvaluations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (recentEvaluations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No evaluation history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Complete some chores to see results!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Past Weeks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(recentEvaluations, key = { it.id }) { evaluation ->
                    EvaluationHistoryCard(
                        evaluation = evaluation,
                        onClick = {
                            navController.navigate(
                                Screen.EvaluationResults.createRoute(evaluation.weekStartDate)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EvaluationHistoryCard(
    evaluation: WeeklyEvaluation,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val weekEndDate = evaluation.weekStartDate + (7 * 24 * 60 * 60 * 1000) - 1
    
    val winnerIcon = if (evaluation.winnerId != null) "🏆" else "🤝"
    val totalPoints = evaluation.user1Points + evaluation.user2Points
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = winnerIcon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = "${dateFormat.format(Date(evaluation.weekStartDate))} - ${dateFormat.format(Date(weekEndDate))}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (evaluation.winnerId != null) "Winner declared" else "Tie game",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "User 1: ${String.format("%.1f", evaluation.user1Points)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (evaluation.winnerId == evaluation.user1Id) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "User 2: ${String.format("%.1f", evaluation.user2Points)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (evaluation.winnerId == evaluation.user2Id) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
