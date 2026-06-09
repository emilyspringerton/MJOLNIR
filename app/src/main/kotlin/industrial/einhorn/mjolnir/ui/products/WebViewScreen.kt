package industrial.einhorn.mjolnir.ui.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(url: String, onBack: () -> Unit) {
    val state = rememberWebViewState(url = url)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(url.substringAfterLast("/").ifBlank { url }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            WebView(
                state = state,
                modifier = Modifier.fillMaxSize(),
                onCreated = { wv ->
                    wv.settings.javaScriptEnabled = true
                    wv.settings.domStorageEnabled = true
                }
            )
        }
    }
}
