package com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.spendingcomparison.*
import dev.olshevski.navigation.reimagined.hilt.*
import java.text.*
import java.util.*

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
            .collectAsState(initial = Data.Loading()).value,
        leftSideHeader = stringResource(id = R.string.previous),
        rightSideItems = viewModel.categoryTotalSpentCurrentMonth(
            year,
            month
        )
            .collectAsState(initial = Data.Loading()).value,
        rightSideHeader = stringResource(id = R.string.current),
    )
}