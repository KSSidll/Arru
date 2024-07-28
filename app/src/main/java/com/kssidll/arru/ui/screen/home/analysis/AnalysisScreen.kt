package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.home.analysis.components.DateHeader
import com.kssidll.arru.ui.theme.ArrugarqTheme

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

/**
 * @param year Year for which the main data is fetched
 * @param month Month for which the main data is fetched, in range of 1 - 12
 * @param onMonthIncrement Callback called to request [month] increment, should handle overflow and increase year
 * @param onMonthDecrement Callback called to request [month] decrement, should handle underflow and decrease year
 */
@Composable
internal fun AnalysisScreen(
    isExpandedScreen: Boolean,
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
) {
    if (isExpandedScreen) {
        ExpandedAnalysisScreenContent(
            year = year,
            month = month,
            onMonthIncrement = onMonthIncrement,
            onMonthDecrement = onMonthDecrement,
        )
    } else {
        AnalysisScreenContent(
            year = year,
            month = month,
            onMonthIncrement = onMonthIncrement,
            onMonthDecrement = onMonthDecrement,
        )
    }
}

@Composable
private fun AnalysisScreenContent(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                AnimatedVisibility(visible = true) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_data_to_display_text),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                DateHeader(
                    year = year,
                    month = month,
                    onMonthIncrement = onMonthIncrement,
                    onMonthDecrement = onMonthDecrement,
                )
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = false) {
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = false) {
            }
        }
    }
}

@Composable
private fun ExpandedAnalysisScreenContent(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 4.dp)) {
                DateHeader(
                    year = year,
                    month = month,
                    onMonthIncrement = onMonthIncrement,
                    onMonthDecrement = onMonthDecrement,
                )
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(visible = true) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data_to_display_text),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Row {
                AnimatedVisibility(
                    visible = false,
                    modifier = Modifier.weight(1f)
                ) {
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = false,
                    modifier = Modifier.weight(1f)
                ) {
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AnalysisScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(
                isExpandedScreen = false,
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedAnalysisScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(
                isExpandedScreen = true,
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
            )
        }
    }
}
