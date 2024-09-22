package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.home.HomeRouteLocations
import com.kssidll.arru.ui.screen.home.getTranslation
import com.kssidll.arru.ui.theme.ArrugarqTheme

/**
 * Rail navigation bar for home screen
 * @param currentLocation Current home location
 * @param onLocationChange Callback called as request to change home location. Provides new location as parameter
 * @param onActionButtonClick Callback called when the FAB is clicked
 */
@Composable
internal fun HomeRailNavBar(
    modifier: Modifier = Modifier,
    currentLocation: HomeRouteLocations = HomeRouteLocations.entries.first { it.initial },
    onLocationChange: (HomeRouteLocations) -> Unit,
    onActionButtonClick: () -> Unit,
    onSettingsAction: () -> Unit,
    windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
) {
    NavigationRail(
        modifier = modifier,
        header = {
            FloatingActionButton(
                onClick = {
                    onActionButtonClick()
                },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.transaction_add_label),
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        windowInsets = windowInsets,
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
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
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
private fun HomeRailNavBarPreview() {
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
