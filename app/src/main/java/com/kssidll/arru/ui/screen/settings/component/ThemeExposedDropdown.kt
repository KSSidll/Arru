package com.kssidll.arru.ui.screen.settings.component

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
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography

/**
 * Exposed dropdown to select theme
 *
 * @param currentTheme Currently selected theme
 * @param setTheme Callback to call to request theme change. Provides requested theme as parameter
 * @param modifier Modifier to apply to the dropdown menu box
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeExposedDropdown(
    currentTheme: AppPreferences.Theme.ColorScheme.Values,
    setTheme: (newTheme: AppPreferences.Theme.ColorScheme.Values) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
        modifier = modifier,
    ) {
        TextField(
            readOnly = true,
            value = currentTheme.getTranslation(),
            textStyle = Typography.bodyMedium,
            onValueChange = {},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
            },
            label = {
                Text(text = stringResource(id = R.string.theme), style = Typography.titleLarge)
            },
            colors =
                ExposedDropdownMenuDefaults.textFieldColors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,
                ),
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
        )

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false },
            modifier =
                Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                ),
        ) {
            AppPreferences.Theme.ColorScheme.Values.entries.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(text = theme.getTranslation(), style = Typography.bodyLarge) },
                    onClick = {
                        setTheme(theme)
                        dropdownExpanded = false
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ThemeExposedDropdownPreview() {
    ArruTheme {
        Surface {
            ThemeExposedDropdown(
                currentTheme = AppPreferences.Theme.ColorScheme.DEFAULT,
                setTheme = {},
            )
        }
    }
}
