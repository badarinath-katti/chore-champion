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
    val availablePartners by viewModel.availablePartners.collectAsState()
    
    var challengeName by remember { mutableStateOf("") }
    var challengeDescription by remember { mutableStateOf("") }
    var selectedPartner by remember { mutableStateOf<User?>(null) }
    var isSoloChallenge by remember { mutableStateOf(false) }
    
    // Listen for partner selection from navigation
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<String?>("selected_partner_id", null)
            ?.collect { partnerId ->
                if (partnerId != null) {
                    // Fetch partner directly from viewModel
                    viewModel.getPartnerById(partnerId)
                    navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_partner_id")
                }
            }
    }
    
    // Observe selected partner from viewModel
    val selectedPartnerFromViewModel by viewModel.selectedPartner.collectAsState()
    LaunchedEffect(selectedPartnerFromViewModel) {
        if (selectedPartnerFromViewModel != null) {
            selectedPartner = selectedPartnerFromViewModel
        }
    }
    
    // Date/Time state
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

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

            // Partner Selection Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Partner Selection",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSoloChallenge,
                            onCheckedChange = { 
                                isSoloChallenge = it
                                if (it) selectedPartner = null
                            }
                        )
                        Text("Solo Challenge (No Partner)")
                    }

                    if (!isSoloChallenge) {
                        OutlinedButton(
                            onClick = { 
                                navController.navigate(Screen.PartnerSelection.route)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                if (selectedPartner != null) Icons.Default.Person else Icons.Default.PersonAdd,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = if (selectedPartner != null) "Partner" else "Select Partner",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = selectedPartner?.name ?: "Tap to choose",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                selectedPartner?.email?.let { email ->
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    }
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
                        partnerId = if (isSoloChallenge) null else selectedPartner?.id,
                        startDate = startDate,
                        endDate = endDate
                    )
                    navController.navigateUp()
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
