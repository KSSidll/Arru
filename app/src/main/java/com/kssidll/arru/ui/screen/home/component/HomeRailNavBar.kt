package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.home.*
import com.kssidll.arru.ui.theme.*

/**
 * Rail navigation bar for home screen
 * @param currentLocation Current home location
 * @param onLocationChange Callback called as request to change home location. Provides new location as parameter
 * @param onActionButtonClick Callback called when the FAB is clicked
 */
@Composable
internal fun HomeRailNavBar(
    currentLocation: HomeRouteLocations = HomeRouteLocations.entries.first { it.initial },
    onLocationChange: (HomeRouteLocations) -> Unit,
    onActionButtonClick: () -> Unit,
    onSettingsAction: () -> Unit,
) {
    NavigationRail(
        header = {
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
    ) {
        HomeRouteLocations.entries.forEach {
            NavigationRailItem(
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
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
            )
        }

        NavigationRailItem(
            selected = false,
            onClick = {
                onSettingsAction()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.navigate_to_settings_description),
                )
            },
            label = {
                Text(stringResource(id = R.string.settings))
            }
        )
    }
}

@PreviewLightDark
@Composable
fun HomeRailNavBarPreview() {
    ArrugarqTheme {
        Surface {
            HomeRailNavBar(
                currentLocation = HomeRouteLocations.Dashboard,
                onLocationChange = {},
                onActionButtonClick = {},
                onSettingsAction = {},
            )
        }
    }
}
