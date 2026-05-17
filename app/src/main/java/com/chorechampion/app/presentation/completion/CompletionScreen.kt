package com.chorechampion.app.presentation.completion

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chorechampion.app.R
import com.chorechampion.app.domain.model.WeeklyAssignment
import com.chorechampion.app.presentation.chores.ChoreViewModel
import com.chorechampion.app.util.CameraManager
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletionScreen(
    assignmentId: String,
    navController: NavController,
    completionViewModel: CompletionViewModel = hiltViewModel(),
    choreViewModel: ChoreViewModel = hiltViewModel(),
    assignmentViewModel: com.chorechampion.app.presentation.assignments.AssignmentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var assignment by remember { mutableStateOf<WeeklyAssignment?>(null) }
    var chore by remember { mutableStateOf<com.chorechampion.app.domain.model.Chore?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val completionPercentage by completionViewModel.completionPercentage.collectAsState()
    val photoUri by completionViewModel.photoUri.collectAsState()
    val notes by completionViewModel.notes.collectAsState()
    val completionState by completionViewModel.completionState.collectAsState()
    
    val allAssignments by assignmentViewModel.weeklyAssignments.collectAsState()
    val allChores by choreViewModel.allChores.collectAsState()

    val calculatedPoints = remember(completionPercentage, assignment?.targetWeightage) {
        assignment?.let { 
            com.chorechampion.app.util.ScoringCalculator.calculateFinalPoints(
                completionPercentage, 
                it.targetWeightage
            )
        } ?: 0f
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            showCamera = true
        }
    }

    // Load assignment and chore data
    LaunchedEffect(assignmentId, allAssignments, allChores) {
        assignment = allAssignments.find { it.id == assignmentId }
        assignment?.let { assign ->
            chore = allChores.find { it.id == assign.choreId }
        }
        // Load existing completion if any
        completionViewModel.getExistingCompletion(assignmentId)
    }

    LaunchedEffect(completionState) {
        when (completionState) {
            is CompletionState.Success -> {
                navController.navigateUp()
            }
            else -> {}
        }
    }

    if (showCamera) {
        CameraScreen(
            onPhotoCaptured = { uri ->
                completionViewModel.setPhotoUri(uri)
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Complete Chore") },
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
                // Chore Info Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chore Details",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Title: ${chore?.title ?: "Loading..."}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        chore?.description?.takeIf { it.isNotBlank() }?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Weightage: ${assignment?.targetWeightage ?: 0}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Completion Percentage
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Completion",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$completionPercentage%",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = completionPercentage.toFloat(),
                            onValueChange = { completionViewModel.updateCompletionPercentage(it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 19,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { completionViewModel.updateCompletionPercentage(0) }) {
                                Text("0%")
                            }
                            TextButton(onClick = { completionViewModel.updateCompletionPercentage(50) }) {
                                Text("50%")
                            }
                            TextButton(onClick = { completionViewModel.updateCompletionPercentage(100) }) {
                                Text("100%")
                            }
                        }
                    }
                }

                // Points Preview
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {(assignment?.targetWeightage ?: 0)
                        Column {
                            Text(
                                text = "Points You'll Earn",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Base: ${com.chorechampion.app.util.ScoringCalculator.getBasePoints(completionPercentage)} × Multiplier: ${30 / 10f}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = String.format("%.1f", calculatedPoints),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Photo Section
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Photo Proof (Optional)",
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (photoUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Chore photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { completionViewModel.setPhotoUri(null) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            MaterialTheme.shapes.small
                                        )
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove photo")
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (hasCameraPermission) {
                                    showCamera = true
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (photoUri == null) "Take Photo" else "Retake Photo")
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { completionViewModel.updateNotes(it) },
                    label = { Text(stringResource(R.string.notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Add any notes about completing this chore...") }
                )

                // Submit Button
                Button(
                    onClick = {
                        assignment?.let { completionViewModel.completeChore(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = completionState !is CompletionState.Loading
                ) {
                    if (completionState is CompletionState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Complete")
                    }
                }

                if (completionState is CompletionState.Error) {
                    Text(
                        text = (completionState as CompletionState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val cameraManager = remember { CameraManager(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.shutdown()
            executor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Photo") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also {
                        previewView = it
                        coroutineScope.launch {
                            try {
                                cameraManager.startCamera(lifecycleOwner, it, executor)
                            } catch (e: Exception) {
                                // Handle camera error
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val uri = cameraManager.takePhoto()
                            onPhotoCaptured(uri)
                        } catch (e: Exception) {
                            // Handle photo capture error
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture")
            }
        }
    }
}
