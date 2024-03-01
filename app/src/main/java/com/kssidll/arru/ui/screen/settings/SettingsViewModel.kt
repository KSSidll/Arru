package com.kssidll.arru.ui.screen.settings


import android.content.*
import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.core.os.*
import androidx.datastore.preferences.core.*
import androidx.lifecycle.*
import com.kssidll.arru.data.database.*
import com.kssidll.arru.domain.*
import dagger.hilt.android.lifecycle.*
import dagger.hilt.android.qualifiers.*
import java.io.*
import javax.inject.*

@HiltViewModel
class SettingsViewModel @Inject constructor(
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
     * Sets application locale to [locale]
     * @param locale [AppLocale] to set the application to, if null, sets application to system default
     */
    @MainThread
    fun setLocale(locale: AppLocale?) {
        val localeList = if (locale != null) {
            LocaleListCompat.forLanguageTags(locale.tag)
        } else LocaleListCompat.getEmptyLocaleList()

        setApplicationLocales(localeList)
    }

    fun createDbBackup() {
        // TODO add notification when you create maybe?
        AppDatabase.saveDbBackup(
            appContext,
            preferences
        )
        refreshAvailableBackups()
    }
}
