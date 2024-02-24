package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.ui.screen.home.*
import com.kssidll.arru.ui.theme.*

@Composable
fun HomeScreenNothingToDisplayOverlay() {
    Column {
        Box(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = com.kssidll.arru.R.string.no_data_to_display_text),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
        }

        val pathColor = MaterialTheme.colorScheme.tertiary
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val curveEndX = size.width - 88.dp.toPx()
            val curveEndY = size.height - 12.dp.toPx()
            val lineWidth = 3.dp.toPx()

            drawPath(
                path = Path().apply {
                    moveTo(
                        size.width / 2,
                        24.dp.toPx()
                    )
                    quadraticBezierTo(
                        size.width * 1 / 4,
                        size.height / 2,
                        curveEndX,
                        curveEndY,
                    )

                    relativeLineTo(
                        -32.dp.toPx(),
                        6.dp.toPx()
                    )
                    relativeMoveTo(
                        33.dp.toPx(),
                        -5.dp.toPx()
                    )
                    relativeLineTo(
                        -6.dp.toPx(),
                        -35.dp.toPx()
                    )
                },
                style = Stroke(width = lineWidth),
                color = pathColor
            )
        }
    }
}

@Composable
fun ExpandedHomeScreenNothingToDisplayOverlay() {
    Column {
        val pathColor = MaterialTheme.colorScheme.tertiary
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val curveEndX = 24.dp.toPx()
            val curveEndY = 40.dp.toPx()
            val lineWidth = 3.dp.toPx()

            drawPath(
                path = Path().apply {
                    moveTo(
                        size.width / 2,
                        size.height - 24.dp.toPx()
                    )
                    quadraticBezierTo(
                        size.width * 3 / 4,
                        size.height / 2,
                        curveEndX,
                        curveEndY,
                    )

                    relativeLineTo(
                        32.dp.toPx(),
                        -24.dp.toPx()
                    )
                    relativeMoveTo(
                        -34.dp.toPx(),
                        24.dp.toPx()
                    )
                    relativeLineTo(
                        20.dp.toPx(),
                        38.dp.toPx()
                    )
                },
                style = Stroke(width = lineWidth),
                color = pathColor
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = com.kssidll.arru.R.string.no_data_to_display_text),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
        }

        Box(modifier = Modifier.weight(1f))
    }
}

@PreviewLightDark
@Composable
fun HomeScreenNothingToDisplayOverlayPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeRouteNavigation(
                onLocationChange = {},
                navigateTransactionAdd = {},
            ) {
                HomeScreenNothingToDisplayOverlay()
            }
        }
    }
}

@PreviewExpanded
@Composable
fun ExpandedHomeScreenNothingToDisplayOverlayPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ExpandedHomeRouteNavigation(
                onLocationChange = {},
                navigateTransactionAdd = {},
                navigateSettings = {},
            ) {
                ExpandedHomeScreenNothingToDisplayOverlay()
            }
        }
    }
}
