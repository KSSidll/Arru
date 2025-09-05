package com.kssidll.arru.ui.screen.backups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.R
import com.kssidll.arru.data.data.DatabaseBackup
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography
import com.kssidll.arru.ui.theme.disabledAlpha
import com.kssidll.arru.ui.theme.optionalAlpha
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BackupsScreen(
    uiState: BackupsUiState,
    onEvent: (event: BackupsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currencyLocale = LocalCurrencyFormatLocale.current ?: Locale.getDefault()

    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = { onEvent(BackupsEvent.NavigateBack) },
                title = {
                    Text(
                        text = stringResource(id = R.string.backups),
                        style = Typography.titleLarge,
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(BackupsEvent.CreateBackup) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.backup_create),
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                )
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues)) {
            AnimatedVisibility(
                visible = uiState.backups.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                BackupsScreenNothingToDisplayOverlay()
            }

            AnimatedVisibility(
                visible = uiState.backups.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    var lastDate: Long? = null
                    uiState.backups.forEach {
                        val currentDate = it.time / 86400000

                        if (lastDate == null || currentDate != lastDate) {
                            stickyHeader {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Surface(
                                        shape =
                                            RoundedCornerShape(
                                                bottomStart = 24.dp,
                                                bottomEnd = 24.dp,
                                            ),
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        modifier = Modifier.width(600.dp),
                                    ) {
                                        Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                            Text(
                                                modifier = Modifier.align(Alignment.Center),
                                                text =
                                                    SimpleDateFormat(
                                                            "MMM d, yyyy",
                                                            Locale.getDefault(),
                                                        )
                                                        .format(
                                                            (currentDate * 86400000) -
                                                                TimeZone.getDefault()
                                                                    .getOffset(
                                                                        currentDate * 86400000
                                                                    )
                                                        ),
                                                style = Typography.headlineMedium,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        lastDate = currentDate

                        item(key = it.file.absolutePath, contentType = DatabaseBackup::class) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(600.dp),
                                ) {
                                    Spacer(modifier = Modifier.width(10.dp))

                                    IconButton(
                                        onClick = {
                                            if (!it.locked) {
                                                onEvent(BackupsEvent.DeleteBackup(it))
                                            }
                                        }
                                    ) {
                                        Crossfade(
                                            targetState = it.locked,
                                            label = "delete'ability due to lock change animation",
                                        ) { locked ->
                                            if (locked) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = null,
                                                    tint =
                                                        MaterialTheme.colorScheme.error.copy(
                                                            disabledAlpha
                                                        ),
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription =
                                                        stringResource(id = R.string.backup_delete),
                                                    tint =
                                                        MaterialTheme.colorScheme.error.copy(
                                                            optionalAlpha
                                                        ),
                                                )
                                            }
                                        }
                                    }

                                    Box(
                                        modifier =
                                            Modifier.weight(1f)
                                                .clickable(
                                                    role = Role.Button,
                                                    onClickLabel =
                                                        stringResource(id = R.string.backup_load),
                                                    onClick = {
                                                        onEvent(BackupsEvent.LoadBackup(it))
                                                    },
                                                )
                                    ) {
                                        Box(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Spacer(modifier = Modifier.width(10.dp))

                                                Text(
                                                    text =
                                                        SimpleDateFormat(
                                                                "HH:mm:ss",
                                                                Locale.getDefault(),
                                                            )
                                                            .format(it.time),
                                                    style = Typography.headlineMedium,
                                                )

                                                Spacer(modifier = Modifier.weight(1f))

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Row(
                                                        verticalAlignment =
                                                            Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = it.totalTransactions.toString(),
                                                            style = Typography.titleMedium,
                                                        )

                                                        Spacer(Modifier.width(5.dp))

                                                        Icon(
                                                            imageVector =
                                                                Icons.Outlined.ShoppingBasket,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp),
                                                            tint = MaterialTheme.colorScheme.primary,
                                                        )
                                                    }

                                                    Row(
                                                        verticalAlignment =
                                                            Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text =
                                                                TransactionEntity.actualTotalCost(
                                                                        it.totalSpending
                                                                    )
                                                                    .formatToCurrency(
                                                                        currencyLocale
                                                                    ),
                                                            style = Typography.titleMedium,
                                                        )

                                                        Spacer(Modifier.width(5.dp))

                                                        Icon(
                                                            imageVector = Icons.Outlined.Payment,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp),
                                                            tint = MaterialTheme.colorScheme.primary,
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(10.dp))
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onEvent(BackupsEvent.ToggleBackupLock(it)) }
                                    ) {
                                        Crossfade(
                                            targetState = it.locked,
                                            label = "lock status change animation",
                                        ) { locked ->
                                            if (locked) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription =
                                                        stringResource(id = R.string.backup_unlock),
                                                    tint = MaterialTheme.colorScheme.primary,
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.LockOpen,
                                                    contentDescription =
                                                        stringResource(id = R.string.backup_lock),
                                                    tint =
                                                        MaterialTheme.colorScheme.primary.copy(
                                                            disabledAlpha
                                                        ),
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                            }
                        }
                    }

                    item {
                        Box(Modifier.height(FloatingActionButtonDefaults.LargeIconSize + 32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BackupsScreenNothingToDisplayOverlay(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.no_data_to_display_text),
                style = Typography.titleLarge,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.no_data_to_display_add_backup_hint),
                style = Typography.titleLarge,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        val pathColor = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val curveEndX = size.width - 90.dp.toPx()
            val curveEndY = size.height - 45.dp.toPx()
            val lineWidth = 3.dp.toPx()

            drawPath(
                path =
                    Path().apply {
                        moveTo(size.width / 2, 24.dp.toPx())
                        quadraticTo(size.width * 1 / 4, size.height / 2, curveEndX, curveEndY)
                    },
                style = Stroke(width = lineWidth),
                color = pathColor,
            )
        }
    }
}

@PreviewLightDark
@ExpandedPreviews
@Composable
private fun BackupsScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            BackupsScreen(uiState = BackupsUiState(), onEvent = {})
        }
    }
}
