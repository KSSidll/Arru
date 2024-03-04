package com.kssidll.arru.ui.screen.settings


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
    navigateBackups: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()

    SettingsScreen(
        setLocale = {
            viewModel.setLocale(it)
        },
        onBack = navigateBack,
        onBackupsClick = navigateBackups,
    )
}
