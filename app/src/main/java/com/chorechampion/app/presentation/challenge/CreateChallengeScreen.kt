package com.chorechampion.app.presentation.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chorechampion.app.domain.model.User
import com.chorechampion.app.presentation.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeScreen(
    navController: NavController,
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    val createdChallenge by viewModel.createdChallenge.collectAsState()
    
    var challengeName by remember { mutableStateOf("") }
    var challengeDescription by remember { mutableStateOf("") }
    
    // Date/Time state
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    // Show invite code dialog when challenge is created
    if (createdChallenge != null) {
        InviteCodeDialog(
            inviteCode = createdChallenge!!.inviteCode,
            challengeName = createdChallenge!!.name,
            onDismiss = {
                viewModel.clearCreatedChallenge()
                navController.navigateUp()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Challenge") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Challenge Info Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Challenge Details",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = challengeName,
                        onValueChange = { challengeName = it },
                        label = { Text("Challenge Name") },
                        placeholder = { Text("e.g., Spring Cleaning Challenge") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = challengeDescription,
                        onValueChange = { challengeDescription = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("Add challenge details...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📢 Partner Pairing",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "After creating the challenge, you'll receive a unique invite code. Share this code with your partner so they can join!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // Date Range Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Challenge Duration",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Start Date
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                "Start Date",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                dateFormat.format(Date(startDate)),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // End Date
                    OutlinedButton(
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                "End Date",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                dateFormat.format(Date(endDate)),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Duration display
                    val durationDays = ((endDate - startDate) / (24 * 60 * 60 * 1000)).toInt()
                    Text(
                        text = "Duration: $durationDays days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Create Button
            Button(
                onClick = {
                    viewModel.createChallenge(
                        name = challengeName,
                        description = challengeDescription.ifBlank { null },
                        partnerId = null, // Partner joins later using invite code
                        startDate = startDate,
                        endDate = endDate
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = challengeName.isNotBlank() && endDate > startDate
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Challenge")
            }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            currentDate = startDate,
            onDateSelected = { 
                startDate = it
                // Ensure end date is after start date
                if (endDate <= startDate) {
                    endDate = startDate + (7 * 24 * 60 * 60 * 1000L)
                }
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            currentDate = endDate,
            minDate = startDate,
            onDateSelected = { 
                endDate = it
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    currentDate: Long,
    minDate: Long? = null,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return minDate?.let { utcTimeMillis >= it } ?: true
            }
        }
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun InviteCodeDialog(
    inviteCode: String,
    challengeName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Challenge Created!",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "\"$challengeName\" has been created successfully!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Divider()
                
                Text(
                    text = "Share this invite code with your partner:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = inviteCode,
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = androidx.compose.ui.unit.TextUnit(8f, androidx.compose.ui.unit.TextUnitType.Sp)
                    )
                }
                
                Text(
                    text = "Your partner can join by entering this code in the 'Join Challenge' screen.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got it!")
            }
        }
    )
}
