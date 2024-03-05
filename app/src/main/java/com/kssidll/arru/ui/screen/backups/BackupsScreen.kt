package com.kssidll.arru.ui.screen.backups

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.utils.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun BackupsScreen(
    createBackup: () -> Unit,
    loadBackup: (dbFile: DatabaseBackup) -> Unit,
    deleteBackup: (dbFile: DatabaseBackup) -> Unit,
    availableBackups: List<DatabaseBackup>,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            // TODO remove when upload is implemented
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(),
                        backgroundContent = {}
                    ) {
                        Snackbar(
                            snackbarData = it,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        },
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
        ) {
            AnimatedVisibility(
                visible = availableBackups.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                BackupsScreenNothingToDisplayOverlay()
            }

            AnimatedVisibility(
                visible = availableBackups.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    var lastDate: Long? = null
                    availableBackups.forEach {
                        val currentDate = (it.time / DAY_IN_MILIS) * DAY_IN_MILIS

                        if (lastDate == null || currentDate != lastDate) {
                            stickyHeader {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            bottomStart = 24.dp,
                                            bottomEnd = 24.dp,
                                        ),
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        modifier = Modifier.width(600.dp)
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
                            key = it.file.absolutePath,
                            contentType = DatabaseBackup::class,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(600.dp)
                                ) {
                                    Spacer(modifier = Modifier.width(10.dp))

                                    IconButton(
                                        onClick = {
                                            deleteBackup(it)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = stringResource(id = R.string.backup_delete),
                                            tint = MaterialTheme.colorScheme.error.copy(optionalAlpha),
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
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
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Spacer(modifier = Modifier.width(10.dp))

                                                Text(
                                                    text = SimpleDateFormat(
                                                        "HH:mm:ss",
                                                        Locale.getDefault()
                                                    ).format(it.time),
                                                    style = Typography.headlineMedium,
                                                )

                                                Spacer(modifier = Modifier.weight(1f))

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = it.totalTransactions.toString(),
                                                            style = Typography.titleMedium,
                                                        )

                                                        Spacer(Modifier.width(5.dp))

                                                        Icon(
                                                            imageVector = Icons.Outlined.ShoppingBasket,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp),
                                                            tint = MaterialTheme.colorScheme.tertiary,
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = TransactionBasket.actualTotalCost(it.totalSpending)
                                                                .formatToCurrency(),
                                                            style = Typography.titleMedium,
                                                        )

                                                        Spacer(Modifier.width(5.dp))

                                                        Icon(
                                                            imageVector = Icons.Outlined.Payment,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp),
                                                            tint = MaterialTheme.colorScheme.tertiary,
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(10.dp))
                                            }
                                        }
                                    }

                                    val snackbarUploadNotImplementedMessage =
                                        stringResource(id = R.string.backups_upload_not_implemented_snackbar_message)
                                    IconButton(
                                        onClick = {
                                            // TODO upload to cloud, set uploaded to 1f alpha, not uploaded to disabled
                                            scope.launch {
                                                if (snackbarHostState.currentSnackbarData == null) {
                                                    snackbarHostState.showSnackbar(
                                                        message = snackbarUploadNotImplementedMessage,
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary.copy(disabledAlpha),
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupsScreenNothingToDisplayOverlay() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = com.kssidll.arru.R.string.no_data_to_display_text),
                style = Typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.no_data_to_display_add_backup_hint),
                style = Typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        val pathColor = MaterialTheme.colorScheme.tertiary
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val curveEndX = size.width - 90.dp.toPx()
            val curveEndY = size.height - 45.dp.toPx()
            val lineWidth = 3.dp.toPx()


            drawPath(
                path = Path().apply {
                    moveTo(
                        size.width / 2,
                        24.dp.toPx()
                    )
                    quadraticBezierTo(
                        size.width * 1 / 4,
                        size.height / 2,
                        curveEndX,
                        curveEndY,
                    )

                    // temporarily disabled arrow as it looks too weird on wide screens
//                    relativeLineTo(
//                        -32.dp.toPx(),
//                        6.dp.toPx()
//                    )
//                    relativeMoveTo(
//                        34.dp.toPx(),
//                        -6.dp.toPx()
//                    )
//                    relativeLineTo(
//                        -6.dp.toPx(),
//                        -33.dp.toPx()
//                    )
                },
                style = Stroke(width = lineWidth),
                color = pathColor
            )
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
