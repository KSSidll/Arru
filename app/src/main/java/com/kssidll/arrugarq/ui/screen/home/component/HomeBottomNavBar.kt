package com.kssidll.arrugarq.ui.screen.home.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun HomeBottomNavBar(
    modifier: Modifier = Modifier,
    currentLocation: HomeScreenLocations = HomeScreenLocations.entries.first { it.initial },
    onLocationChange: ((HomeScreenLocations) -> Unit)? = null,
    onAddItem: (() -> Unit)? = null,
    addItemButtonCanvasSize: Dp = 90.dp,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    Surface(
        modifier = modifier.height(75.dp),
        color = color,
    ) {
        Row {
            Row(
                modifier = Modifier.weight(
                    1F,
                    true
                )
            ) {

            }

            Box(
                modifier = Modifier
                    .width(addItemButtonCanvasSize)
                    .fillMaxHeight()
                    .drawBehind {
                        drawCircle(
                            color = color.copy(0.7F),
                            blendMode = BlendMode.SrcIn,
                            radius = size.height,
                            center = Offset(
                                size.height,
                                size.height.div(5)
                            ),
                        )
                    }
            ) {
                FilledIconButton(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(4.dp)
                        .offset(
                            x = 4.dp,
                            y = (-1).dp
                        )
                        .aspectRatio(1F),
                    onClick = {
                        onAddItem?.invoke()
                    },
                    colors = IconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new item",
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
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
