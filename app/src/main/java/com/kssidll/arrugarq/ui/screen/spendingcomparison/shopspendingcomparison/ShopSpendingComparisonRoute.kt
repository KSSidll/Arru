package com.kssidll.arrugarq.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.spendingcomparison.*
import dev.olshevski.navigation.reimagined.hilt.*
import java.text.*
import java.util.*

@Composable
fun ShopSpendingComparisonRoute(
    navigateBack: () -> Unit,
    year: Int,
    month: Int,
) {
    val viewModel: ShopSpendingComparisonViewModel = hiltViewModel()

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
        leftSideItems = viewModel.shopTotalSpentPreviousMonth(
            year,
            month
        )
            .collectAsState(initial = emptyList()).value,
        leftSideHeader = stringResource(id = R.string.previous),
        rightSideItems = viewModel.shopTotalSpentCurrentMonth(
            year,
            month
        )
            .collectAsState(initial = emptyList()).value,
        rightSideHeader = stringResource(id = R.string.current),
    )
}
