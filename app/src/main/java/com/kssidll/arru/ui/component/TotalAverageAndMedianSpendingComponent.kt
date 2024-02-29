package com.kssidll.arru.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.domain.utils.*
import com.kssidll.arru.helper.*
import com.kssidll.arru.ui.theme.*

@Composable
fun TotalAverageAndMedianSpendingComponent(
    spentByTimeData: List<ChartSource>,
    totalSpentData: Float,
    animationSpec: AnimationSpec<Float> = tween(800, easing = EaseIn),
    skipAnimation: Boolean = false,
    onAnimationEnd: () -> Unit = {},
) {
    val totalStartValue = if (skipAnimation) totalSpentData else 0f
    val averageStartValue = if (skipAnimation) spentByTimeData.avg() else 0f
    val medianStartValue = if (skipAnimation) spentByTimeData.median() else 0f

    Column {
        Box(Modifier.fillMaxWidth()) {
            var targetValue by remember { mutableFloatStateOf(totalStartValue) }

            LaunchedEffect(totalSpentData) {
                targetValue = totalSpentData
            }

            val animatedValue = animateFloatAsState(
                targetValue = targetValue,
                animationSpec = animationSpec,
                label = "total spent value animation",
                finishedListener = {
                    // only called here since all animations use same spec and the total is the highest value
                    // so will take the longest no matter the spec
                    onAnimationEnd()
                }
            )

            Text(
                text = animatedValue.value.formatToCurrency(),
                modifier = Modifier.align(Alignment.Center),
                style = Typography.headlineLarge,
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            var averageTargetValue by remember { mutableFloatStateOf(averageStartValue) }
            var medianTargetValue by remember { mutableFloatStateOf(medianStartValue) }

            LaunchedEffect(spentByTimeData) {
                averageTargetValue = spentByTimeData.avg()
                medianTargetValue = spentByTimeData.median()
            }

            val animatedAverageValue = animateFloatAsState(
                targetValue = averageTargetValue,
                animationSpec = animationSpec,
                label = "average spent value animation"
            )

            val animatedMedianValue = animateFloatAsState(
                targetValue = medianTargetValue,
                animationSpec = animationSpec,
                label = "median spent value animation"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.average),
                    style = Typography.headlineSmall,
                )

                Text(
                    text = animatedAverageValue.value.formatToCurrency(),
                    style = Typography.headlineSmall,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.median),
                    style = Typography.headlineSmall,
                )

                Text(
                    text = animatedMedianValue.value.formatToCurrency(),
                    style = Typography.headlineSmall,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun TotalAverageAndMedianSpendingComponentPreview() {
    ArrugarqTheme {
        Surface {
            TotalAverageAndMedianSpendingComponent(
                spentByTimeData = ItemSpentByTime.generateList(),
                totalSpentData = generateRandomFloatValue(),
            )
        }
    }
}
