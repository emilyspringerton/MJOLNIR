package industrial.einhorn.mjolnir.ui.heimdal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import industrial.einhorn.mjolnir.data.model.SprintItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeimdalScreen(
    onBack: () -> Unit,
    viewModel: HeimdalViewModel = hiltViewModel(),
) {
    val sprints by viewModel.sprints.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var requirement by remember { mutableStateOf("") }

    // Auto-clear input after successful submit
    LaunchedEffect(submitState) {
        if (submitState is SubmitSprintState.Success) {
            requirement = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HEIMDAL") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Requirement input panel
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sprint Requirement", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = requirement,
                        onValueChange = {
                            requirement = it
                            if (submitState is SubmitSprintState.Error || submitState is SubmitSprintState.Success) {
                                viewModel.resetSubmit()
                            }
                        },
                        placeholder = { Text("Describe what you want built…") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp),
                        maxLines = 6,
                        enabled = submitState !is SubmitSprintState.Loading,
                    )

                    when (val state = submitState) {
                        is SubmitSprintState.Error ->
                            Text(state.message, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        is SubmitSprintState.Success ->
                            Text("Sprint #${state.id} submitted — Emily Prime will queue it shortly.",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall)
                        else -> {}
                    }

                    Button(
                        onClick = { viewModel.submitRequirement(requirement) },
                        modifier = Modifier.align(Alignment.End),
                        enabled = requirement.isNotBlank() && submitState !is SubmitSprintState.Loading,
                    ) {
                        if (submitState is SubmitSprintState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Send to Emily")
                    }
                }
            }

            // Sprint list
            if (isRefreshing && sprints.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (sprints.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sprints yet — send your first requirement above.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(sprints, key = { it.id }) { sprint ->
                        SprintCard(sprint)
                    }
                }
            }
        }
    }
}

@Composable
private fun SprintCard(sprint: SprintItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("#${sprint.id}", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                SprintStatusChip(sprint.status)
            }
            Text(sprint.requirementPreview, style = MaterialTheme.typography.bodyMedium)
            if (sprint.roadmapId.isNotEmpty()) {
                Text(sprint.roadmapId,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary)
            }
            sprint.criteria?.takeIf { it.isNotEmpty() }?.let { criteria ->
                Spacer(Modifier.height(2.dp))
                Text("Criteria", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                criteria.forEach { c ->
                    Text("· ${c.name}: ${c.target}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SprintStatusChip(status: String) {
    val (label, color) = when (status) {
        "pending"     -> "Pending"     to MaterialTheme.colorScheme.outline
        "queued"      -> "Queued"      to MaterialTheme.colorScheme.secondary
        "in_progress" -> "In Progress" to MaterialTheme.colorScheme.primary
        "complete"    -> "Complete"    to MaterialTheme.colorScheme.tertiary
        "blocked"     -> "Blocked"     to MaterialTheme.colorScheme.error
        else          -> status        to MaterialTheme.colorScheme.outline
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.15f),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
