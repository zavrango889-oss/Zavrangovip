package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ControlCenterScreen
import com.example.ui.screens.FpsGraphicsScreen
import com.example.ui.screens.NotificationControlScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.SmartOptimizeScreen
import com.example.ui.screens.TouchSmoothnessScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedBright
import com.example.ui.theme.NeonRedMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.ZavrangoBoostTheme
import com.example.viewmodel.BoosterViewModel

sealed class AppNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Control : AppNavTab("Control", Icons.Filled.Bolt, Icons.Outlined.Bolt, "nav_tab_control")
    object Performance : AppNavTab("Hardware", Icons.Filled.Memory, Icons.Outlined.Memory, "nav_tab_performance")
    object Fps : AppNavTab("FPS & Game", Icons.Filled.RocketLaunch, Icons.Outlined.RocketLaunch, "nav_tab_fps")
    object Touch : AppNavTab("Touch", Icons.Filled.TouchApp, Icons.Outlined.TouchApp, "nav_tab_touch")
    object Notifications : AppNavTab("Silence", Icons.Filled.Notifications, Icons.Outlined.Notifications, "nav_tab_notifications")
    object Optimize : AppNavTab("Optimize", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_optimize")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZavrangoBoostTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val viewModel: BoosterViewModel = viewModel()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val navTabs = listOf(
        AppNavTab.Control,
        AppNavTab.Performance,
        AppNavTab.Fps,
        AppNavTab.Touch,
        AppNavTab.Notifications,
        AppNavTab.Optimize
    )

    // Handle system back navigation when not on home screen
    if (selectedTabIndex != 0) {
        BackHandler {
            selectedTabIndex = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(DarkSurface),
                containerColor = DarkSurface,
                tonalElevation = 6.dp
            ) {
                navTabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonRedBright,
                            selectedTextColor = NeonRedBright,
                            indicatorColor = NeonRedMuted,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> ControlCenterScreen(
                    viewModel = viewModel,
                    onNavigateToSmartCheck = { selectedTabIndex = 5 }
                )
                1 -> PerformanceScreen(viewModel = viewModel)
                2 -> FpsGraphicsScreen(viewModel = viewModel)
                3 -> TouchSmoothnessScreen(viewModel = viewModel)
                4 -> NotificationControlScreen(viewModel = viewModel)
                5 -> SmartOptimizeScreen(viewModel = viewModel)
            }
        }
    }
}
