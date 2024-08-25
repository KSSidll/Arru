package com.kssidll.arru.ui.screen.settings


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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.screen.settings.component.LanguageExposedDropdown
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

/**
 * @param setLocale Callback called as request to change current Locale. Provides requested locale as parameter
 * @param onBack Called to request a back navigation
 * @param onBackupsClick Called when the backups button is clicked
 * @param onExportClick Called when the export button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    setLocale: (locale: AppLocale?) -> Unit,
    onBack: () -> Unit,
    onBackupsClick: () -> Unit,
    onExportClick: (AppPreferences.Export.Type.Values) -> Unit,
    currentExportType: AppPreferences.Export.Type.Values,
    onExportTypeChange: (AppPreferences.Export.Type.Values) -> Unit
) {
    Scaffold(
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
                    .width(600.dp)
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
                            .width(210.dp)
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

                val layoutDirection = LocalLayoutDirection.current

                val buttonStartPadding =
                    ButtonDefaults.ContentPadding.calculateStartPadding(layoutDirection)
                val buttonEndPadding =
                    ButtonDefaults.ContentPadding.calculateEndPadding(layoutDirection)

                val exportTypeExpanded = remember {
                    mutableStateOf(false)
                }

                Row {
                    Button(
                        shape = RoundedCornerShape(
                            topStartPercent = 50,
                            bottomStartPercent = 50,
                        ),
                        onClick = {
                            onExportClick(currentExportType)
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(180.dp + buttonStartPadding)
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
                            exportTypeExpanded.value = true
                        },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .width(30.dp + buttonEndPadding)
                            .height(ButtonDefaults.MinHeight + 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp)
                        )

                        DropdownMenu(
                            expanded = exportTypeExpanded.value,
                            onDismissRequest = {
                                exportTypeExpanded.value = false
                            },
                            offset = DpOffset(
                                x = 30.dp,
                                y = 0.dp
                            )
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (currentExportType == AppPreferences.Export.Type.Values.CompactCSV) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.width(24.dp))
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Text(
                                            text = "Compact CSV"
                                        )
                                    }
                                },
                                onClick = {
                                    exportTypeExpanded.value = false
                                    onExportTypeChange(AppPreferences.Export.Type.Values.CompactCSV)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (currentExportType == AppPreferences.Export.Type.Values.RawCSV) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.width(24.dp))
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Text(
                                            text = "Raw CSV"
                                        )
                                    }
                                },
                                onClick = {
                                    exportTypeExpanded.value = false
                                    onExportTypeChange(AppPreferences.Export.Type.Values.RawCSV)
                                }
                            )
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
                onExportTypeChange = {}
            )
        }
    }
}
