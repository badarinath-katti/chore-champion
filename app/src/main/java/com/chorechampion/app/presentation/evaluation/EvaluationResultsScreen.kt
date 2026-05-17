package com.chorechampion.app.presentation.evaluation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationResultsScreen(
    weekStartDate: Long? = null,
    navController: NavController,
    viewModel: EvaluationViewModel = hiltViewModel()
) {
    val evaluationState by viewModel.evaluationState.collectAsState()

    LaunchedEffect(weekStartDate) {
        if (weekStartDate != null) {
            viewModel.loadEvaluationForWeek(weekStartDate)
        } else {
            viewModel.loadLatestEvaluation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = evaluationState) {
            is EvaluationState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is EvaluationState.Success -> {
                EvaluationContent(
                    evaluation = state.evaluation,
                    user1Name = state.user1.name,
                    user2Name = state.user2.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            is EvaluationState.Error -> {
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
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationContent(
    evaluation: com.chorechampion.app.domain.model.WeeklyEvaluation,
    user1Name: String,
    user2Name: String,
    modifier: Modifier = Modifier
) {
    val winnerName = when (evaluation.winnerId) {
        evaluation.user1Id -> user1Name
        evaluation.user2Id -> user2Name
        else -> null // Tie
    }
    
    val winnerPoints = when (evaluation.winnerId) {
        evaluation.user1Id -> evaluation.user1Points
        evaluation.user2Id -> evaluation.user2Points
        else -> evaluation.user1Points // For tie, show same points
    }

    // Animation values
    var animationPlayed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Week Info
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val weekEndDate = evaluation.weekStartDate + (7 * 24 * 60 * 60 * 1000) - 1
        
        Text(
            text = "${dateFormat.format(Date(evaluation.weekStartDate))} - ${dateFormat.format(Date(weekEndDate))}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Winner Announcement
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (winnerName != null) "🏆" else "🤝",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
                
                Text(
                    text = if (winnerName != null) "Winner!" else "It's a Tie!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (winnerName != null) {
                    Text(
                        text = winnerName,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = "${String.format("%.1f", winnerPoints)} points",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Score Breakdown
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Score Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Divider()
                
                ScoreRow(
                    name = user1Name,
                    points = evaluation.user1Points,
                    isWinner = evaluation.winnerId == evaluation.user1Id,
                    totalPoints = evaluation.user1Points + evaluation.user2Points
                )
                
                ScoreRow(
                    name = user2Name,
                    points = evaluation.user2Points,
                    isWinner = evaluation.winnerId == evaluation.user2Id,
                    totalPoints = evaluation.user1Points + evaluation.user2Points
                )
            }
        }

        // Stats Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Week Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                StatRow(
                    label = "Total Points",
                    value = String.format("%.1f", evaluation.user1Points + evaluation.user2Points)
                )
                
                StatRow(
                    label = "Point Difference",
                    value = String.format("%.1f", kotlin.math.abs(evaluation.user1Points - evaluation.user2Points))
                )
                
                val evaluatedDate = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                    .format(Date(evaluation.evaluatedAt))
                StatRow(
                    label = "Evaluated",
                    value = evaluatedDate
                )
            }
        }

        // Close Button
        Button(
            onClick = { /* Navigate back or to home */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun ScoreRow(
    name: String,
    points: Float,
    isWinner: Boolean,
    totalPoints: Float
) {
    val percentage = if (totalPoints > 0) (points / totalPoints) * 100 else 0f
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
                )
                if (isWinner) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Winner",
                        tint = Color(0xFFFFD700), // Gold
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = String.format("%.1f", points),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
