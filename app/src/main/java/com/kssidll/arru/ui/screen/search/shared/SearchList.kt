package com.kssidll.arru.ui.screen.search.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.domain.data.interfaces.NameSource
import com.kssidll.arru.domain.data.interfaces.searchSort
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.component.field.styledTextFieldColorDefaults
import com.kssidll.arru.ui.screen.search.component.SearchItem
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.kssidll.arru.ui.theme.optionalAlpha
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Fuzzy searchable list component
 *
 * @param filter Filter by which to sort the [items]
 * @param onFilterChange Callback triggered when the input service updates the filter text. Provides
 *   new filter as parameter
 * @param items List of items to display
 * @param onItemClick Callback triggered when an item is clicked. Provides item as parameter
 * @param onItemLongClick Callback triggered when an item is long pressed/clicked. Provides item as
 *   parameter
 */
@Composable
fun <T> SearchList(
    filter: String,
    onFilterChange: (String) -> Unit,
    items: ImmutableList<T>,
    onItemClick: (item: T) -> Unit,
    onItemLongClick: (item: T) -> Unit,
    modifier: Modifier = Modifier,
) where T : FuzzySearchSource, T : NameSource {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = items.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.no_data_to_display_text),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                )
            }
        }

        AnimatedVisibility(visible = items.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
            val displayItems: SnapshotStateList<T> = remember { mutableStateListOf() }

            LaunchedEffect(items, filter) {
                val newItems = items.searchSort(filter)

                displayItems.clear()
                displayItems.addAll(newItems)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    bottomBar = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.width(600.dp).align(Alignment.Center)) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )

                                StyledOutlinedTextField(
                                    value = filter,
                                    onValueChange = { onFilterChange(it) },
                                    placeholder = {
                                        Text(
                                            text = stringResource(id = R.string.search),
                                            style = Typography.bodyLarge,
                                            color =
                                                MaterialTheme.colorScheme.primary.copy(
                                                    optionalAlpha
                                                ),
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Search,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    },
                                    colors =
                                        styledTextFieldColorDefaults(
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
                    Column(modifier = Modifier.fillMaxSize().padding(it).consumeWindowInsets(it)) {
                        LazyColumn(reverseLayout = true, modifier = Modifier.weight(1f)) {
                            items(displayItems.toList()) { item ->
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.width(600.dp).align(Alignment.Center)
                                    ) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        )

                                        SearchItem(
                                            text = item.name(),
                                            onItemClick = { onItemClick(item) },
                                            onItemLongClick = { onItemLongClick(item) },
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

@PreviewLightDark
@Composable
private fun ListScreenPreview() {
    ArrugarqTheme {
        Surface {
            SearchList(
                filter = String(),
                onFilterChange = {},
                items = ProductEntity.generateList().toImmutableList(),
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
                items = emptyImmutableList(),
                onItemClick = {},
                onItemLongClick = {},
            )
        }
    }
}
