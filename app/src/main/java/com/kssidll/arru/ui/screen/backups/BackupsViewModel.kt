package com.kssidll.arru.ui.screen.backups

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.TransactionRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferences: Preferences,
    private val transactionRepository: TransactionRepositorySource,
): ViewModel() {
    val availableBackups: SnapshotStateList<DatabaseBackup> = mutableStateListOf()

    init {
        refreshAvailableBackups()
    }

    /**
     * Refreshes the available backups list to represent currently available backups
     */
    private fun refreshAvailableBackups() {
        val newAvailableBackups = AppDatabase.availableBackups(
            appContext,
            preferences
        )

        availableBackups.clear()
        availableBackups.addAll(newAvailableBackups)
    }

    /**
     * Creates a backup of current database
     */
    fun createDbBackup() = viewModelScope.launch {
        Dispatchers.IO.invoke {
            // TODO add notification when you create maybe?
            val totalTransactions = transactionRepository.count()
            val totalSpending = transactionRepository.totalRawSpent()

            AppDatabase.saveDbBackup(
                context = appContext,
                preferences = preferences,
                totalTransactions = totalTransactions,
                totalSpending = totalSpending,
            )

            refreshAvailableBackups()
        }
    }

    /**
     * Loads a backup of the database
     * @param dbFile Database file to load
     */
    fun loadDbBackup(dbFile: DatabaseBackup) {
        AppDatabase.loadDbBackup(
            appContext,
            preferences,
            dbFile,
        )
    }

    /**
     * Removes a backup of the database
     * @param dbFile Database file to remove
     */
    fun deleteDbBackup(dbFile: DatabaseBackup) {
        AppDatabase.deleteDbBackup(dbFile)
        refreshAvailableBackups()
    }
}
