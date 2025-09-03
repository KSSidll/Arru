package com.kssidll.arru.ui.screen.home

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.helper.BetterNavigationSuiteScaffoldDefaults
import com.kssidll.arru.ui.screen.home.analysis.AnalysisEvent
import com.kssidll.arru.ui.screen.home.analysis.AnalysisScreen
import com.kssidll.arru.ui.screen.home.analysis.AnalysisUiState
import com.kssidll.arru.ui.screen.home.dashboard.DashboardEvent
import com.kssidll.arru.ui.screen.home.dashboard.DashboardScreen
import com.kssidll.arru.ui.screen.home.dashboard.DashboardUiState
import com.kssidll.arru.ui.screen.home.transactions.TransactionsEvent
import com.kssidll.arru.ui.screen.home.transactions.TransactionsScreen
import com.kssidll.arru.ui.screen.home.transactions.TransactionsUiState
import com.kssidll.arru.ui.theme.Typography

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    dashboardUiState: DashboardUiState,
    analysisUiState: AnalysisUiState,
    transactionsUiState: TransactionsUiState,
    onEvent: (event: HomeEvent) -> Unit,
    dashboardOnEvent: (event: DashboardEvent) -> Unit,
    analysisOnEvent: (event: AnalysisEvent) -> Unit,
    transactionsOnEvent: (event: TransactionsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navSuiteType =
        BetterNavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    NavigationSuiteScaffoldLayout(
        layoutType = navSuiteType,
        navigationSuite = {
            when (navSuiteType) {
                NavigationSuiteType.NavigationBar -> {
                    NavigationBar {
                        HomeDestinations.entries.forEach { it ->
                            NavigationBarItem(
                                icon = {
                                    Crossfade(
                                        targetState = it == uiState.currentDestination,
                                        label = "home nav destination change (icon)",
                                    ) { selected ->
                                        Icon(
                                            imageVector =
                                                if (selected) it.enabledIcon else it.disabledIcon,
                                            contentDescription =
                                                stringResource(it.contentDescription),
                                        )
                                    }
                                },
                                label = {
                                    @SuppressLint("UnusedCrossfadeTargetStateParameter")
                                    Crossfade(
                                        targetState = it == uiState.currentDestination,
                                        label = "home nav destination change (label)",
                                    ) { selected ->
                                        Text(
                                            text = stringResource(it.label),
                                            style = Typography.labelMedium,
                                        )
                                    }
                                },
                                selected = it == uiState.currentDestination,
                                onClick = { onEvent(HomeEvent.ChangeScreenDestination(it)) },
                            )
                        }

                        FloatingActionButton(
                            onClick = { onEvent(HomeEvent.NavigateAddTransaction) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.transaction_add),
                                modifier = Modifier.size(36.dp),
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                NavigationSuiteType.NavigationDrawer -> {
                    PermanentNavigationDrawer(
                        drawerContent = {
                            PermanentDrawerSheet {
                                Column(Modifier.verticalScroll(rememberScrollState())) {
                                    Spacer(Modifier.height(8.dp))

                                    ExtendedFloatingActionButton(
                                        onClick = { onEvent(HomeEvent.NavigateAddTransaction) },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription =
                                                stringResource(R.string.transaction_add_label),
                                        )

                                        Spacer(Modifier.width(8.dp))

                                        Text(
                                            text = stringResource(R.string.transaction_add_label),
                                            style = Typography.labelLarge,
                                        )
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    HomeDestinations.entries.forEach {
                                        NavigationDrawerItem(
                                            icon = {
                                                Crossfade(
                                                    targetState = it == uiState.currentDestination,
                                                    label = "home nav destination change (icon)",
                                                ) { selected ->
                                                    Icon(
                                                        imageVector =
                                                            if (selected) it.enabledIcon
                                                            else it.disabledIcon,
                                                        contentDescription =
                                                            stringResource(it.contentDescription),
                                                    )
                                                }
                                            },
                                            label = {
                                                Text(
                                                    text = stringResource(it.label),
                                                    style = Typography.labelLarge,
                                                )
                                            },
                                            selected = it == uiState.currentDestination,
                                            onClick = {
                                                onEvent(HomeEvent.ChangeScreenDestination(it))
                                            },
                                            modifier = Modifier,
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        HomeScreenContent(
                            uiState = uiState,
                            dashboardUiState = dashboardUiState,
                            analysisUiState = analysisUiState,
                            transactionsUiState = transactionsUiState,
                            dashboardOnEvent = dashboardOnEvent,
                            analysisOnEvent = analysisOnEvent,
                            transactionsOnEvent = transactionsOnEvent,
                            modifier = modifier,
                        )
                    }
                }

                NavigationSuiteType.NavigationRail -> {
                    NavigationRail(
                        header = {
                            FloatingActionButton(
                                onClick = { onEvent(HomeEvent.NavigateAddTransaction) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.transaction_add),
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                        }
                    ) {
                        HomeDestinations.entries.forEach {
                            NavigationRailItem(
                                icon = {
                                    Crossfade(
                                        targetState = it == uiState.currentDestination,
                                        label = "home nav destination change (icon)",
                                    ) { selected ->
                                        Icon(
                                            imageVector =
                                                if (selected) it.enabledIcon else it.disabledIcon,
                                            contentDescription =
                                                stringResource(it.contentDescription),
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = stringResource(it.label),
                                        style = Typography.labelMedium,
                                    )
                                },
                                selected = it == uiState.currentDestination,
                                onClick = { onEvent(HomeEvent.ChangeScreenDestination(it)) },
                            )
                        }
                    }
                }
            }
        },
    ) {
        HomeScreenContent(
            uiState = uiState,
            dashboardUiState = dashboardUiState,
            analysisUiState = analysisUiState,
            transactionsUiState = transactionsUiState,
            dashboardOnEvent = dashboardOnEvent,
            analysisOnEvent = analysisOnEvent,
            transactionsOnEvent = transactionsOnEvent,
            modifier = modifier,
        )
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    dashboardUiState: DashboardUiState,
    analysisUiState: AnalysisUiState,
    transactionsUiState: TransactionsUiState,
    dashboardOnEvent: (event: DashboardEvent) -> Unit,
    analysisOnEvent: (event: AnalysisEvent) -> Unit,
    transactionsOnEvent: (event: TransactionsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = uiState.currentDestination,
        label = "home nav destination change (screen)",
        modifier = modifier,
    ) {
        when (it) {
            HomeDestinations.DASHBOARD -> {
                DashboardScreen(uiState = dashboardUiState, onEvent = dashboardOnEvent)
            }

            HomeDestinations.ANALYSIS -> {
                AnalysisScreen(uiState = analysisUiState, onEvent = analysisOnEvent)
            }

            HomeDestinations.TRANSACTIONS -> {
                TransactionsScreen(uiState = transactionsUiState, onEvent = transactionsOnEvent)
            }
        }
    }
}
