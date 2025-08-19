package com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.spendingcomparison.SpendingComparisonScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ShopSpendingComparisonRoute(
    navigateBack: () -> Unit,
    year: Int,
    month: Int,
    viewModel: ShopSpendingComparisonViewModel = hiltViewModel(),
) {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.MONTH, month - 1) // calendar has 0 - 11 month indexes

    val formatter = SimpleDateFormat("LLLL", Locale.getDefault())

    SpendingComparisonScreen(
        onBack = navigateBack,
        title =
            "${
            formatter.format(calendar.time)
                .replaceFirstChar { it.titlecase() }
        } $year",
        leftSideItems =
            viewModel
                .shopTotalSpentPreviousMonth(year, month)
                .collectAsState(initial = emptyImmutableList())
                .value,
        leftSideHeader = stringResource(id = R.string.previous),
        rightSideItems =
            viewModel
                .shopTotalSpentCurrentMonth(year, month)
                .collectAsState(initial = emptyImmutableList())
                .value,
        rightSideHeader = stringResource(id = R.string.current),
    )
}
