package com.kssidll.arru.ui.screen.settings


import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.os.*
import androidx.lifecycle.*
import com.kssidll.arru.domain.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {

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
