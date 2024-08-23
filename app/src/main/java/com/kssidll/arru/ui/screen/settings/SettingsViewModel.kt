package com.kssidll.arru.ui.screen.settings


import android.content.Context
import android.net.Uri
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getExportType
import com.kssidll.arru.data.preference.setExportType
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.service.DataExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    preferences: Preferences
): ViewModel() {
    val currentExportType: MutableState<AppPreferences.Export.Type.Values> =
        mutableStateOf(preferences.getExportType())

    fun setExportType(newType: AppPreferences.Export.Type.Values) = viewModelScope.launch {
        AppPreferences.setExportType(
            appContext,
            newType
        )
        currentExportType.value = newType
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

    fun exportWithService(uri: Uri) = viewModelScope.launch {
        when (currentExportType.value) {
            AppPreferences.Export.Type.Values.CompactCSV -> {

            }

            AppPreferences.Export.Type.Values.RawCSV -> {
                DataExportService.startExportCsvRaw(
                    appContext,
                    uri
                )
            }
        }
    }
}
