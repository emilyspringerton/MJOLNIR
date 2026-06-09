package industrial.einhorn.mjolnir.ui.apples

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
import industrial.einhorn.mjolnir.data.model.Apple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppleDetailScreen(
    appleId: Long,
    onBack: () -> Unit,
    viewModel: AppleDetailViewModel = hiltViewModel()
) {
    val apple by viewModel.apple.collectAsState()

    LaunchedEffect(appleId) { viewModel.load(appleId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apple #$appleId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val a = apple) {
            null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> AppleDetail(apple = a, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun AppleDetail(apple: Apple, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TypeBadge(type = apple.appleType)
        Text(text = apple.title, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SourceChip(label = apple.sourceRepo)
            SourceChip(label = "by ${apple.agentId}")
        }
        HorizontalDivider()
        if (!apple.body.isNullOrBlank()) {
            Text(text = apple.body, style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }
        Text(
            text = "Filed: ${apple.recordedAt}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
