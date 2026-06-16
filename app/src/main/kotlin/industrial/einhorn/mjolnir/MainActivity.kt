package industrial.einhorn.mjolnir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dagger.hilt.android.AndroidEntryPoint
import industrial.einhorn.mjolnir.data.repository.AuthRepository
import industrial.einhorn.mjolnir.ui.apples.AppleDetailScreen
import industrial.einhorn.mjolnir.ui.apples.ApplesFeedScreen
import industrial.einhorn.mjolnir.ui.chat.ChatScreen
import industrial.einhorn.mjolnir.ui.auth.LoginScreen
import industrial.einhorn.mjolnir.ui.heimdal.HeimdalScreen
import industrial.einhorn.mjolnir.ui.intelligence.CameraScreen
import industrial.einhorn.mjolnir.ui.intelligence.IntelligenceScreen
import industrial.einhorn.mjolnir.ui.intelligence.IntelligenceViewModel
import industrial.einhorn.mjolnir.ui.intelligence.ObservationDetailScreen
import industrial.einhorn.mjolnir.ui.products.ProductsScreen
import industrial.einhorn.mjolnir.ui.rsi.RsiScreen
import industrial.einhorn.mjolnir.ui.source.SourceBrowserScreen
import industrial.einhorn.mjolnir.ui.theme.MjolnirTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MjolnirTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isAuthenticated by authRepo.isAuthenticated.collectAsState(initial = false)
                    val navController = rememberNavController()

                    if (!isAuthenticated) {
                        LoginScreen(onLoginSuccess = {
                            navController.navigate("feed") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
                    } else {
                        NavHost(navController = navController, startDestination = "feed") {
                            composable("feed") {
                                ApplesFeedScreen(
                                    onAppleClick = { id -> navController.navigate("apple/$id") },
                                    onProductsClick = { navController.navigate("products") },
                                    onIntelligenceClick = { navController.navigate("intelligence") },
                                    onSourceClick = { navController.navigate("source") },
                                    onHeimdalClick = { navController.navigate("heimdal") },
                                    onRsiClick = { navController.navigate("rsi") },
                                    onEmilyPrimeChatClick = { navController.navigate("chat/EMILY_PRIME") },
                                    onFatBabyChatClick = { navController.navigate("chat/FATBABY_EMILY") },
                                )
                            }
                            composable(
                                route = "apple/{appleId}",
                                arguments = listOf(navArgument("appleId") { type = NavType.LongType }),
                                deepLinks = listOf(navDeepLink { uriPattern = "mjolnir://apple/{appleId}" })
                            ) { back ->
                                AppleDetailScreen(
                                    appleId = back.arguments!!.getLong("appleId"),
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "products",
                                deepLinks = listOf(navDeepLink { uriPattern = "mjolnir://product/{name}" })
                            ) {
                                ProductsScreen(onBack = { navController.popBackStack() })
                            }
                            // Intelligence
                            composable("intelligence") {
                                IntelligenceScreen(
                                    onOpenCamera = { navController.navigate("camera") },
                                    onOpenObservation = { id -> navController.navigate("observation/$id") },
                                )
                            }
                            composable("camera") {
                                val intelligenceVm: IntelligenceViewModel = hiltViewModel(
                                    navController.getBackStackEntry("intelligence")
                                )
                                CameraScreen(
                                    onImageCaptured = { jpegBytes ->
                                        intelligenceVm.submitImage(jpegBytes, prompt = null)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() },
                                )
                            }
                            composable(
                                route = "observation/{obsId}",
                                arguments = listOf(navArgument("obsId") { type = NavType.LongType }),
                            ) { back ->
                                ObservationDetailScreen(
                                    observationId = back.arguments!!.getLong("obsId"),
                                    onBack = { navController.popBackStack() },
                                )
                            }
                            // RSI loop state
                            composable("rsi") {
                                RsiScreen(onBack = { navController.popBackStack() })
                            }
                            // HEIMDAL sprint planning
                            composable("heimdal") {
                                HeimdalScreen(onBack = { navController.popBackStack() })
                            }
                            // Source browser
                            composable("source") {
                                SourceBrowserScreen(onBack = { navController.popBackStack() })
                            }
                            composable(
                                route = "source/{repo}",
                                arguments = listOf(navArgument("repo") { type = NavType.StringType }),
                            ) { back ->
                                SourceBrowserScreen(
                                    repoName = back.arguments!!.getString("repo"),
                                    onBack = { navController.popBackStack() },
                                )
                            }
                            // Chat screens
                            composable(
                                route = "chat/{mode}",
                                arguments = listOf(navArgument("mode") { type = NavType.StringType }),
                            ) {
                                ChatScreen(onBack = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
}
