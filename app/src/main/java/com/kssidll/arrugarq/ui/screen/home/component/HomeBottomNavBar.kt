package com.kssidll.arrugarq.ui.screen.home.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.theme.*

/**
 * Bottom navigation bar for [HomeScreen] screen
 * @param currentLocation Current [HomeScreen] location
 * @param onLocationChange Callback called as request to change [HomeScreen] location, Provides new location as parameter
 * @param onActionButtonClick Callback called when the FAB is clicked
 */
@Composable
internal fun HomeBottomNavBar(
    currentLocation: HomeRouteLocations = HomeRouteLocations.entries.first { it.initial },
    onLocationChange: ((HomeRouteLocations) -> Unit)? = null,
    onActionButtonClick: (() -> Unit)? = null,
) {
    BottomAppBar(
        actions = {
            HomeRouteLocations.entries.forEach {
                NavigationBarItem(
                    selected = currentLocation == it,
                    onClick = {
                        onLocationChange?.invoke(it)
                    },
                    icon = {
                        Icon(
                            imageVector = it.imageVector,
                            contentDescription = it.description,
                        )
                    },
                    label = {
                        Text(it.getTranslation())
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onActionButtonClick?.invoke()
                },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new item",
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
    )
}

@Preview(
    group = "Home Bottom Nav Bar",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Home Bottom Nav Bar",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun HomeBottomNavBarPreview() {
    ArrugarqTheme {
        Surface {
            HomeBottomNavBar(
                currentLocation = HomeRouteLocations.Dashboard,
            )
        }
    }
}
