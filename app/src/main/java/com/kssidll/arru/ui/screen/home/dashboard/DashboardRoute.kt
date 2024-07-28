package com.kssidll.arru.ui.screen.home.dashboard


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kssidll.arru.domain.data.Data
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
internal fun DashboardRoute(
    isExpandedScreen: Boolean,
    navigateSettings: () -> Unit,
) {
    val viewModel: DashboardViewModel = hiltViewModel()

    DashboardScreen(
        isExpandedScreen = isExpandedScreen,
        onSettingsAction = navigateSettings,
        totalSpentData = viewModel.getTotalSpent()
            .collectAsState(initial = Data.Loading()).value,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodUpdate = {
            viewModel.switchToSpentByTimePeriod(it)
        },
    )
}
