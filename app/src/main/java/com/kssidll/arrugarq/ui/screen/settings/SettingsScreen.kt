package com.kssidll.arrugarq.ui.screen.settings


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.screen.settings.component.*
import com.kssidll.arrugarq.ui.theme.*

/**
 * @param setLocale Callback called as request to change current Locale. Provides requested locale as parameter
 * @param onBack Called to request a back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    setLocale: (locale: AppLocale?) -> Unit,
    onBack: () -> Unit,
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
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                LanguageExposedDropdown(
                    setLocale = setLocale,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

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
                setLocale = {},
                onBack = {},
            )
        }
    }
}
