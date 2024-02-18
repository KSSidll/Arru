package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.ui.screen.home.*
import com.kssidll.arru.ui.theme.*

/**
 * Bottom navigation bar for home screen
 * @param currentLocation Current home location
 * @param onLocationChange Callback called as request to change home location. Provides new location as parameter
 * @param onActionButtonClick Callback called when the FAB is clicked
 */
@Composable
internal fun HomeBottomNavBar(
    currentLocation: HomeRouteLocations = HomeRouteLocations.entries.first { it.initial },
    onLocationChange: (HomeRouteLocations) -> Unit,
    onActionButtonClick: () -> Unit,
) {
    BottomAppBar(
        actions = {
            HomeRouteLocations.entries.forEach {
                NavigationBarItem(
                    selected = currentLocation == it,
                    onClick = {
                        onLocationChange(it)
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
                    onActionButtonClick()
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

@PreviewLightDark
@Composable
fun HomeBottomNavBarPreview() {
    ArrugarqTheme {
        Surface {
            HomeBottomNavBar(
                currentLocation = HomeRouteLocations.Dashboard,
                onLocationChange = {},
                onActionButtonClick = {},
            )
        }
    }
}
