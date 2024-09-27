package com.kssidll.arru.ui.screen.backups

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionBasketRepository: TransactionBasketRepositorySource,
): ViewModel() {
    val availableBackups: SnapshotStateList<DatabaseBackup> = mutableStateListOf()

    init {
        viewModelScope.launch {
            refreshAvailableBackups()
        }
    }

    /**
     * Refreshes the available backups list to represent currently available backups
     */
    private suspend fun refreshAvailableBackups() {
        val newAvailableBackups = AppDatabase.availableBackups(context)

        availableBackups.clear()
        availableBackups.addAll(newAvailableBackups)
    }

    private fun lockDbBackup(dbBackup: DatabaseBackup) = viewModelScope.launch {
        AppDatabase.lockDbBackup(dbBackup)

        refreshAvailableBackups()
    }

    private fun unlockDbBackup(dbBackup: DatabaseBackup) = viewModelScope.launch {
        AppDatabase.unlockDbBackup(dbBackup)

        refreshAvailableBackups()
    }

    fun toggleLockDbBackup(dbBackup: DatabaseBackup) = viewModelScope.launch {
        if (dbBackup.locked) {
            unlockDbBackup(dbBackup)
        } else {
            lockDbBackup(dbBackup)
        }
    }

    /**
     * Creates a backup of current database
     */
    fun createDbBackup() = viewModelScope.launch {
        Dispatchers.IO.invoke {
            // TODO add notification when you create maybe?
            val totalTransactions = transactionBasketRepository.count()
            val totalSpending = transactionBasketRepository.totalSpentLong()

            if (totalSpending is Data.Loaded) {
                AppDatabase.saveDbBackup(
                    context = context,
                    totalTransactions = totalTransactions,
                    totalSpending = totalSpending.data ?: 0,
                )
            }
            refreshAvailableBackups()
        }
    }

    /**
     * Loads a backup of the database
     * @param dbFile Database file to load
     */
    fun loadDbBackup(dbFile: DatabaseBackup) = viewModelScope.launch {
        AppDatabase.loadDbBackup(
            context,
            dbFile,
        )
    }

    /**
     * Removes a backup of the database
     * @param dbFile Database file to remove
     */
    fun deleteDbBackup(dbFile: DatabaseBackup) = viewModelScope.launch {
        AppDatabase.deleteDbBackup(dbFile)
        refreshAvailableBackups()
    }
}
