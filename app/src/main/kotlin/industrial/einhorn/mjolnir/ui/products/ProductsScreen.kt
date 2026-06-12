package industrial.einhorn.mjolnir.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import industrial.einhorn.mjolnir.BuildConfig

private data class Product(val name: String, val url: String, val description: String)

private val products = listOf(
    Product("FatBaby News", "${BuildConfig.IDUNA_BASE_URL.replace(":8090", ":8082")}", "Filing reader + signals"),
    Product("Signal API", "${BuildConfig.IDUNA_BASE_URL.replace(":8090", ":8083")}", "Query intelligence signals"),
    Product("IDUNA Admin", "${BuildConfig.IDUNA_BASE_URL}/admin", "IAM back office"),
    Product(
        "TYLER Episodes",
        "https://raw.githubusercontent.com/emilyspringerton/TYLER/main/EPISODES.md",
        "Episode index — S1–S7, 52 episodes, Build 0082 current"
    ),
    Product(
        "SHANKPIT Server",
        "${BuildConfig.IDUNA_BASE_URL.replace(":8090", ":6969")}/health",
        "Game server health — UDP :6969, Go backend"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(onBack: () -> Unit) {
    var selectedUrl by remember { mutableStateOf<String?>(null) }

    if (selectedUrl != null) {
        WebViewScreen(url = selectedUrl!!, onBack = { selectedUrl = null })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            products.forEach { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = { selectedUrl = product.url }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(product.name, style = MaterialTheme.typography.titleSmall)
                        Text(product.description, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(product.url, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
