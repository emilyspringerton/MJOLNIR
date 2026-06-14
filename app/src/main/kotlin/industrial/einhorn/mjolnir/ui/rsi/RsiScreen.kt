package industrial.einhorn.mjolnir.ui.rsi

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import industrial.einhorn.mjolnir.data.model.CycleState
import industrial.einhorn.mjolnir.data.model.DailyTokenStat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RsiScreen(
    onBack: () -> Unit,
    viewModel: RsiViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RSI Loop State") },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Emily Prime unreachable", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            state.error ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                    }
                }
                state.cycleState != null -> RsiContent(state.cycleState!!, state.tokenStats)
            }
        }
    }
}

@Composable
private fun RsiContent(state: CycleState, tokenStats: List<DailyTokenStat> = emptyList()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Token spend sparkline
        if (tokenStats.isNotEmpty()) {
            TokenSparklineCard(tokenStats)
        }

        // Cycle header
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cycle #${state.cycleNumber}", style = MaterialTheme.typography.headlineSmall)
                state.lastCycleAt?.let { ts ->
                    Text(
                        "Last run: ${formatTimestamp(ts)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Metrics row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard("Tasks done", state.metrics.tasksCompleted.toString(), Modifier.weight(1f))
            MetricCard("Iterations", state.metrics.itersRun.toString(), Modifier.weight(1f))
            MetricCard("Failures", state.metrics.consecFailures.toString(), Modifier.weight(1f))
        }

        // Active task
        if (state.activeTask != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACTIVE TASK", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text(state.activeTask.id, style = MaterialTheme.typography.titleSmall)
                    if (state.activeTask.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            state.activeTask.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 4
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    val iters = state.activeTask.iterations?.size ?: 0
                    Text(
                        "Status: ${state.activeTask.status}  ·  $iters/${state.activeTask.maxIters} iters",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "No active task — idle",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Next cycle plan
        if (!state.nextCyclePlan.isNullOrBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("NEXT CYCLE PLAN", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        state.nextCyclePlan,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 8
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatTimestamp(iso: String): String = try {
    val instant = Instant.parse(iso)
    DateTimeFormatter.ofPattern("MMM d HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(instant)
} catch (_: Exception) { iso.take(16) }

@Composable
private fun TokenSparklineCard(stats: List<DailyTokenStat>) {
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val maxTokens = stats.maxOfOrNull { it.tokens }?.takeIf { it > 0 } ?: 1L
    val totalTokens = stats.sumOf { it.tokens }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOKEN SPEND (7 DAYS)", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    formatTokenCount(totalTokens),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                val barCount = stats.size
                if (barCount == 0) return@Canvas
                val gap = 4.dp.toPx()
                val barWidth = (size.width - gap * (barCount - 1)) / barCount
                stats.forEachIndexed { i, stat ->
                    val x = i * (barWidth + gap)
                    val barHeight = (stat.tokens.toFloat() / maxTokens) * size.height
                    // Background track
                    drawRoundRect(
                        color = surfaceVariant,
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, size.height),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    // Filled bar
                    if (barHeight > 0f) {
                        drawRoundRect(
                            color = primary,
                            topLeft = Offset(x, size.height - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            // Day labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                stats.forEach { stat ->
                    Text(
                        stat.date.takeLast(5), // MM-DD
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatTokenCount(tokens: Long): String = when {
    tokens >= 1_000_000 -> "%.1fM".format(tokens / 1_000_000.0)
    tokens >= 1_000 -> "%.1fK".format(tokens / 1_000.0)
    else -> tokens.toString()
}
