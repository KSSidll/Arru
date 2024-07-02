package com.kssidll.arru.ui.screen.search.shared


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ProductWithAltNames
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.component.field.styledTextFieldColorDefaults
import com.kssidll.arru.ui.screen.search.component.SearchItem
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.kssidll.arru.ui.theme.optionalAlpha

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
                                    HorizontalDivider(color = MaterialTheme.colorScheme.tertiaryContainer)

                                    StyledOutlinedTextField(
                                        value = filter,
                                        onValueChange = {
                                            onFilterChange(it)
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
                                            HorizontalDivider(color = MaterialTheme.colorScheme.tertiaryContainer)

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
