package com.kssidll.arru.ui.screen.backups

import android.content.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.datastore.preferences.core.*
import androidx.lifecycle.*
import com.kssidll.arru.data.database.*
import dagger.hilt.android.lifecycle.*
import dagger.hilt.android.qualifiers.*
import java.io.*
import javax.inject.*

@HiltViewModel
class BackupsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferences: Preferences
): ViewModel() {
    val availableBackups: SnapshotStateList<File> = mutableStateListOf()

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
    fun createDbBackup() {
        // TODO add notification when you create maybe?
        AppDatabase.saveDbBackup(
            appContext,
            preferences,
        )
        refreshAvailableBackups()
    }

    /**
     * Loads a backup of the database
     * @param dbFile Database file to load
     */
    fun loadDbBackup(dbFile: File) {
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
    fun deleteDbBackup(dbFile: File) {
        AppDatabase.deleteDbBackup(dbFile)
        refreshAvailableBackups()
    }
}
