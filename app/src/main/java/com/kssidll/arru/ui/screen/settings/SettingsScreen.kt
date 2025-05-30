package com.kssidll.arru.ui.screen.settings


import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.field.SearchField
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.screen.settings.component.LanguageExposedDropdown
import com.kssidll.arru.ui.screen.settings.component.ThemeExposedDropdown
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.kssidll.compiled.CurrencyLocaleData
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (event: SettingsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var exportOptionsExpanded: Boolean by remember {
        mutableStateOf(false)
    }

    var isCurrencyFormatSearchExpanded: Boolean by remember {
        mutableStateOf(false)
    }

    val layoutDirection = LocalLayoutDirection.current

    val buttonStartPadding =
        ButtonDefaults.ContentPadding.calculateStartPadding(layoutDirection)
    val buttonEndPadding =
        ButtonDefaults.ContentPadding.calculateEndPadding(layoutDirection)

    val buttonHorizontalPadding = buttonStartPadding + buttonEndPadding

    AnimatedVisibility(visible = uiState.databaseLocationChangeShowExtremeDangerActionConfirmationDialogVisible) {
        BasicAlertDialog(
            onDismissRequest = {
                onEvent(SettingsEvent.CloseDatabaseLocationChangeExtremeDangerActionConfirmationDialog)
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
                        .width(280.dp)
                        .height(160.dp)
                        .padding(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.database_location_change_extreme_danger_alert),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = {
                            onEvent(SettingsEvent.CloseDatabaseLocationChangeExtremeDangerActionConfirmationDialog)
                        },
                        shape = RoundedCornerShape(
                            topStartPercent = 50,
                            bottomStartPercent = 50
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.take_me_to_safety),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Button(
                        onClick = {
                            onEvent(SettingsEvent.ConfirmDatabaseLocationChangeExtremeDangerAction)
                        },
                        shape = RoundedCornerShape(
                            topEndPercent = 50,
                            bottomEndPercent = 50
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.i_know_what_i_am_doing),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(visible = uiState.databaseLocationChangeFailedError) {
        BasicAlertDialog(
            onDismissRequest = {
                onEvent(SettingsEvent.DismissDatabaseLocationChangeError)
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
                        .width(280.dp)
                        .height(160.dp)
                ) {
                    Text(
                        text = stringResource(R.string.database_location_change_failed_error),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = { onEvent(SettingsEvent.NavigateBack) },
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = Typography.titleLarge,
                    )
                },
            )
        },
        modifier = modifier.windowInsetsPadding(
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
            if (isCurrencyFormatSearchExpanded) {
                SearchableListDialog(
                    showAddButton = false,
                    items = Data.Loaded(CurrencyLocaleData.items),
                    itemText = {
                        buildString {
                            append(it.first)
                            append(" | ")
                            append(it.second.map {
                                NumberFormat.getCurrencyInstance(
                                    Locale.forLanguageTag(
                                        it
                                    )
                                ).currency?.symbol ?: String()
                            }.distinct().fastJoinToString(" "))
                        }
                    },
                    onDismissRequest = {
                        isCurrencyFormatSearchExpanded = false
                    },
                    calculateScore = { item, query ->
                        FuzzySearch.extractOne(
                            query,
                            item.second.map {
                                NumberFormat.getCurrencyInstance(
                                    Locale.forLanguageTag(
                                        it
                                    )
                                ).currency?.symbol ?: String()
                            } + item.first
                        ).score
                    },
                    showDefaultValueItem = true,
                    defaultItemText = stringResource(R.string.language),
                    onItemClick = {
                        onEvent(SettingsEvent.SetCurrencyFormatLocale(it?.let { Locale.forLanguageTag(it.second.first()) }))
                        isCurrencyFormatSearchExpanded = false
                    }
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onEvent(SettingsEvent.NavigateBackups)
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
                                onEvent(SettingsEvent.ExportData)
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .width(TextFieldDefaults.MinWidth - buttonStartPadding - 30.dp)
                                .height(ButtonDefaults.MinHeight + 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.export),
                                style = Typography.titleMedium,
                                modifier = Modifier.padding(start = buttonStartPadding + 30.dp)
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
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
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
                                    AnimatedVisibility(visible = uiState.exportUriVisible) {
                                        Surface(
                                            shape = ShapeDefaults.Large,
                                            tonalElevation = 2.dp,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onEvent(SettingsEvent.SetExportUri)
                                                }
                                        ) {
                                            Column(modifier = Modifier.padding(24.dp)) {
                                                Text(
                                                    text = "${stringResource(id = R.string.export_location)}:",
                                                    style = Typography.labelLarge
                                                )

                                                Text(
                                                    text = uiState.readableExportUriString,
                                                    style = Typography.labelMedium
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text(
                                            text = "${stringResource(id = R.string.export_type)}:",
                                            style = Typography.labelLarge
                                        )

                                        AppPreferences.Export.Type.Values.entries.forEach {
                                            Surface(
                                                shape = ShapeDefaults.Large,
                                                tonalElevation = 2.dp,
                                                onClick = {
                                                    onEvent(SettingsEvent.SetExportType(it))
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
                                                        text = it.getTranslation(),
                                                        style = Typography.labelMedium
                                                    )

                                                    Spacer(modifier = Modifier.width(4.dp))

                                                    if (uiState.exportType == it) {
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


                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Button(
                            onClick = {
                                onEvent(SettingsEvent.ImportData)
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .width(TextFieldDefaults.MinWidth)
                                .height(ButtonDefaults.MinHeight + 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.import_),
                                style = Typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LanguageExposedDropdown(setLocale = { onEvent(SettingsEvent.SetLocale(it)) })

                    Spacer(modifier = Modifier.height(24.dp))

                    SearchField(
                        showAddButton = false,
                        label = stringResource(R.string.settings_currency_format),
                        value = 1.0f.formatToCurrency(uiState.currencyFormatLocale ?: Locale.getDefault()),
                        onClick = {
                            isCurrencyFormatSearchExpanded = true
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = uiState.theme != null) {
                        ThemeExposedDropdown(
                            currentTheme = uiState.theme ?: AppPreferences.Theme.ColorScheme.DEFAULT,
                            setTheme = { onEvent(SettingsEvent.SetTheme(it)) }
                        )
                    }

                    if (Build.VERSION.SDK_INT >= 31) {
                        val dynamicThemeInteractionSource = remember {
                            MutableInteractionSource()
                        }

                        Surface(
                            shape = ShapeDefaults.Large,
                            tonalElevation = 2.dp,
                            interactionSource = dynamicThemeInteractionSource,
                            onClick = {
                                onEvent(SettingsEvent.SetDynamicColor(!uiState.isInDynamicColor))
                            },
                            modifier = Modifier
                                .width(TextFieldDefaults.MinWidth)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            ) {
                                Checkbox(
                                    checked = uiState.isInDynamicColor,
                                    interactionSource = dynamicThemeInteractionSource,
                                    onCheckedChange = {
                                        onEvent(SettingsEvent.SetDynamicColor(!uiState.isInDynamicColor))
                                    }
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = stringResource(R.string.settings_dynamic_theme),
                                    style = Typography.labelMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = uiState.advancedSettingsVisible,
                    ) {
                        Column {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.width(TextFieldDefaults.MinWidth + buttonHorizontalPadding)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = stringResource(R.string.advanced),
                                style = Typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )

                            if (Build.VERSION.SDK_INT >= 30) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        shape = ShapeDefaults.Large,
                                        tonalElevation = 1.dp,
                                        modifier = Modifier.width(TextFieldDefaults.MinWidth + buttonHorizontalPadding)
                                    ) {
                                        Column(modifier = Modifier.animateContentSize()) {
                                            AnimatedVisibility(visible = uiState.databaseLocationChangeVisible) {
                                                Column(modifier = Modifier.padding(24.dp)) {
                                                    Text(
                                                        text = "${stringResource(id = R.string.database_location)}:",
                                                        style = Typography.labelLarge
                                                    )

                                                    AppPreferences.Database.Location.Values.entries.forEach {
                                                        Surface(
                                                            shape = ShapeDefaults.Large,
                                                            tonalElevation = 2.dp,
                                                            onClick = {
                                                                onEvent(SettingsEvent.SetDatabaseLocation(it))
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
                                                                    text = it.getTranslation(),
                                                                    style = Typography.labelMedium
                                                                )

                                                                Spacer(modifier = Modifier.width(4.dp))

                                                                if (uiState.databaseLocation == it) {
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
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // TODO remove when there's more advanced settings, currently the only one is for api 30+
                    if (Build.VERSION.SDK_INT >= 30) {
                        val advancedSettingsToggleInteractionSource = remember {
                            MutableInteractionSource()
                        }

                        Surface(
                            shape = ShapeDefaults.Large,
                            tonalElevation = 2.dp,
                            interactionSource = advancedSettingsToggleInteractionSource,
                            onClick = {
                                onEvent(SettingsEvent.ToggleAdvancedSettingsVisibility)
                            },
                            color = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .width(TextFieldDefaults.MinWidth)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            ) {
                                Checkbox(
                                    checked = uiState.advancedSettingsVisible,
                                    interactionSource = advancedSettingsToggleInteractionSource,
                                    onCheckedChange = {
                                        onEvent(SettingsEvent.ToggleAdvancedSettingsVisibility)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.onErrorContainer,
                                        uncheckedColor = MaterialTheme.colorScheme.onErrorContainer,
                                        checkmarkColor = MaterialTheme.colorScheme.errorContainer,
                                    )
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = stringResource(R.string.show_advanced_settings),
                                    style = Typography.labelMedium
                                )
                            }
                        }
                    }
                }
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
                uiState = SettingsUiState(),
                onEvent = {}
            )
        }
    }
}
