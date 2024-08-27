package com.kssidll.arru.ui.screen.settings


import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.helper.getReadablePathFromUri
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.screen.settings.component.LanguageExposedDropdown
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

/**
 * @param setLocale Callback called as request to change current Locale. Provides requested locale as parameter
 * @param onBack Called to request a back navigation
 * @param onBackupsClick Called when the backups button is clicked
 * @param onExportClick Called when the export button is clicked
 * @param currentExportType Current export type
 * @param onExportTypeChange Called when the export type changes. Provides new export type as parameter
 * @param exportUri Uri of the export location if any
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    setLocale: (locale: AppLocale?) -> Unit,
    onBack: () -> Unit,
    onBackupsClick: () -> Unit,
    onExportClick: (AppPreferences.Export.Type.Values) -> Unit,
    currentExportType: AppPreferences.Export.Type.Values,
    onExportTypeChange: (AppPreferences.Export.Type.Values) -> Unit,
    exportUri: Uri?,
    onChangeExportUri: (Uri?) -> Unit,
) {
    var exportOptionsExpanded: Boolean by remember {
        mutableStateOf(false)
    }

    val layoutDirection = LocalLayoutDirection.current

    val buttonStartPadding =
        ButtonDefaults.ContentPadding.calculateStartPadding(layoutDirection)
    val buttonEndPadding =
        ButtonDefaults.ContentPadding.calculateEndPadding(layoutDirection)

    val buttonHorizontalPadding = buttonStartPadding + buttonEndPadding

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(),
                        backgroundContent = {}
                    ) {
                        Snackbar(
                            snackbarData = it,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        },
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = Typography.titleLarge,
                    )
                },
            )
        },
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars
                .only(
                    WindowInsetsSides.Horizontal
                )
        )
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onBackupsClick()
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .width(TextFieldDefaults.MinWidth - buttonHorizontalPadding)
                    ) {
                        Text(
                            text = stringResource(id = R.string.backups),
                            style = Typography.titleMedium
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Button(
                        shape = RoundedCornerShape(
                            topStartPercent = 50,
                            bottomStartPercent = 50,
                        ),
                        onClick = {
                            onExportClick(currentExportType)
//                            TODO show snackbar after export starts if it's in the background
//                            scope.launch {
//                                if (snackbarHostState.currentSnackbarData == null) {
//                                    snackbarHostState.showSnackbar(
//                                        message = "Exporting"
//                                    )
//                                }
//                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(TextFieldDefaults.MinWidth - buttonStartPadding - 30.dp)
                            .height(ButtonDefaults.MinHeight + 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.export),
                            style = Typography.titleMedium
                        )
                    }

                    Button(
                        shape = RoundedCornerShape(
                            topEndPercent = 50,
                            bottomEndPercent = 50,
                        ),
                        onClick = {
                            exportOptionsExpanded = !exportOptionsExpanded
                        },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .width(30.dp + buttonStartPadding)
                            .height(ButtonDefaults.MinHeight + 4.dp)
                    ) {
                        Crossfade(
                            targetState = exportOptionsExpanded,
                            animationSpec = tween(200),
                            label = "export options visibility toggle"
                        ) { expanded ->
                            if (expanded) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    modifier = Modifier.padding(12.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = exportOptionsExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = ShapeDefaults.Large,
                            tonalElevation = 1.dp,
                            modifier = Modifier.width(TextFieldDefaults.MinWidth + buttonHorizontalPadding)
                        ) {
                            Column(modifier = Modifier.animateContentSize()) {
                                AnimatedVisibility(visible = exportUri != null) {
                                    Surface(
                                        shape = ShapeDefaults.Large,
                                        tonalElevation = 2.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onChangeExportUri(exportUri)
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(24.dp)) {
                                            Text(
                                                text = "${stringResource(id = R.string.export_location)}:"
                                            )

                                            Text(
                                                text = exportUri?.let { getReadablePathFromUri(it) }
                                                    ?: String(),
                                                style = Typography.labelMedium
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }
                                }

                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "${stringResource(id = R.string.export_type)}:"
                                    )

                                    AppPreferences.Export.Type.Values.entries.forEach {
                                        Surface(
                                            shape = ShapeDefaults.Large,
                                            tonalElevation = 2.dp,
                                            onClick = {
                                                onExportTypeChange(it)
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(
                                                    vertical = 8.dp,
                                                    horizontal = 16.dp
                                                )
                                            ) {
                                                Text(
                                                    text = it.name
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                if (it == currentExportType) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                } else {
                                                    Spacer(modifier = Modifier.width(24.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LanguageExposedDropdown(setLocale = setLocale)
            }
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
private fun SettingsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreen(
                setLocale = {},
                onBack = {},
                onBackupsClick = {},
                onExportClick = {},
                currentExportType = AppPreferences.Export.Type.Values.CompactCSV,
                onExportTypeChange = {},
                exportUri = null,
                onChangeExportUri = {}
            )
        }
    }
}
