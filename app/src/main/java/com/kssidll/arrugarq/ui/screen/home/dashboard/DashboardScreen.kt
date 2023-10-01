package com.kssidll.arrugarq.ui.screen.home.dashboard

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.domain.utils.*
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
    spentByTimeData: Flow<List<Chartable>>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    DashboardScreenContent(
        totalSpentData = totalSpentData.collectAsState(0F).value,
        spentByShopData = spentByShopData.collectAsState(emptyList()).value,
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
    spentByTimeData: List<Chartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.scrollable(
            state = scrollState,
            orientation = Orientation.Vertical,
        )
    ) {
        Spacer(Modifier.height(40.dp))

        Box(Modifier.fillMaxWidth()) {
            val dropDecimal = totalSpentData >= 100

            Text(
                text = totalSpentData.formatToCurrency(dropDecimal = dropDecimal),
                modifier = Modifier.align(Alignment.Center),
                style = Typography.headlineLarge,
            )
        }

        Spacer(Modifier.height(32.dp))

        Surface(
            modifier = Modifier.padding(tileOuterPadding),
            shape = ShapeDefaults.ExtraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column {
                OneDimensionalSpendingChart(
                    modifier = Modifier.padding(tileInnerPadding),
                    spentByTimeData = spentByTimeData,
                    spentByTimePeriod = spentByTimePeriod,
                    onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                    autoScrollSpec = defaultOneDimensionalSpendingChartAutoScrollSpec,
                )
            }
        }

        Surface(
            modifier = Modifier.padding(tileOuterPadding),
            shape = ShapeDefaults.ExtraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
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
                    spentByShopData = listOf(
                        ItemSpentByShop(
                            shop = Shop("test1"),
                            total = 168200
                        ),
                        ItemSpentByShop(
                            shop = Shop("test2"),
                            total = 10000
                        ),
                        ItemSpentByShop(
                            shop = Shop("test3"),
                            total = 100000
                        ),
                        ItemSpentByShop(
                            shop = Shop("test4"),
                            total = 61000
                        ),
                        ItemSpentByShop(
                            shop = Shop("test5"),
                            total = 27600
                        ),
                    ),
                    spentByTimeData = listOf(
                        ItemSpentByTime(
                            time = "2022-08",
                            total = 34821
                        ),
                        ItemSpentByTime(
                            time = "2022-09",
                            total = 25000
                        ),
                        ItemSpentByTime(
                            time = "2022-10",
                            total = 50000
                        ),
                        ItemSpentByTime(
                            time = "2022-11",
                            total = 12345
                        ),
                    ),
                    spentByTimePeriod = SpentByTimePeriod.Month,
                    onSpentByTimePeriodSwitch = {},
                )
            }
        }
    }
}
