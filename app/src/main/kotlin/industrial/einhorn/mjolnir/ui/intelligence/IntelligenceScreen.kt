package industrial.einhorn.mjolnir.ui.intelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import industrial.einhorn.mjolnir.data.model.Observation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelligenceScreen(
    onOpenCamera: () -> Unit,
    onOpenObservation: (Long) -> Unit,
    viewModel: IntelligenceViewModel = hiltViewModel(),
) {
    val observations by viewModel.observations.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    if (submitState is SubmitState.Success) {
        AlertDialog(
            onDismissRequest = { viewModel.resetSubmit() },
            title = { Text("Observation submitted") },
            text = { Text("Emily Prime will analyse the image and post a result to your Apple feed.") },
            confirmButton = { TextButton(onClick = { viewModel.resetSubmit() }) { Text("OK") } },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intelligence") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenCamera) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "New observation")
            }
        }
    ) { padding ->
        if (observations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No observations yet", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap the camera button to observe your environment.\nEmily Prime will analyse and archive the result.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(observations, key = { it.id }) { obs ->
                    ObservationCard(obs, onClick = { onOpenObservation(obs.id) })
                }
            }
        }
    }
}

@Composable
fun ObservationCard(obs: Observation, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                StatusChip(obs.status)
                Text(
                    obs.createdAt.take(16).replace("T", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!obs.prompt.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    obs.prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            val preview = obs.analysisPreview ?: obs.analysis
            if (!preview.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "done"       -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "pending", "processing" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else         -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(
            status,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
