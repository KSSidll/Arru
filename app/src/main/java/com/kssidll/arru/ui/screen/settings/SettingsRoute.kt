package com.kssidll.arru.ui.screen.settings


import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kssidll.arru.service.DataExportService
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
    navigateBackups: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val exportUri = remember { mutableStateOf(Uri.EMPTY) }

    val exportServicePermissionsState = rememberMultiplePermissionsState(
        permissions = DataExportService.Permissions.ALL.asList(),
        onPermissionsResult = {
            if (exportUri == Uri.EMPTY) {
                Log.d(
                    "SETTINGS_ROUTE",
                    "exportServiceLaunch: uri is empty"
                )
                return@rememberMultiplePermissionsState
            }

            if (it.all { true }) {
                viewModel.exportWithService(exportUri.value)
            } else {
                // TODO export but show on screen instead of a notification
            }
        }
    )

    val exportFolderPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
            it?.let { uri ->
                exportUri.value = uri
                exportServicePermissionsState.launchMultiplePermissionRequest()
            }
        }

    SettingsScreen(
        setLocale = {
            viewModel.setLocale(it)
        },
        onBack = navigateBack,
        onBackupsClick = navigateBackups,
        onExportClick = {
            exportFolderPickerLauncher.launch(null)
        }
    )
}
