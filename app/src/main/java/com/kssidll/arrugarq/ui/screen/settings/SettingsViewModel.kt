package com.kssidll.arrugarq.ui.screen.settings


import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.os.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
): ViewModel() {
    val screenState: SettingsScreenState = SettingsScreenState()

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
}
