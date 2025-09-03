package com.kssidll.arru.ui.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.ui.theme.Typography

@Composable
fun HomeScreenNothingToDisplayOverlay(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.no_data_to_display_text),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )

            Text(
                text = stringResource(id = R.string.no_data_to_display_add_transaction_hint),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
        }

        val pathColor = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val curveEndX = size.width - 88.dp.toPx()
            val curveEndY = size.height - 12.dp.toPx()
            val lineWidth = 3.dp.toPx()

            drawPath(
                path =
                    Path().apply {
                        moveTo(size.width / 2, 24.dp.toPx())
                        quadraticTo(size.width * 1 / 4, size.height / 2, curveEndX, curveEndY)

                        relativeLineTo(-32.dp.toPx(), 6.dp.toPx())
                        relativeMoveTo(33.dp.toPx(), -5.dp.toPx())
                        relativeLineTo(-6.dp.toPx(), -35.dp.toPx())
                    },
                style = Stroke(width = lineWidth),
                color = pathColor,
            )
        }
    }
}

@Composable
fun ExpandedHomeScreenNothingToDisplayOverlay(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val pathColor = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val curveEndX = 24.dp.toPx()
            val curveEndY = 60.dp.toPx()
            val lineWidth = 3.dp.toPx()

            drawPath(
                path =
                    Path().apply {
                        moveTo(size.width / 2, size.height - 24.dp.toPx())
                        quadraticTo(size.width * 3 / 4, size.height / 2, curveEndX, curveEndY)

                        relativeLineTo(32.dp.toPx(), -24.dp.toPx())
                        relativeMoveTo(-34.dp.toPx(), 24.dp.toPx())
                        relativeLineTo(20.dp.toPx(), 38.dp.toPx())
                    },
                style = Stroke(width = lineWidth),
                color = pathColor,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.no_data_to_display_add_transaction_hint),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )

            Text(
                text = stringResource(id = R.string.no_data_to_display_text),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
        }

        Box(modifier = Modifier.weight(1f))
    }
}
