package com.kssidll.arru.ui.screen.backups

import android.content.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.datastore.preferences.core.*
import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.database.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
import dagger.hilt.android.lifecycle.*
import dagger.hilt.android.qualifiers.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class BackupsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferences: Preferences,
    private val transactionBasketRepository: TransactionBasketRepositorySource,
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
            val totalTransactions = transactionBasketRepository.count()
            val totalSpending = transactionBasketRepository.totalSpentLong()

            if (totalSpending is Data.Loaded) {
                AppDatabase.saveDbBackup(
                    context = appContext,
                    preferences = preferences,
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
