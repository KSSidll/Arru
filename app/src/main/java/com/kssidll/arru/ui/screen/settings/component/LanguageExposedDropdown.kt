package com.kssidll.arru.ui.screen.settings.component


import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import java.util.Locale

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
            modifier = Modifier.menuAnchor(
                type = MenuAnchorType.PrimaryNotEditable
            )
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

@PreviewLightDark
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
