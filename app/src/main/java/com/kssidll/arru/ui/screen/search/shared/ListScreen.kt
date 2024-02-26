package com.kssidll.arru.ui.screen.search.shared


import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.field.*
import com.kssidll.arru.ui.screen.search.component.*
import com.kssidll.arru.ui.theme.*
import kotlinx.coroutines.flow.*

/**
 * Generic list screen
 * @param T Type of item, needs to implement [FuzzySearchSource] and [NameSource]
 * @param state [ListScreenState] representing the screen state
 * @param onItemClick Callback called when the item is clicked. Provides [T] as parameter
 * @param onItemLongClick Callback called when the item is long clicked/pressed. Provides [T] as parameter
 */
@Composable
internal fun <T> ListScreen(
    state: ListScreenState<T>,
    onItemClick: (item: T) -> Unit,
    onItemLongClick: (item: T) -> Unit,
) where T: FuzzySearchSource, T: NameSource {
    val items = state.items.value.collectAsState(initial = Data.Loading()).value
    val displayItems: SnapshotStateList<T> = remember { mutableStateListOf() }

    LaunchedEffect(
        items,
        state.filter.value
    ) {
        if (items is Data.Loaded) {
            displayItems.clear()
            displayItems.addAll(items.data.fuzzySearchSort(state.filter.value))
        }
    }

    // TODO add info when empty
    // TODO this animation is kinda weird, fix it (the product search screen mostly)
    AnimatedVisibility(
        visible = items.loadedData(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .fillMaxWidth()
                ) {
                    SearchItemHorizontalDivider()

                    StyledOutlinedTextField(
                        value = state.filter.value,
                        onValueChange = {
                            state.filter.value = it
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.search),
                                style = Typography.bodyLarge,
                                color = MaterialTheme.colorScheme.tertiary.copy(optionalAlpha),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        },
                        colors = styledTextFieldColorDefaults(
                            disabledIndicator = Color.Transparent,
                            unfocusedIndicator = Color.Transparent,
                            focusedIndicator = Color.Transparent,
                            errorIndicator = Color.Transparent,
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LazyColumn(
                    reverseLayout = true,
                    modifier = Modifier.weight(1f),
                ) {
                    itemsIndexed(displayItems.toList()) { index, item ->
                        if (index != 0) {
                            SearchItemHorizontalDivider()
                        }

                        SearchItem(
                            text = item.name(),
                            onItemClick = {
                                onItemClick(item)
                            },
                            onItemLongClick = {
                                onItemLongClick(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

// TODO remove this state class
/**
 * Data representing [ListScreen] state
 */
internal data class ListScreenState<T>(
    val filter: MutableState<String> = mutableStateOf(String()),
    val items: MutableState<Flow<Data<List<T>>>> = mutableStateOf(flowOf()),
) where T: FuzzySearchSource, T: NameSource

@PreviewLightDark
@Composable
private fun ListScreenPreview() {
    ArrugarqTheme {
        Surface {
            ListScreen(
                state = ListScreenState<ProductWithAltNames>(),
                onItemClick = {},
                onItemLongClick = {},
            )
        }
    }
}
