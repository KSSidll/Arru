package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ui.screen.home.HomeRouteLocations
import com.kssidll.arru.ui.screen.home.getTranslation
import com.kssidll.arru.ui.theme.ArrugarqTheme

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
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
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
private fun HomeBottomNavBarPreview() {
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
