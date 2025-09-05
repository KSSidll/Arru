package com.kssidll.arru.ui.screen.backups

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.CreateBackupUseCase
import com.kssidll.arru.domain.usecase.data.DeleteBackupUseCase
import com.kssidll.arru.domain.usecase.data.GetBackupsUseCase
import com.kssidll.arru.domain.usecase.data.LoadBackupUseCase
import com.kssidll.arru.domain.usecase.data.ToggleBackupLockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class BackupsUiState(val backups: ImmutableList<DatabaseBackup> = emptyImmutableList())

@Immutable
sealed class BackupsEvent {
    data object NavigateBack : BackupsEvent()

    data object CreateBackup : BackupsEvent()

    data class DeleteBackup(val backup: DatabaseBackup) : BackupsEvent()

    data class ToggleBackupLock(val backup: DatabaseBackup) : BackupsEvent()

    data class LoadBackup(val backup: DatabaseBackup) : BackupsEvent()
}

@HiltViewModel
class BackupsViewModel
@Inject
constructor(
    private val getBackupsUseCase: GetBackupsUseCase,
    private val createBackupUseCase: CreateBackupUseCase,
    private val deleteBackupUseCase: DeleteBackupUseCase,
    private val toggleBackupLockUseCase: ToggleBackupLockUseCase,
    private val loadBackupUseCase: LoadBackupUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupsUiState())
    val uiState: StateFlow<BackupsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(backups = getBackupsUseCase()) }
        }
    }

    fun handleEvent(event: BackupsEvent) =
        viewModelScope.launch {
            when (event) {
                is BackupsEvent.NavigateBack -> {}
                is BackupsEvent.CreateBackup -> createBackupUseCase(true)
                is BackupsEvent.DeleteBackup -> deleteBackupUseCase(event.backup)
                is BackupsEvent.ToggleBackupLock -> toggleBackupLockUseCase(event.backup)
                is BackupsEvent.LoadBackup -> loadBackupUseCase(event.backup)
            }

            _uiState.update { currentState -> currentState.copy(backups = getBackupsUseCase()) }
        }
}
