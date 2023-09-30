package com.kssidll.arrugarq.ui.screen.home

import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.chart.*
import com.kssidll.arrugarq.ui.screen.home.component.*
import com.kssidll.arrugarq.ui.screen.home.dashboard.*
import com.kssidll.arrugarq.ui.theme.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import kotlinx.coroutines.*

// Important, the order of items in the enum determines the order that the locations appear in
// on the bottom navigation bar
enum class HomeScreenLocations(
    val initial: Boolean = false,
) {
    Dashboard(initial = true),
    ;

    val description: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Dashboard -> stringResource(R.string.navigate_to_dashboard_description)
        }

    val imageVector: ImageVector?
        @Composable
        get() = when (this) {
            Dashboard -> Icons.Rounded.Home
        }

    val painter: Painter?
        @Composable
        get() = when (this) {
            Dashboard -> null
        }

    companion object {
        private val idMap = entries.associateBy { it.ordinal }
        fun getByOrdinal(ordinal: Int) = idMap[ordinal]

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
    spentByTimeData: List<IChartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = HomeScreenLocations.entries.first { it.initial }.ordinal,
        initialPageOffsetFraction = 0F,
        pageCount = { HomeScreenLocations.entries.size },
    )

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentLocation = HomeScreenLocations.getByOrdinal(pagerState.currentPage)!!,
                onLocationChange = {
                    scope.launch {
                        pagerState.animateScrollToPage(it.ordinal)
                    }
                },
                onAddItem = onAddItem,
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            HomeScreenContent(
                pagerState = pagerState,
                spentByTimeData = spentByTimeData,
                spentByTimePeriod = spentByTimePeriod,
                onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    pagerState: PagerState,
    spentByTimeData: List<IChartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
    ) {
        when (HomeScreenLocations.getByOrdinal(it)!!) {
            HomeScreenLocations.Dashboard -> {
                DashboardScreen(
                    spentByTimeData = spentByTimeData,
                    spentByTimePeriod = spentByTimePeriod,
                    onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                )
            }
        }
    }

}


@Preview(
    group = "Home Screen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "Home Screen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun HomeScreenPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    onAddItem = {},
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