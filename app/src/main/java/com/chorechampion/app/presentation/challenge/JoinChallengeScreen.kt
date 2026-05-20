package com.chorechampion.app.presentation.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinChallengeScreen(
    navController: NavController,
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    var inviteCode by remember { mutableStateOf(TextFieldValue("")) }
    var joinAttempted by remember { mutableStateOf(false) }
    val joinError by viewModel.joinChallengeError.collectAsState()
    
    // Clear error when screen is opened
    LaunchedEffect(Unit) {
        viewModel.clearJoinError()
    }
    
    // Navigate back on successful join
    LaunchedEffect(joinError, joinAttempted) {
        if (joinAttempted && joinError == null && inviteCode.text.isNotEmpty()) {
            // Successfully joined - navigate back
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Challenge") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Instructions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter Invite Code",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ask the challenge creator for their 6-character invite code",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Invite Code Input
            OutlinedTextField(
                value = inviteCode,
                onValueChange = { 
                    if (it.text.length <= 6) {
                        inviteCode = it.copy(text = it.text.uppercase())
                        viewModel.clearJoinError()
                    }
                },
                label = { Text("Invite Code") },
                placeholder = { Text("ABC123") },
                singleLine = true,
                isError = joinError != null,
                supportingText = {
                    joinError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    } ?: Text("Enter the 6-character code")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(8f, androidx.compose.ui.unit.TextUnitType.Sp)
                )
            )

            // Join Button
            Button(
                onClick = {
                    if (inviteCode.text.length == 6) {
                        joinAttempted = true
                        viewModel.joinChallenge(inviteCode.text)
                    }
                },
                enabled = inviteCode.text.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Join Challenge",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• The creator generates a unique code when creating a challenge\n" +
                               "• Share the code with your partner\n" +
                               "• Enter the code here to join the challenge\n" +
                               "• Once joined, chores can be assigned to both partners",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
