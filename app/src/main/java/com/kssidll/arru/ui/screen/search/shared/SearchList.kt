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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.field.*
import com.kssidll.arru.ui.screen.search.component.*
import com.kssidll.arru.ui.theme.*

/**
 * Fuzzy searchable list component
 * @param filter Filter by which to sort the [items]
 * @param onFilterChange Callback triggered when the input service updates the filter text. Provides new filter as parameter
 * @param items List of items to display
 * @param onItemClick Callback triggered when an item is clicked. Provides item as parameter
 * @param onItemLongClick Callback triggered when an item is long pressed/clicked. Provides item as parameter
 */
@Composable
fun <T> SearchList(
    filter: String,
    onFilterChange: (String) -> Unit,
    items: Data<List<T>>,
    onItemClick: (item: T) -> Unit,
    onItemLongClick: (item: T) -> Unit,
) where T: FuzzySearchSource, T: NameSource {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = items.loadedEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.no_data_to_display_text),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                )
            }
        }

        AnimatedVisibility(
            visible = items.loadedData(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val displayItems: SnapshotStateList<T> = remember { mutableStateListOf() }

            LaunchedEffect(
                items,
                filter
            ) {
                if (items is Data.Loaded) {
                    val newItems = items.data.fuzzySearchSort(filter)

                    displayItems.clear()
                    displayItems.addAll(newItems)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = items.loadedData(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Scaffold(
                        bottomBar = {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .width(600.dp)
                                        .align(Alignment.Center)
                                ) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)

                                    StyledOutlinedTextField(
                                        value = filter,
                                        onValueChange = {
                                            onFilterChange(it)
                                        },
                                        placeholder = {
                                            Text(
                                                text = stringResource(id = R.string.search),
                                                style = Typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.primary.copy(optionalAlpha),
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Search,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        colors = styledTextFieldColorDefaults(
                                            disabledIndicator = Color.Transparent,
                                            unfocusedIndicator = Color.Transparent,
                                            focusedIndicator = Color.Transparent,
                                            errorIndicator = Color.Transparent,
                                        ),
                                    )
                                }
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                                .consumeWindowInsets(it)
                        ) {
                            LazyColumn(
                                reverseLayout = true,
                                modifier = Modifier.weight(1f),
                            ) {
                                items(displayItems.toList()) { item ->
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier
                                                .width(600.dp)
                                                .align(Alignment.Center)
                                        ) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)

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
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ListScreenPreview() {
    ArrugarqTheme {
        Surface {
            SearchList(
                filter = String(),
                onFilterChange = {},
                items = Data.Loaded(ProductWithAltNames.generateList()),
                onItemClick = {},
                onItemLongClick = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyListScreenPreview() {
    ArrugarqTheme {
        Surface {
            SearchList(
                filter = String(),
                onFilterChange = {},
                items = Data.Loaded(emptyList()),
                onItemClick = {},
                onItemLongClick = {},
            )
        }
    }
}
