package com.chorechampion.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.chorechampion.app.presentation.chores.ChoreViewModel
import com.chorechampion.app.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    choreViewModel: ChoreViewModel = hiltViewModel()
) {
    val allChores by choreViewModel.allChores.collectAsState()
    val categories by choreViewModel.allCategories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to profile */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home)) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_chores)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Chores.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_history)) },
                    selected = false,
                    onClick = { /* TODO: History */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_profile)) },
                    selected = false,
                    onClick = { /* TODO: Profile */ }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Chores.route) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_chore))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome to Chore Champion!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Quick Stats",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                label = "Total Chores",
                                value = allChores.size.toString(),
                                icon = Icons.Default.CheckCircle
                            )
                            StatItem(
                                label = "Categories",
                                value = categories.size.toString(),
                                icon = Icons.Default.Category
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "This Week's Assignments",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Track your weekly tasks and compete with your partner",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        FilledTonalButton(onClick = { navController.navigate(Screen.Assignments.route) }) {
                            Text("View")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "⚔️",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Chores Challenges",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create time-bound challenges and compete with your partner",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        FilledTonalButton(onClick = { navController.navigate(Screen.Challenges.route) }) {
                            Text("Challenges")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Manage Your Chores",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Create, edit, and organize your household tasks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        FilledTonalButton(onClick = { navController.navigate(Screen.Chores.route) }) {
                            Text("View All")
                        }
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🏆",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Weekly Results",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "View past evaluations and see who won each week",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        FilledTonalButton(onClick = { navController.navigate(Screen.History.route) }) {
                            Text("History")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Getting Started",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        GettingStartedItem(
                            number = 1,
                            title = "Create Chores",
                            description = "Add the household tasks you and your partner do"
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        GettingStartedItem(
                            number = 2,
                            title = "Assign Weekly Tasks",
                            description = "Set up chores for yourself and your partner each week"
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        GettingStartedItem(
                            number = 3,
                            title = "Complete & Track",
                            description = "Mark tasks done with photo proof and earn points"
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        GettingStartedItem(
                            number = 4,
                            title = "Weekly Winner",
                            description = "Automatic evaluation determines the winner each week"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GettingStartedItem(
    number: Int,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
