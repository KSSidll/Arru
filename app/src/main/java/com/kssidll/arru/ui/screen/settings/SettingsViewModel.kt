package com.kssidll.arru.ui.screen.settings


import android.content.Context
import android.net.Uri
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getExportType
import com.kssidll.arru.data.preference.setExportType
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.domain.usecase.ExportDataUIBlockingUseCase
import com.kssidll.arru.domain.usecase.ExportDataWithServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val exportDataWithServiceUseCase: ExportDataWithServiceUseCase,
    private val exportDataUIBlockingUseCase: ExportDataUIBlockingUseCase,
): ViewModel() {
    val currentExportType: Flow<AppPreferences.Export.Type.Values> =
        AppPreferences.getExportType(appContext)

    fun setExportType(newType: AppPreferences.Export.Type.Values) = viewModelScope.launch {
        AppPreferences.setExportType(
            appContext,
            newType
        )
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
        exportDataWithServiceUseCase(uri)
    }

    fun exportUIBlocking(
        uri: Uri,
        onMaxProgressChange: (newMaxProgress: Int) -> Unit,
        onProgressChange: (newProgress: Int) -> Unit,
        onFinished: () -> Unit,
    ) = viewModelScope.launch {
        exportDataUIBlockingUseCase(uri, onMaxProgressChange, onProgressChange, onFinished)
    }
}
