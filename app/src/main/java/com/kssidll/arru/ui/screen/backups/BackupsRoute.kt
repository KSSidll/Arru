package com.kssidll.arru.ui.screen.backups

import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun BackupsRoute(
    navigateBack: () -> Unit,
) {
    val viewModel: BackupsViewModel = hiltViewModel()

    BackupsScreen(
        createBackup = {
            viewModel.createDbBackup()
        },
        loadBackup = {
            viewModel.loadDbBackup(it)
        },
        deleteBackup = {
            viewModel.deleteDbBackup(it)
        },
        availableBackups = viewModel.availableBackups.toList(),
        onBack = navigateBack,
    )
}