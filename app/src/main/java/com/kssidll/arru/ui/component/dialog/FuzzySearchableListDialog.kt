package com.kssidll.arru.ui.component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.domain.data.interfaces.searchSort
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.component.field.styledTextFieldColorDefaults
import com.kssidll.arru.ui.component.list.BaseClickableListItem
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @param T: Type of the item, needs to implement FuzzySearchSource
 * @param onDismissRequest: Function called when the user tries to dismiss the Dialog by clicking outside. This is also called when the Add/'+' button is clicked
 * @param items: Items to be displayed in the list
 * @param itemText: Transformation used to determine what to display on the item card
 * @param onItemClick: Function called when an item is clicked
 * @param showAddButton: Whether to show an Add/'+' button in the search field
 * @param onAddButtonClick: Function called when the Add/'+' button is clicked. Provides searched for value at the time of the event as parameter
 * @param addButtonDescription: Description for the Add/'+' button icon
 * @param showDefaultValueItem: Whether to show a default, null value item under the search field
 * @param defaultItemText: String to display on the default, null value item
 * @param shape: Shape of the Dialog
 * @param calculateScore function to use to calculate the score of an item which determines the order of the items
 */
@Composable
fun <T> SearchableListDialog(
    onDismissRequest: () -> Unit,
    items: ImmutableList<T>,
    itemText: (T) -> String,
    calculateScore: (item: T, query: String) -> Int,
    onItemClick: ((T?) -> Unit)? = null,
    onItemClickLabel: String? = null,
    onItemLongClick: ((T) -> Unit)? = null,
    onItemLongClickLabel: String? = null,
    showAddButton: Boolean = true,
    onAddButtonClick: ((query: String) -> Unit)? = null,
    addButtonDescription: String? = null,
    showDefaultValueItem: Boolean = false,
    defaultItemText: String = String(),
    shape: Shape = ShapeDefaults.ExtraLarge,
    modifier: Modifier = Modifier
) {
    var query: String by remember {
        mutableStateOf(String())
    }

    var displayedItems: List<T> by remember {
        mutableStateOf(listOf())
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .width(400.dp)
                .height(600.dp),
            shape = shape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            AnimatedVisibility(
                visible = items.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data_to_display_text),
                        textAlign = TextAlign.Center,
                        style = Typography.titleMedium,
                    )
                }
            }

            AnimatedVisibility(
                visible = items.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                LaunchedEffect(
                    items,
                    query
                ) {
                    displayedItems = if (query.isNotBlank()) items.searchSort {
                        calculateScore(
                            it,
                            query
                        )
                    } else items
                }

                Column {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        reverseLayout = true,
                    ) {
                        items(items = displayedItems) {
                            BaseClickableListItem(
                                text = itemText(it),
                                onClick = {
                                    onItemClick?.invoke(it)
                                },
                                onClickLabel = onItemClickLabel,
                                onLongClick = {
                                    onItemLongClick?.invoke(it)
                                },
                                onLongClickLabel = onItemLongClickLabel,
                            )
                            HorizontalDivider()
                        }
                    }

                    if (showDefaultValueItem) {
                        HorizontalDivider()
                        BaseClickableListItem(
                            onClick = {
                                onItemClick?.invoke(null)
                            },
                            onClickLabel = onItemClickLabel,
                            text = defaultItemText
                        )
                    }

                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        StyledOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            singleLine = true,
                            value = query,
                            onValueChange = {
                                query = it
                            },
                            textStyle = TextStyle.Default.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            ),
                            label = {
                                Text(
                                    text = stringResource(R.string.search),
                                    fontSize = 16.sp,
                                )
                            },
                            colors = styledTextFieldColorDefaults(
                                focusedIndicator = Color.Transparent,
                                unfocusedIndicator = Color.Transparent,
                            ),
                            trailingIcon = {
                                if (showAddButton) {
                                    IconButton(
                                        onClick = {
                                            onDismissRequest()
                                            onAddButtonClick?.invoke(query)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = addButtonDescription,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FuzzySearchableListDialogPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SearchableListDialog(
                onDismissRequest = {},
                items = ProductEntity.generateList().toImmutableList(),
                itemText = { "test" },
                calculateScore = { _, _ -> 0 }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyFuzzySearchableListDialogPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SearchableListDialog(
                onDismissRequest = {},
                items = emptyList<ProductEntity>().toImmutableList(),
                itemText = { "test" },
                calculateScore = { _, _ -> 0 }
            )
        }
    }
}