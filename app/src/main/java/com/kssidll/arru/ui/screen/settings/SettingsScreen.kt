package com.kssidll.arru.ui.screen.settings


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.domain.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.screen.settings.component.*
import com.kssidll.arru.ui.theme.*

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

@PreviewLightDark
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
