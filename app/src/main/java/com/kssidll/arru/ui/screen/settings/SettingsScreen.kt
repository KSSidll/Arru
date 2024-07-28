package com.kssidll.arru.ui.screen.settings


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.domain.AppLocale
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.screen.settings.component.LanguageExposedDropdown
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

/**
 * @param setLocale Callback called as request to change current Locale. Provides requested locale as parameter
 * @param onBack Called to request a back navigation
 * @param onBackupsClick Called when the backups button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    setLocale: (locale: AppLocale?) -> Unit,
    onBack: () -> Unit,
    onBackupsClick: () -> Unit,
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
            )
        }
    }
}
