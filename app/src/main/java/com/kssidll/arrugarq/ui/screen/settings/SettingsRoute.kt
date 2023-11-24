package com.kssidll.arrugarq.ui.screen.settings


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()

    SettingsScreen(
        state = viewModel.screenState,
        setLocale = {
            viewModel.setLocale(it)
        },
        onBack = navigateBack,
    )
}
