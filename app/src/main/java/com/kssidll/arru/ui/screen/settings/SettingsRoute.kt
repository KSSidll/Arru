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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.provider.DocumentsContractCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kssidll.arru.R
import com.kssidll.arru.data.database.ImportError
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getExportLocation
import com.kssidll.arru.data.preference.setExportLocation
import com.kssidll.arru.helper.getLocalizedString
import com.kssidll.arru.service.DataExportService
import com.kssidll.arru.service.PersistentNotificationService
import com.kssidll.arru.ui.theme.Typography
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
    navigateBackups: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var uiBlockingProgressPopupVisible by remember {
        mutableStateOf(false)
    }

    var uiBlockingJob: Job? by remember {
        mutableStateOf(null)
    }

    var uiBlockingMaxProgress by remember {
        mutableIntStateOf(0)
    }

    var uiBlockingProgress by remember {
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
                    uiBlockingJob?.cancel()

                    uiBlockingProgressPopupVisible = true

                    uiBlockingJob = viewModel.exportUIBlocking(
                        parentUri,
                        onMaxProgressChange = {
                            uiBlockingMaxProgress = it
                        },
                        onProgressChange = {
                            uiBlockingProgress = it
                        },
                        onFinished = {
                            val job = Job(uiBlockingJob)
                            val finishScope = CoroutineScope(Dispatchers.Default + job)

                            finishScope.launch {
                                delay(500)

                                uiBlockingProgressPopupVisible = false
                                uiBlockingMaxProgress = 0
                                uiBlockingProgress = 0
                            }
                        }
                    )
                }
            }
        }
    )

    val persistentNotificationServicePermissionsState = rememberMultiplePermissionsState(
        permissions = PersistentNotificationService.Permissions.ALL.asList(),
        onPermissionsResult = {
            viewModel.handleEvent(SettingsEvent.TogglePersistentNotification)
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

    var importErrorVisible by remember { mutableStateOf(false) }
    var importErrorText by remember { mutableStateOf("") }
    val importFolderPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
            it?.let {
                uiBlockingJob?.cancel()

                uiBlockingProgressPopupVisible = true

                uiBlockingJob = viewModel.importFromUri(
                    uri = it,
                    onMaxProgressChange = {
                        uiBlockingMaxProgress = it
                    },
                    onProgressChange = {
                        uiBlockingProgress = it
                    },
                    onFinished = {
                        val job = Job(uiBlockingJob)
                        val finishScope = CoroutineScope(Dispatchers.Default + job)

                        finishScope.launch {
                            delay(500)

                            uiBlockingProgressPopupVisible = false
                            uiBlockingMaxProgress = 0
                            uiBlockingProgress = 0
                        }
                    },
                    onError = {
                        uiBlockingProgressPopupVisible = false
                        uiBlockingMaxProgress = 0
                        uiBlockingProgress = 0

                        importErrorVisible = true

                        importErrorText = when (it) {
                            is ImportError.FailedToDetermineVersion -> {
                                buildString {
                                    append(context.getLocalizedString(R.string.import_error_failed_to_determine_version))
                                    append(" ${it.name}")
                                }
                            }

                            is ImportError.FailedToOpenFile -> {
                                buildString {
                                    append(context.getLocalizedString(R.string.import_error_failed_to_open_file))
                                    append(" ${it.fileName}")
                                }
                            }

                            is ImportError.MissingFiles -> {
                                buildString {
                                    append(context.getLocalizedString(R.string.import_error_missing_files))
                                    it.expectedFileGroups.forEach {
                                        append("\n[ ${it.joinToString(", ")} ]")
                                    }
                                }
                            }

                            is ImportError.ParseError -> {
                                context.getLocalizedString(R.string.import_error_parse_error)
                            }
                        }
                    }
                )
            }
        }

    Box(modifier = modifier.fillMaxSize()) {

        val uiState = viewModel.uiState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value

        SettingsScreen(
            uiState = uiState,
            onEvent = { event ->
                when (event) {
                    is SettingsEvent.NavigateBack -> navigateBack()
                    is SettingsEvent.NavigateBackups -> navigateBackups()

                    is SettingsEvent.ExportData -> {
                        scope.launch {
                            val uri = AppPreferences.getExportLocation(context).first()
                            if (uri != null) {
                                exportServicePermissionsState.launchMultiplePermissionRequest()
                            } else {
                                exportFolderPickerWithExportLauncher.launch(null)
                            }
                        }
                    }

                    is SettingsEvent.ImportData -> {
                        scope.launch {
                            importFolderPickerLauncher.launch(uiState.exportUri)
                        }
                    }

                    is SettingsEvent.SetExportType -> viewModel.handleEvent(event)

                    is SettingsEvent.SetExportUri -> {
                        exportFolderPickerLauncher.launch(uiState.exportUri)
                    }

                    is SettingsEvent.TogglePersistentNotification -> {
                        persistentNotificationServicePermissionsState.launchMultiplePermissionRequest()
                    }

                    is SettingsEvent.SetTheme -> viewModel.handleEvent(event)
                    is SettingsEvent.SetDynamicColor -> viewModel.handleEvent(event)
                    is SettingsEvent.SetCurrencyFormatLocale -> viewModel.handleEvent(event)
                    is SettingsEvent.SetLocale -> viewModel.handleEvent(event)
                    is SettingsEvent.SetDatabaseLocation -> viewModel.handleEvent(event)
                    is SettingsEvent.DismissDatabaseLocationChangeError -> viewModel.handleEvent(event)
                    is SettingsEvent.ToggleAdvancedSettingsVisibility -> viewModel.handleEvent(event)
                    is SettingsEvent.CloseDatabaseLocationChangeExtremeDangerActionConfirmationDialog -> viewModel.handleEvent(event)
                    is SettingsEvent.ConfirmDatabaseLocationChangeExtremeDangerAction -> viewModel.handleEvent(event)
                }
            }
        )

        AnimatedVisibility(
            visible = uiBlockingProgressPopupVisible,
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
                                        if (uiBlockingMaxProgress == 0) {
                                            0f
                                        } else {
                                            uiBlockingProgress.toFloat() / uiBlockingMaxProgress.toFloat()
                                        }
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
                                    text = "$uiBlockingProgress / $uiBlockingMaxProgress",
                                    style = Typography.bodySmall,
                                    color = LocalContentColor.current.copy(alpha = 0.6f)
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Button(
                                onClick = {
                                    uiBlockingJob?.cancel()
                                    uiBlockingProgressPopupVisible = false
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

        AnimatedVisibility(visible = importErrorVisible) {
            BasicAlertDialog(
                onDismissRequest = {
                    importErrorVisible = false
                }
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                          .width(300.dp)
                          .height(300.dp)
                    ) {
                        Text(
                            text = importErrorText,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}
