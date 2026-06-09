package industrial.einhorn.mjolnir.ui.source

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import industrial.einhorn.mjolnir.data.local.MultiRepoSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class FileNode(
    val name: String,
    val path: String,     // relative to repo root
    val isDir: Boolean,
    val depth: Int,
)

@HiltViewModel
class SourceBrowserViewModel @Inject constructor(
    private val app: Application,
) : AndroidViewModel(app) {

    private val baseDir get() = File(app.filesDir, "src-repos")

    val repos: List<String> get() = MultiRepoSyncWorker.REPOS.map { it.first }

    private val _tree = MutableStateFlow<List<FileNode>>(emptyList())
    val tree: StateFlow<List<FileNode>> = _tree

    private val _fileContent = MutableStateFlow<String?>(null)
    val fileContent: StateFlow<String?> = _fileContent

    private val _currentRepo = MutableStateFlow<String?>(null)
    val currentRepo: StateFlow<String?> = _currentRepo

    private val _currentFile = MutableStateFlow<String?>(null)
    val currentFile: StateFlow<String?> = _currentFile

    fun selectRepo(repoName: String) {
        _currentRepo.value = repoName
        _fileContent.value = null
        _currentFile.value = null
        viewModelScope.launch {
            _tree.value = buildTree(repoName)
        }
    }

    fun openFile(node: FileNode) {
        if (node.isDir) return
        _currentFile.value = node.path
        viewModelScope.launch {
            val repoDir = File(baseDir, _currentRepo.value ?: return@launch)
            val file = File(repoDir, node.path)
            _fileContent.value = withContext(Dispatchers.IO) {
                try {
                    val text = file.readText()
                    if (text.length > 200_000) text.take(200_000) + "\n\n[truncated]" else text
                } catch (e: Exception) {
                    "[error reading file: ${e.message}]"
                }
            }
        }
    }

    fun clearFile() {
        _fileContent.value = null
        _currentFile.value = null
    }

    fun isRepoAvailable(repoName: String): Boolean =
        File(File(baseDir, repoName), ".git").exists()

    private suspend fun buildTree(repoName: String): List<FileNode> =
        withContext(Dispatchers.IO) {
            val repoDir = File(baseDir, repoName)
            if (!repoDir.exists()) return@withContext emptyList()
            val nodes = mutableListOf<FileNode>()
            fun walk(dir: File, depth: Int) {
                val children = dir.listFiles()
                    ?.filter { it.name != ".git" }
                    ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                    ?: return
                for (child in children) {
                    val relative = child.relativeTo(repoDir).path
                    nodes += FileNode(child.name, relative, child.isDirectory, depth)
                    if (child.isDirectory && depth < 4) walk(child, depth + 1)
                }
            }
            walk(repoDir, 0)
            nodes
        }
}
