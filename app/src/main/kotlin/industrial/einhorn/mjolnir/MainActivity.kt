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
import industrial.einhorn.mjolnir.ui.auth.LoginScreen
import industrial.einhorn.mjolnir.ui.products.ProductsScreen
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
                                    onProductsClick = { navController.navigate("products") }
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
                        }
                    }
                }
            }
        }
    }
}
