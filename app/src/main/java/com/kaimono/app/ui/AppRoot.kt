package com.kaimono.app.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaimono.app.ui.screens.AuthScreen
import com.kaimono.app.ui.screens.BrowseScreen
import com.kaimono.app.ui.screens.DashboardScreen
import com.kaimono.app.ui.screens.ExtensionsScreen
import com.kaimono.app.ui.screens.HistoryScreen
import com.kaimono.app.ui.screens.LibraryScreen
import com.kaimono.app.ui.screens.MangaDetailScreen
import com.kaimono.app.ui.screens.ReaderScreen
import com.kaimono.app.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

val LocalSnack = staticCompositionLocalOf<SnackbarHostState> { error("LocalSnack not provided") }

private data class DrawerItem(val route: String, val label: String, val icon: ImageVector)

private val drawerItems = listOf(
    DrawerItem("dashboard", "Dashboard", Icons.Filled.Dashboard),
    DrawerItem("library", "My Library", Icons.Filled.LibraryBooks),
    DrawerItem("browse", "Browse Sources", Icons.Filled.Explore),
    DrawerItem("history", "History", Icons.Filled.History),
    DrawerItem("extensions", "Extensions", Icons.Filled.Extension),
    DrawerItem("settings", "Settings", Icons.Filled.Settings),
)

@Composable
fun AppRoot(vm: AppViewModel) {
    val theme by vm.theme.collectAsState()
    val user by vm.user.collectAsState()
    KaimonoTheme(theme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (user == null) AuthScreen(vm) else MainScaffold(vm, user ?: "Reader")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(vm: AppViewModel, user: String) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snack = remember { SnackbarHostState() }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    CompositionLocalProvider(LocalSnack provides snack) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.MenuBook, null,
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.padding(4.dp))
                        Text("Kaimono", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)
                    }
                    Text(user, style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp))
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            selected = route == item.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                nav.navigate(item.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(titleFor(route)) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                    )
                },
                snackbarHost = { SnackbarHost(snack) },
            ) { padding ->
                NavHost(
                    navController = nav,
                    startDestination = "dashboard",
                    modifier = Modifier.padding(padding),
                ) {
                    composable("dashboard") { DashboardScreen(vm, nav) }
                    composable("library") { LibraryScreen(vm, nav) }
                    composable("browse") { BrowseScreen(vm, nav) }
                    composable("history") { HistoryScreen(vm, nav) }
                    composable("extensions") { ExtensionsScreen(vm) }
                    composable("settings") { SettingsScreen(vm) }
                    composable("manga/{mangaId}") { entry ->
                        MangaDetailScreen(vm, nav, entry.arguments?.getString("mangaId").orEmpty())
                    }
                    composable("reader/{chapterId}") { entry ->
                        ReaderScreen(vm, nav, entry.arguments?.getString("chapterId").orEmpty())
                    }
                }
            }
        }
    }
}

private fun titleFor(route: String?): String = when {
    route == null -> "Kaimono"
    route.startsWith("manga/") -> "Title details"
    route.startsWith("reader/") -> "Reader"
    else -> drawerItems.firstOrNull { it.route == route }?.label ?: "Kaimono"
}
