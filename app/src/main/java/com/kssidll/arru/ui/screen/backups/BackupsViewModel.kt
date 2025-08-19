package com.kssidll.arru.ui.screen.backups

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.TransactionRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class BackupsViewModel
@Inject
constructor(
    @param:ApplicationContext private val context: Context,
    private val transactionBasketRepository: TransactionRepositorySource,
) : ViewModel() {
    val availableBackups: SnapshotStateList<DatabaseBackup> = mutableStateListOf()

    init {
        viewModelScope.launch { refreshAvailableBackups() }
    }

    /** Refreshes the available backups list to represent currently available backups */
    private suspend fun refreshAvailableBackups() {
        val newAvailableBackups = AppDatabase.availableBackups(context)

        availableBackups.clear()
        availableBackups.addAll(newAvailableBackups)
    }

    private fun lockDbBackup(dbBackup: DatabaseBackup) =
        viewModelScope.launch {
            AppDatabase.lockDbBackup(dbBackup)

            refreshAvailableBackups()
        }

    private fun unlockDbBackup(dbBackup: DatabaseBackup) =
        viewModelScope.launch {
            AppDatabase.unlockDbBackup(dbBackup)

            refreshAvailableBackups()
        }

    fun toggleLockDbBackup(dbBackup: DatabaseBackup) =
        viewModelScope.launch {
            if (dbBackup.locked) {
                unlockDbBackup(dbBackup)
            } else {
                lockDbBackup(dbBackup)
            }
        }

    /** Creates a backup of current database */
    fun createDbBackup() =
        viewModelScope.launch {
            Dispatchers.IO.invoke {
                // TODO add notification when you create maybe?
                val totalTransactions = transactionBasketRepository.count()
                val totalSpending = transactionBasketRepository.totalSpentLong().first()

                if (totalSpending != null) {
                    AppDatabase.saveDbBackup(
                        context = context,
                        totalTransactions = totalTransactions,
                        totalSpending = totalSpending,
                    )
                }
                refreshAvailableBackups()
            }
        }

    /**
     * Loads a backup of the database
     *
     * @param dbFile Database file to load
     */
    fun loadDbBackup(dbFile: DatabaseBackup) =
        viewModelScope.launch { AppDatabase.loadDbBackup(context, dbFile) }

    /**
     * Removes a backup of the database
     *
     * @param dbFile Database file to remove
     */
    fun deleteDbBackup(dbFile: DatabaseBackup) =
        viewModelScope.launch {
            AppDatabase.deleteDbBackup(dbFile)
            refreshAvailableBackups()
        }
}
