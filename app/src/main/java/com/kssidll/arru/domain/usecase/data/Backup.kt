package com.kssidll.arru.domain.usecase.data

import android.content.Context
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.BackupRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetBackupsUseCase(private val context: Context) {
    suspend operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        withContext(dispatcher) { AppDatabase.availableBackups(context) }
}

class CreateBackupUseCase(
    private val context: Context,
    private val backupRepository: BackupRepositorySource,
) {
    suspend operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        withContext(dispatcher) {
            AppDatabase.saveDbBackup(
                context = context,
                totalTransactions = backupRepository.transactionCount(),
                totalSpending = backupRepository.transactionTotalSpent(),
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
