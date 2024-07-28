package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun AnalysisRoute(
    isExpandedScreen: Boolean,
) {
    val viewModel: AnalysisViewModel = hiltViewModel()

    AnalysisScreen(
        isExpandedScreen = isExpandedScreen,
        year = viewModel.year,
        month = viewModel.month,
        onMonthDecrement = {
            viewModel.monthDecrement()
        },
        onMonthIncrement = {
            viewModel.monthIncrement()
        },
    )
}
