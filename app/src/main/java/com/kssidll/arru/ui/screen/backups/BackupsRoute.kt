package com.kssidll.arru.ui.screen.backups

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun BackupsRoute(navigateBack: () -> Unit, viewModel: BackupsViewModel = hiltViewModel()) {
    BackupsScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is BackupsEvent.NavigateBack -> navigateBack()
                is BackupsEvent.CreateBackup -> viewModel.handleEvent(event)
                is BackupsEvent.DeleteBackup -> viewModel.handleEvent(event)
                is BackupsEvent.LoadBackup -> viewModel.handleEvent(event)
                is BackupsEvent.ToggleBackupLock -> viewModel.handleEvent(event)
            }
        },
    )
}
