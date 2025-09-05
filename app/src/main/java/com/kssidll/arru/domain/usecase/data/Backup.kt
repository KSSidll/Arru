package com.kssidll.arru.domain.usecase.data

import android.content.Context
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getBackupOnDangerousActionEnabled
import com.kssidll.arru.data.repository.BackupRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GetBackupsUseCase(private val context: Context) {
    suspend operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        withContext(dispatcher) { AppDatabase.availableBackups(context) }
}

class CreateBackupUseCase(
    private val context: Context,
    private val backupRepository: BackupRepositorySource,
) {
    suspend operator fun invoke(locked: Boolean, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        withContext(dispatcher) {
            AppDatabase.saveDbBackup(
                context = context,
                totalTransactions = backupRepository.transactionCount(),
                totalSpending = backupRepository.transactionTotalSpent(),
                locked = locked,
            )
        }
}

class DeleteBackupUseCase() {
    suspend operator fun invoke(
        backup: DatabaseBackup,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        force: Boolean = false,
    ) =
        withContext(dispatcher) {
            if (!backup.locked || force) {
                AppDatabase.deleteDbBackup(backup)
            }
        }
}

class ToggleBackupLockUseCase() {
    suspend operator fun invoke(
        backup: DatabaseBackup,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) =
        withContext(dispatcher) {
            if (backup.locked) {
                AppDatabase.unlockDbBackup(backup)
            } else {
                AppDatabase.lockDbBackup(backup)
            }
        }
}

class LoadBackupUseCase(private val context: Context) {
    suspend operator fun invoke(
        backup: DatabaseBackup,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) { AppDatabase.loadDbBackup(context, backup) }
}

class PerformAutomaticBackupIfEnabledUseCase(
    private val context: Context,
    private val getBackupsUseCase: GetBackupsUseCase,
    private val createBackupUseCase: CreateBackupUseCase,
    private val deleteBackupUseCase: DeleteBackupUseCase,
) {
    suspend operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        withContext(dispatcher) {
            if (AppPreferences.getBackupOnDangerousActionEnabled(context).first()) {
                createBackupUseCase(false)

                val unlockedBackups = getBackupsUseCase(dispatcher).filterNot { it.locked }

                if (unlockedBackups.size > 20) {
                    val overflowing =
                        unlockedBackups.sortedBy { it.time }.take(unlockedBackups.size - 20)

                    overflowing.forEach { deleteBackupUseCase(it, dispatcher, true) }
                }
            }
        }
}
