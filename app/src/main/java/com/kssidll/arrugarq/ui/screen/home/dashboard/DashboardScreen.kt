package com.kssidll.arrugarq.ui.screen.home.dashboard

import android.content.res.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.screen.home.component.*
import com.kssidll.arrugarq.ui.theme.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import kotlinx.coroutines.flow.*

@Composable
fun DashboardScreen(
    totalSpentData: Flow<Float>,
    spentByShopData: Flow<List<ItemSpentByShop>>,
    spentByCategoryData: Flow<List<ItemSpentByCategory>>,
    spentByTimeData: Flow<List<Chartable>>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    DashboardScreenContent(
        totalSpentData = totalSpentData.collectAsState(0F).value,
        spentByShopData = spentByShopData.collectAsState(emptyList()).value,
        spentByCategoryData = spentByCategoryData.collectAsState(emptyList()).value,
        spentByTimeData = spentByTimeData.collectAsState(emptyList()).value,
        spentByTimePeriod = spentByTimePeriod,
        onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
    )
}

private val tileOuterPadding: Dp = 8.dp
private val tileInnerPadding: Dp = 12.dp

@Composable
private fun DashboardScreenContent(
    totalSpentData: Float,
    spentByShopData: List<ItemSpentByShop>,
    spentByCategoryData: List<ItemSpentByCategory>,
    spentByTimeData: List<Chartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(state = scrollState)
    ) {
        Spacer(Modifier.height(40.dp))

        Box(Modifier.fillMaxWidth()) {
            var targetValue by remember { mutableFloatStateOf(totalSpentData) }

            LaunchedEffect(totalSpentData) {
                targetValue = totalSpentData
            }

            val animatedValue = animateFloatAsState(
                targetValue = targetValue,
                animationSpec = defaultOneDimensionalSpendingChartAutoScrollSpec,
                label = "total spent value animation"
            )
            val dropDecimal = animatedValue.value >= 100

            Text(
                text = animatedValue.value.formatToCurrency(dropDecimal = dropDecimal),
                modifier = Modifier.align(Alignment.Center),
                style = Typography.headlineLarge,
            )
        }

        Spacer(Modifier.height(32.dp))

        OneDimensionalSpendingChart(
            spentByTimeData = spentByTimeData,
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
            autoScrollSpec = defaultOneDimensionalSpendingChartAutoScrollSpec,
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .padding(tileOuterPadding)
                .clickable {

                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            RankingList(
                modifier = Modifier.padding(tileInnerPadding),
                items = spentByCategoryData,
                animationSpec = defaultOneDimensionalSpendingChartAutoScrollSpec,
            )
        }

        Card(
            modifier = Modifier.padding(tileOuterPadding),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            RankingList(
                modifier = Modifier.padding(tileInnerPadding),
                items = spentByShopData,
                animationSpec = defaultOneDimensionalSpendingChartAutoScrollSpec,
            )
        }

    }
}

@Preview(
    group = "Dashboard Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Dashboard Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DashboardScreenPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                DashboardScreenContent(
                    totalSpentData = 16832.18F,
                    spentByShopData = getFakeSpentByShopData(),
                    spentByCategoryData = getFakeSpentByCategoryData(),
                    spentByTimeData = getFakeSpentByTimeData(),
                    spentByTimePeriod = SpentByTimePeriod.Month,
                    onSpentByTimePeriodSwitch = {},
                )
            }
        }
    }
}
