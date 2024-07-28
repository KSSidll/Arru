package com.kssidll.arru.ui.screen.settings


import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.kssidll.arru.domain.AppLocale
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
