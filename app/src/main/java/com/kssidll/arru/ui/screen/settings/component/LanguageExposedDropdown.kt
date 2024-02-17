package com.kssidll.arru.ui.screen.settings.component


import android.content.res.Configuration.*
import androidx.appcompat.app.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.domain.*
import com.kssidll.arru.ui.theme.*
import java.util.*

/**
 * Exposed dropdown to select locale
 * @param setLocale Callback to call to request locale change. Provides requested locale as parameter, provides null as System locale
 * @param modifier Modifier to apply to the dropdown menu box
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageExposedDropdown(
    setLocale: (newLocale: AppLocale?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = {
            dropdownExpanded = !dropdownExpanded
        },
        modifier = modifier
    ) {
        // no support for multiple languages so we just check for first value
        val currentLocale = AppCompatDelegate.getApplicationLocales()[0]

        TextField(
            readOnly = true,
            value = currentLocale?.displayLanguage?.replaceFirstChar { it.titlecase(currentLocale) }
                ?: "System",
            textStyle = Typography.bodyMedium,
            onValueChange = {},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
            },
            label = {
                Text(
                    text = stringResource(id = R.string.language),
                    style = Typography.titleLarge
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = {
                dropdownExpanded = false
            },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    ),
                )
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
                },
            )

            AppLocale.entries.forEach { appLocale ->
                val locale = Locale(appLocale.code)

                DropdownMenuItem(
                    text = {
                        Text(
                            text = locale.getDisplayName(locale)
                                .replaceFirstChar { it.titlecase(locale) },
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

@Preview(
    group = "LanguageExposedDropdown",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "LanguageExposedDropdown",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
private fun LanguageExposedDropdownPreview() {
    ArrugarqTheme {
        Surface {
            LanguageExposedDropdown(
                setLocale = {},
            )
        }
    }
}
