package com.kssidll.arrugarq.ui.component

import android.content.res.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun TotalAverageAndMedianSpendingComponent(
    spentByTimeData: List<ItemSpentByTime>,
    totalSpentData: Float,
    animationSpec: AnimationSpec<Float> = tween(1200),
) {
    Column {
        Box(Modifier.fillMaxWidth()) {
            var targetValue by remember { mutableFloatStateOf(totalSpentData) }

            LaunchedEffect(totalSpentData) {
                targetValue = totalSpentData
            }

            val animatedValue = animateFloatAsState(
                targetValue = targetValue,
                animationSpec = tween(1200),
                label = "total spent value animation"
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
            var averageTargetValue by remember { mutableFloatStateOf(spentByTimeData.avg()) }
            var medianTargetValue by remember { mutableFloatStateOf(spentByTimeData.median()) }

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
                    text = "Average",
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
                    text = "Median",
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

@Preview(
    group = "Total Average And Median Spending Component",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Total Average And Median Spending Component",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TotalAverageAndMedianSpendingComponentPreview() {
    ArrugarqTheme {
        Surface {
            TotalAverageAndMedianSpendingComponent(
                spentByTimeData = generateRandomItemSpentByTimeList(),
                totalSpentData = generateRandomFloatValue(),
            )
        }
    }
}
