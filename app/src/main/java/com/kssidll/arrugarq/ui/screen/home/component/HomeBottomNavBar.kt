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

@Composable
fun HomeBottomNavBar(
    currentLocation: HomeScreenLocations = HomeScreenLocations.entries.first { it.initial },
    onLocationChange: ((HomeScreenLocations) -> Unit)? = null,
    onAddItem: (() -> Unit)? = null,
) {
    BottomAppBar(
        actions = {

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddItem?.invoke()
                },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new item",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
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
                currentLocation = HomeScreenLocations.Dashboard,
            )
        }
    }
}
