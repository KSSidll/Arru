package com.kssidll.arrugarq.ui.screen.settings


import android.content.res.Configuration.*
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*
import java.util.*

/**
 * @param state [SettingsScreenState] instance representing the screen state
 * @param onBack Called to request a back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    state: SettingsScreenState,
    setLocale: (locale: AppLocale?) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {

                },
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                var dropdownExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = {
                        dropdownExpanded = !dropdownExpanded
                    },
                ) {
                    // no support for multiple languages so we just check for first value
                    val currentLocale = getApplicationLocales()[0]

                    TextField(
                        readOnly = true,
                        value = currentLocale?.displayLanguage?.replaceFirstChar { it.titlecase(currentLocale) } ?: "System",
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = {
                            dropdownExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(id = R.string.system),
                                    style = Typography.bodyLarge,
                                )
                            },
                            onClick = {
                                setLocale(null)
                                dropdownExpanded = false
                            }
                        )

                        AppLocale.entries.forEach { appLocale ->
                            val locale = Locale(appLocale.code)

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = locale.displayLanguage.replaceFirstChar { it.titlecase(locale) },
                                        style = Typography.bodyLarge,
                                    )
                                },
                                onClick = {
                                    setLocale(appLocale)
                                    dropdownExpanded = false
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}

/**
 * Data representing [SettingsScreen] screen state
 */
data class SettingsScreenState(
    val placeholder: MutableState<Boolean> = mutableStateOf(false),
)

@Preview(
    group = "SettingsScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "SettingsScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
private fun SettingsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreen(
                state = SettingsScreenState(),
                setLocale = {},
                onBack = {},
            )
        }
    }
}
