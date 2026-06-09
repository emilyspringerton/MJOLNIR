package industrial.einhorn.mjolnir.ui.intelligence

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import industrial.einhorn.mjolnir.data.model.Observation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationDetailScreen(
    observationId: Long,
    onBack: () -> Unit,
    viewModel: IntelligenceViewModel = hiltViewModel(),
) {
    var obs by remember { mutableStateOf<Observation?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(observationId) {
        viewModel.repository.getObservation(observationId)
            .onSuccess { obs = it; loading = false }
            .onFailure { e -> error = e.message; loading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Observation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", modifier = Modifier.padding(16.dp))
                obs != null -> ObservationDetail(obs!!)
            }
        }
    }
}

@Composable
private fun ObservationDetail(obs: Observation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ObservationCard(obs = obs, onClick = {})
        }

        if (!obs.analysis.isNullOrBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Analysis", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(obs.analysis, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else if (obs.status == "pending" || obs.status == "processing") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Emily Prime is analysing this image…", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if ((obs.appleId ?: 0) > 0) {
            Text(
                "Apple #${obs.appleId} — view in feed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
