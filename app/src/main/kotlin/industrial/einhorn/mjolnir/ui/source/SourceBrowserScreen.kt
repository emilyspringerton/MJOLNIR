package industrial.einhorn.mjolnir.ui.source

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceBrowserScreen(
    repoName: String? = null,
    onBack: () -> Unit,
    viewModel: SourceBrowserViewModel = hiltViewModel(),
) {
    LaunchedEffect(repoName) {
        if (repoName != null) viewModel.selectRepo(repoName)
    }

    val currentRepo by viewModel.currentRepo.collectAsState()
    val tree by viewModel.tree.collectAsState()
    val fileContent by viewModel.fileContent.collectAsState()
    val currentFile by viewModel.currentFile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentFile ?: currentRepo ?: "Source") },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            fileContent != null -> viewModel.clearFile()
                            currentRepo != null -> {
                                viewModel.clearFile()
                                // pop back to repo list — handled by navigating back
                                onBack()
                            }
                            else -> onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            fileContent != null -> CodeViewer(fileContent!!, Modifier.padding(padding))
            currentRepo != null -> FileTree(tree, viewModel, Modifier.padding(padding))
            else -> RepoList(viewModel, onRepoSelected = { viewModel.selectRepo(it) }, Modifier.padding(padding))
        }
    }
}

@Composable
private fun RepoList(
    viewModel: SourceBrowserViewModel,
    onRepoSelected: (String) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(viewModel.repos) { repo ->
            val available = viewModel.isRepoAvailable(repo)
            ListItem(
                headlineContent = { Text(repo) },
                supportingContent = {
                    Text(if (available) "synced" else "not synced — needs WiFi sync",
                        color = if (available) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                },
                leadingContent = { Icon(Icons.Filled.FolderOpen, contentDescription = null) },
                modifier = Modifier.clickable(enabled = available) { onRepoSelected(repo) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun FileTree(
    tree: List<FileNode>,
    viewModel: SourceBrowserViewModel,
    modifier: Modifier,
) {
    if (tree.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No files — repo not synced yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(tree, key = { it.path }) { node ->
            FileNodeRow(node, onClick = { viewModel.openFile(node) })
        }
    }
}

@Composable
private fun FileNodeRow(node: FileNode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !node.isDir, onClick = onClick)
            .padding(
                start = (16 + node.depth * 16).dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 10.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            if (node.isDir) Icons.Filled.FolderOpen else Icons.Filled.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (node.isDir) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            node.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (node.isDir) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CodeViewer(content: String, modifier: Modifier) {
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        Text(
            text = content,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFFD4D4D4),
            lineHeight = 18.sp,
            modifier = Modifier
                .verticalScroll(vScroll)
                .horizontalScroll(hScroll)
                .padding(16.dp),
        )
    }
}
