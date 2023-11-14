package com.kssidll.arrugarq.ui.screen.settings


import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
): ViewModel() {
    val screenState: SettingsScreenState = SettingsScreenState()

}
