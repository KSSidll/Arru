package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.ui.screen.home.ExpandedHomeRouteNavigation
import com.kssidll.arru.ui.screen.home.HomeRouteNavigation
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

@Composable
fun HomeScreenNothingToDisplayOverlay() {
    Column {
        Box(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = com.kssidll.arru.R.string.no_data_to_display_text),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )

            Text(
                text = stringResource(id = com.kssidll.arru.R.string.no_data_to_display_add_transaction_hint),
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
                    quadraticTo(
                        size.width * 1 / 4,
                        size.height / 2,
                        curveEndX,
                        curveEndY
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
                    quadraticTo(
                        size.width * 3 / 4,
                        size.height / 2,
                        curveEndX,
                        curveEndY
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = com.kssidll.arru.R.string.no_data_to_display_add_transaction_hint),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )

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
private fun HomeScreenNothingToDisplayOverlayPreview() {
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
private fun ExpandedHomeScreenNothingToDisplayOverlayPreview() {
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
