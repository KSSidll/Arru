package com.kssidll.arru.ui.screen.settings


import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getColorScheme
import com.kssidll.arru.data.preference.getCurrencyFormatLocale
import com.kssidll.arru.data.preference.getDatabaseLocation
import com.kssidll.arru.data.preference.getDynamicColor
import com.kssidll.arru.data.preference.getExportLocation
import com.kssidll.arru.data.preference.getExportType
import com.kssidll.arru.data.preference.setCurrencyFormatLocale
import com.kssidll.arru.data.preference.setExportType
import com.kssidll.arru.data.preference.setThemeColorScheme
import com.kssidll.arru.data.preference.setThemeDynamicColor
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.domain.usecase.ChangeDatabaseLocationUseCase
import com.kssidll.arru.domain.usecase.DatabaseMoveResult
import com.kssidll.arru.domain.usecase.ExportDataUIBlockingUseCase
import com.kssidll.arru.domain.usecase.ExportDataWithServiceUseCase
import com.kssidll.arru.helper.getReadablePathFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SettingsUiState(
    val exportType: AppPreferences.Export.Type.Values? = null,
    val exportUri: Uri? = null,
    val theme: AppPreferences.Theme.ColorScheme.Values? = null,
    val isInDynamicColor: Boolean = false,
    val currencyFormatLocale: Locale? = null,
    val databaseLocation: AppPreferences.Database.Location.Values? = null,

    val databaseLocationChangeFailedError: Boolean = false,
    val advancedSettingsVisible: Boolean = false,
) {
    val databaseLocationChangeVisible = databaseLocation != null

    val exportUriVisible = exportUri != null
    val readableExportUriString = exportUri?.let { getReadablePathFromUri(it) } ?: String()
}

sealed class SettingsEvent {
    data object NavigateBack: SettingsEvent()
    data object NavigateBackups: SettingsEvent()
    data object ExportData: SettingsEvent()
    data class SetExportType(val newExportType: AppPreferences.Export.Type.Values): SettingsEvent()
    data object SetExportUri: SettingsEvent()
    data class SetTheme(val newTheme: AppPreferences.Theme.ColorScheme.Values): SettingsEvent()

    @RequiresApi(31)
    data class SetDynamicColor(val newDynamicColor: Boolean): SettingsEvent()
    data class SetCurrencyFormatLocale(val newCurrencyFormatLocale: Locale?): SettingsEvent()
    data class SetLocale(val newLocale: AppLocale?): SettingsEvent()

    @RequiresApi(30)
    data class SetDatabaseLocation(val newLocation: AppPreferences.Database.Location.Values): SettingsEvent()
    data object DismissDatabaseLocationChangeError: SettingsEvent()

    data object ToggleAdvancedSettingsVisibility : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val exportDataWithServiceUseCase: ExportDataWithServiceUseCase,
    private val exportDataUIBlockingUseCase: ExportDataUIBlockingUseCase,
    private val changeDatabaseLocationUseCase: ChangeDatabaseLocationUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppPreferences.getExportType(appContext).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        exportType = it
                    )
                }
            }
        }

        viewModelScope.launch {
            AppPreferences.getExportLocation(appContext).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        exportUri = it
                    )
                }
            }
        }

        viewModelScope.launch {
            AppPreferences.getColorScheme(appContext).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        theme = it
                    )
                }
            }
        }

        viewModelScope.launch {
            AppPreferences.getDynamicColor(appContext).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        isInDynamicColor = it
                    )
                }
            }
        }

        viewModelScope.launch {
            AppPreferences.getCurrencyFormatLocale(appContext).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        currencyFormatLocale = it
                    )
                }
            }
        }

        viewModelScope.launch {
            AppPreferences.getDatabaseLocation(appContext).collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        databaseLocation = it
                    )
                }
            }
        }
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NavigateBack -> {}

            is SettingsEvent.NavigateBackups -> {}

            is SettingsEvent.ExportData -> {}

            is SettingsEvent.SetExportType -> setExportType(event.newExportType)

            is SettingsEvent.SetExportUri -> {}

            is SettingsEvent.SetTheme -> setTheme(event.newTheme)
            is SettingsEvent.SetDynamicColor -> setDynamicColor(event.newDynamicColor)
            is SettingsEvent.SetCurrencyFormatLocale -> setCurrencyFormatLocale(event.newCurrencyFormatLocale)
            is SettingsEvent.SetLocale -> setLocale(event.newLocale)
            is SettingsEvent.SetDatabaseLocation -> setDatabaseLocation(event.newLocation)
            is SettingsEvent.DismissDatabaseLocationChangeError -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        databaseLocationChangeFailedError = false
                    )
                }
            }
            is SettingsEvent.ToggleAdvancedSettingsVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        advancedSettingsVisible = !currentState.advancedSettingsVisible
                    )
                }
            }
        }
    }

    private fun setExportType(newType: AppPreferences.Export.Type.Values) = viewModelScope.launch {
        AppPreferences.setExportType(
            appContext,
            newType
        )
    }

    private fun setTheme(newTheme: AppPreferences.Theme.ColorScheme.Values) = viewModelScope.launch {
        AppPreferences.setThemeColorScheme(
            appContext,
            newTheme
        )
    }

    private fun setDynamicColor(newDynamicColor: Boolean) = viewModelScope.launch {
        if (Build.VERSION.SDK_INT >= 31) {
            AppPreferences.setThemeDynamicColor(
                appContext,
                newDynamicColor
            )
        } else {
            Log.e("SettingsViewModel", "Attempted to dynamic color on API ${Build.VERSION.SDK_INT}")
        }
    }

    private fun setCurrencyFormatLocale(newCurrencyFormatLocale: Locale?) = viewModelScope.launch {
        AppPreferences.setCurrencyFormatLocale(
            appContext,
            newCurrencyFormatLocale
        )
    }

    /**
     * Sets application locale to [locale]
     * @param locale [AppLocale] to set the application to, if null, sets application to system default
     */
    @MainThread
    private fun setLocale(locale: AppLocale?) {
        val localeList = if (locale != null) {
            LocaleListCompat.forLanguageTags(locale.tag)
        } else LocaleListCompat.getEmptyLocaleList()

        setApplicationLocales(localeList)
    }

    private fun setDatabaseLocation(newLocation: AppPreferences.Database.Location.Values) = viewModelScope.launch {
        if (Build.VERSION.SDK_INT >= 30) {
            val result = changeDatabaseLocationUseCase(newLocation)

            when (result) {
                DatabaseMoveResult.SUCCESS -> {}
                DatabaseMoveResult.FAILED -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            databaseLocationChangeFailedError = true
                        )
                    }
                }
            }
        } else {
            Log.e("SettingsViewModel", "Attempted to change database location on API ${Build.VERSION.SDK_INT}")
        }
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
