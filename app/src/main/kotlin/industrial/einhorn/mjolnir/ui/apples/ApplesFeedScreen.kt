package industrial.einhorn.mjolnir.ui.apples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import industrial.einhorn.mjolnir.data.model.Apple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplesFeedScreen(
    onAppleClick: (Long) -> Unit,
    onProductsClick: () -> Unit,
    onIntelligenceClick: () -> Unit = {},
    onSourceClick: () -> Unit = {},
    viewModel: ApplesFeedViewModel = hiltViewModel()
) {
    val apples by viewModel.apples.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("APPLES") },
                actions = {
                    IconButton(onClick = onSourceClick) {
                        Icon(Icons.Filled.Code, "Source")
                    }
                    IconButton(onClick = onIntelligenceClick) {
                        Icon(Icons.Filled.CameraAlt, "Intelligence")
                    }
                    IconButton(onClick = onProductsClick) {
                        Icon(Icons.Outlined.OpenInNew, "Products")
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading && apples.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(apples, key = { it.id }) { apple ->
                    AppleCard(apple = apple, onClick = { onAppleClick(apple.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun AppleCard(apple: Apple, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TypeBadge(type = apple.appleType, modifier = Modifier.width(72.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = apple.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SourceChip(label = apple.sourceRepo)
                Text(
                    text = formatRelativeTime(apple.recordedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TypeBadge(type: String, modifier: Modifier = Modifier) {
    val color = when (type) {
        "improvement" -> MaterialTheme.colorScheme.primaryContainer
        "rsi_iteration" -> MaterialTheme.colorScheme.secondaryContainer
        "signal_observation" -> MaterialTheme.colorScheme.tertiaryContainer
        "escalation" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(color = color, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Text(
            text = type.replace("_", "\n"),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(4.dp),
            maxLines = 2
        )
    }
}

@Composable
fun SourceChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun formatRelativeTime(iso: String): String {
    return try {
        val parsed = java.time.Instant.parse(iso)
        val diff = java.time.Duration.between(parsed, java.time.Instant.now())
        when {
            diff.toMinutes() < 1 -> "just now"
            diff.toHours() < 1 -> "${diff.toMinutes()}m ago"
            diff.toDays() < 1 -> "${diff.toHours()}h ago"
            else -> "${diff.toDays()}d ago"
        }
    } catch (e: Exception) { iso.take(10) }
}
