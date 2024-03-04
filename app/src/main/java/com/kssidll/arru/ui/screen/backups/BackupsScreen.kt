package com.kssidll.arru.ui.screen.backups

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*
import java.io.*
import java.text.*
import java.util.*

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun BackupsScreen(
    createBackup: () -> Unit,
    loadBackup: (dbFile: File) -> Unit,
    deleteBackup: (dbFile: File) -> Unit,
    availableBackups: List<File>,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(
                        text = stringResource(id = R.string.backups),
                        style = Typography.titleLarge,
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    createBackup()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.backup_create),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(600.dp)
            ) {
                LazyColumn {
                    var lastDate: Long? = null
                    availableBackups.forEach {
                        // TODO make a Backup struct with file, name, datetime, total transactions and size, load that struct from AppDatabase instead of the File
                        // TODO abstract that Backup struct so that each one has a 'selected' field, start select mode on long click like in most gallery apps to allow delete/bulk delete
                        val name = it.nameWithoutExtension
                        val lastSeparatorIndex = name.lastIndexOf('_')

                        val currentDatePrecise = name.substring(lastSeparatorIndex + 1)
                            .toLongOrNull()
                            ?: error("Backup naming schema has changed for some reason, couldn't parse")

                        val currentDate = (currentDatePrecise / DAY_IN_MILIS) * DAY_IN_MILIS

                        if (lastDate == null || currentDate != lastDate) {
                            stickyHeader {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            bottomStart = 24.dp,
                                            bottomEnd = 24.dp,
                                        ),
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                    ) {
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                modifier = Modifier.align(Alignment.Center),
                                                text = SimpleDateFormat(
                                                    "MMM d, yyyy",
                                                    Locale.getDefault()
                                                ).format(currentDate),
                                                style = Typography.headlineMedium,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        lastDate = currentDate

                        item(
                            key = it.absolutePath,
                            contentType = File::class,
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable(
                                        role = Role.Button,
                                        onClickLabel = stringResource(id = R.string.backup_load),
                                        onClick = {
                                            loadBackup(it)
                                        }
                                    )
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(currentDatePrecise),
                                        style = Typography.headlineMedium,
                                    )
                                }
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
private fun BackupsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            BackupsScreen(
                createBackup = {},
                loadBackup = {},
                deleteBackup = {},
                availableBackups = emptyList(),
                onBack = {},
            )
        }
    }
}
