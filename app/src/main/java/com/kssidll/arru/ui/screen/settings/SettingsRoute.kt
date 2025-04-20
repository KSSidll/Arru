package com.kssidll.arru.ui.screen.settings


import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.provider.DocumentsContractCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kssidll.arru.R
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getDatabaseLocation
import com.kssidll.arru.data.preference.getExportLocation
import com.kssidll.arru.data.preference.setDatabaseLocation
import com.kssidll.arru.data.preference.setExportLocation
import com.kssidll.arru.service.DataExportService
import com.kssidll.arru.ui.theme.Typography
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
    navigateBackups: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var exportProgressPopupVisible by remember {
        mutableStateOf(false)
    }

    var exportUIBlockingJob: Job? by remember {
        mutableStateOf(null)
    }

    var exportMaxProgress by remember {
        mutableIntStateOf(0)
    }

    var exportProgress by remember {
        mutableIntStateOf(0)
    }

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

                val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
                    uri,
                    DocumentsContractCompat.getTreeDocumentId(uri)!!
                )!!

                if (it.all { it.value }) {
                    viewModel.exportWithService(parentUri)
                } else {
                    exportUIBlockingJob?.cancel()

                    exportProgressPopupVisible = true

                    exportUIBlockingJob = viewModel.exportUIBlocking(
                        parentUri,
                        onMaxProgressChange = {
                            exportMaxProgress = it
                        },
                        onProgressChange = {
                            exportProgress = it
                        },
                        onFinished = {
                            val job = Job(exportUIBlockingJob)
                            val finishScope = CoroutineScope(Dispatchers.Default + job)

                            finishScope.launch {
                                delay(500)

                                exportProgressPopupVisible = false
                                exportMaxProgress = 0
                                exportProgress = 0
                            }
                        }
                    )
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

            context.contentResolver.takePersistableUriPermission(
                uri,
                persistableFlags
            )

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

    Box(modifier = modifier.fillMaxSize()) {

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
            databaseLocation = AppPreferences.getDatabaseLocation(context).collectAsState(initial = null).value,
            onChangeDatabaseLocation = {
                scope.launch {
                    AppPreferences.setDatabaseLocation(context, it)
                }
            },
            currentExportType = viewModel.currentExportType.collectAsState(initial = AppPreferences.Export.Type.DEFAULT).value,
            onExportTypeChange = {
                viewModel.setExportType(it)
            },
            exportUri = AppPreferences.getExportLocation(context).collectAsState(initial = null).value,
            onChangeExportUri = { currentExportUri ->
                exportFolderPickerLauncher.launch(currentExportUri)
            },
            currentTheme = viewModel.currentTheme.collectAsState(initial = AppPreferences.Theme.ColorScheme.DEFAULT).value,
            setTheme = {
                viewModel.setTheme(it)
            },
            isInDynamicColor = viewModel.isInDynamicColor.collectAsState(initial = AppPreferences.Theme.DynamicColor.DEFAULT).value,
            setDynamicColor = {
                viewModel.setDynamicColor(it)
            },
            currentCurrencyFormat = viewModel.currencyFormatLocale.collectAsState(Locale.getDefault()).value,
            setCurrencyFormat = {
                viewModel.setCurrencyFormatLocale(it)
            }
        )

        AnimatedVisibility(
            visible = exportProgressPopupVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Surface(
                    shape = ShapeDefaults.Large,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Column {
                            Column(horizontalAlignment = Alignment.End) {
                                LinearProgressIndicator(
                                    progress = {
                                        exportProgress.toFloat() / exportMaxProgress.toFloat()
                                    },
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.height(
                                        Typography.titleLarge.fontSize.value.dp.minus(
                                            6.dp
                                        )
                                    )
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "$exportProgress / $exportMaxProgress",
                                    style = Typography.bodySmall,
                                    color = LocalContentColor.current.copy(alpha = 0.6f)
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Button(
                                onClick = {
                                    exportUIBlockingJob?.cancel()
                                    exportProgressPopupVisible = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.data_export_cancel),
                                    style = Typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
