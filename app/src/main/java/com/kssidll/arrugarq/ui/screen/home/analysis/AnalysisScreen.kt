package com.kssidll.arrugarq.ui.screen.home.analysis


import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.screen.home.analysis.components.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.flow.*

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

/**
 * @param year Year for which the main data is fetched
 * @param month Month for which the main data is fetched, in range of 1 - 12
 * @param onMonthIncrement Callback called to request [month] increment, should handle overflow and increase year
 * @param onMonthDecrement Callback called to request [month] decrement, should handle underflow and decrease year
 * @param setCategorySpending List of items representing the category wise spending for currently set [year] and [month]
 * @param compareCategorySpending List of items representing the category wise spending for previous [month] of currently set [year] and [month]
 * @param onCategorySpendingComparisonCardClick Callback called when the category spending comparison card is clicked
 * @param onShopSpendingComparisonCardClick Callback called when the shop spending comparison card is clicked
 */
@Composable
internal fun AnalysisScreen(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
    setCategorySpending: Flow<List<ItemSpentByCategory>>,
    compareCategorySpending: Flow<List<ItemSpentByCategory>>,
    setShopSpending: Flow<List<ItemSpentByShop>>,
    compareShopSpending: Flow<List<ItemSpentByShop>>,
    onCategorySpendingComparisonCardClick: () -> Unit,
    onShopSpendingComparisonCardClick: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(vertical = 12.dp)) {
                DateHeader(
                    year = year,
                    month = month,
                    onMonthIncrement = onMonthIncrement,
                    onMonthDecrement = onMonthDecrement,
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .padding(TileOuterPadding)
                    .fillMaxWidth()
                    .heightIn(min = 144.dp)
                    .clickable {
                        onCategorySpendingComparisonCardClick()
                    }
            ) {
                SpendingComparisonList(
                    leftSideItems = compareCategorySpending.collectAsState(initial = emptyList()).value,
                    leftSideHeader = stringResource(id = R.string.previous),
                    rightSideItems = setCategorySpending.collectAsState(initial = emptyList()).value,
                    rightSideHeader = stringResource(id = R.string.current),
                    itemDisplayLimit = 6,
                    modifier = Modifier.padding(TileInnerPadding)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .padding(TileOuterPadding)
                    .fillMaxWidth()
                    .heightIn(min = 144.dp)
                    .clickable {
                        onShopSpendingComparisonCardClick()
                    }
            ) {
                SpendingComparisonList(
                    leftSideItems = compareShopSpending.collectAsState(initial = emptyList()).value,
                    leftSideHeader = stringResource(id = R.string.previous),
                    rightSideItems = setShopSpending.collectAsState(initial = emptyList()).value,
                    rightSideHeader = stringResource(id = R.string.current),
                    itemDisplayLimit = 6,
                    modifier = Modifier.padding(TileInnerPadding)
                )
            }
        }
    }
}

@Preview(
    group = "AnalysisScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "AnalysisScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
private fun AnalysisScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
                setCategorySpending = flowOf(),
                compareCategorySpending = flowOf(),
                setShopSpending = flowOf(),
                compareShopSpending = flowOf(),
                onCategorySpendingComparisonCardClick = {},
                onShopSpendingComparisonCardClick = {},
            )
        }
    }
}
