package com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.spendingcomparison.SpendingComparisonScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CategorySpendingComparisonRoute(
    navigateBack: () -> Unit,
    year: Int,
    month: Int,
) {
    val viewModel: CategorySpendingComparisonViewModel = hiltViewModel()

    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(
        Calendar.MONTH,
        month - 1
    ) // calendar has 0 - 11 month indexes

    val formatter = SimpleDateFormat(
        "LLLL",
        Locale.getDefault()
    )

    SpendingComparisonScreen(
        onBack = navigateBack,
        title = "${
            formatter.format(calendar.time)
                .replaceFirstChar { it.titlecase() }
        } $year",
        leftSideItems = viewModel.categoryTotalSpentPreviousMonth(
            year,
            month
        )
            .collectAsState(initial = emptyList()).value,
        leftSideHeader = stringResource(id = R.string.previous),
        rightSideItems = viewModel.categoryTotalSpentCurrentMonth(
            year,
            month
        )
            .collectAsState(initial = emptyList()).value,
        rightSideHeader = stringResource(id = R.string.current),
    )
}