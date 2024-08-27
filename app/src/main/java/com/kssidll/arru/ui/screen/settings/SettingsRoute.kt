package com.kssidll.arru.ui.screen.settings


import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.provider.DocumentsContractCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getExportLocation
import com.kssidll.arru.data.preference.setExportLocation
import com.kssidll.arru.service.DataExportService
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
    navigateBackups: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportServicePermissionsState = rememberMultiplePermissionsState(
        permissions = DataExportService.Permissions.ALL.asList(),
        onPermissionsResult = {
            scope.launch {
                val uri = AppPreferences.getExportLocation(context).first() ?: run {
                    Log.d(
                        "SETTINGS_ROUTE",
                        "exportServiceLaunch: uri is empty"
                    )
                    return@launch
                }

                val persis = context.contentResolver.persistedUriPermissions

                val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
                    uri,
                    DocumentsContractCompat.getTreeDocumentId(uri)!!
                )!!

                if (it.all { true }) {
                    viewModel.exportWithService(parentUri)
                } else {
                    // TODO export but show on screen instead of a notification
                }
            }
        }
    )

    val setExportLocation: suspend (Uri) -> Unit = { uri ->
        val oldUri = AppPreferences.getExportLocation(context).first()

        if (oldUri != uri) {
            val persistableFlags =
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION

            if (oldUri != null) {
                try {
                    context.contentResolver.releasePersistableUriPermission(
                        oldUri,
                        persistableFlags
                    )
                } catch (_: Exception) {
                } // it doesn't really matter if we can't release
            }

            val persise = context.contentResolver.persistedUriPermissions

            context.contentResolver.takePersistableUriPermission(
                uri,
                persistableFlags
            )

            val persis = context.contentResolver.persistedUriPermissions

            AppPreferences.setExportLocation(context, uri)
        }
    }

    val exportFolderPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
            scope.launch {
                it?.let { uri ->
                    setExportLocation(uri)
                }
            }
        }

    val exportFolderPickerWithExportLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
            scope.launch {
                it?.let { uri ->
                    setExportLocation(uri)
                    exportServicePermissionsState.launchMultiplePermissionRequest()
                }
            }
        }

    SettingsScreen(
        setLocale = {
            viewModel.setLocale(it)
        },
        onBack = navigateBack,
        onBackupsClick = navigateBackups,
        onExportClick = {
            scope.launch {
                val uri = AppPreferences.getExportLocation(context).first()
                if (uri != null) {
                    exportServicePermissionsState.launchMultiplePermissionRequest()
                } else {
                    exportFolderPickerWithExportLauncher.launch(null)
                }
            }
        },
        currentExportType = viewModel.currentExportType.collectAsState(initial = AppPreferences.Export.Type.DEFAULT).value,
        onExportTypeChange = {
            viewModel.setExportType(it)
        },
        exportUri = AppPreferences.getExportLocation(context).collectAsState(initial = null).value,
        onChangeExportUri = { currentExportUri ->
            exportFolderPickerLauncher.launch(currentExportUri)
        }
    )
}
