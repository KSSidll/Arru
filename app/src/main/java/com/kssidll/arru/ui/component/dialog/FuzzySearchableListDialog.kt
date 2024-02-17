package com.kssidll.arru.ui.component.dialog

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.field.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.theme.*

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
 */
@Composable
fun <T> FuzzySearchableListDialog(
    onDismissRequest: () -> Unit,
    items: List<T>,
    itemText: (T) -> String,
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
) where T: FuzzySearchSource {
    var query: String by remember {
        mutableStateOf(String())
    }

    var displayedItems: List<T> by remember {
        mutableStateOf(listOf())
    }

    LaunchedEffect(
        items,
        query
    ) {
        displayedItems = items.fuzzySearchSort(query)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .requiredWidth(360.dp)
                .heightIn(max = 568.dp),
            shape = shape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {

            Column {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
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
                                text = stringResource(com.kssidll.arru.R.string.search),
                                fontSize = 16.sp,
                            )
                        },
                        colors = styledTextFieldColorDefaults(
                            focusedIndicator = Color.Transparent,
                            unfocusedIndicator = Color.Transparent,
                        ),
                        trailingIcon = {
                            if (showAddButton) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            onDismissRequest()
                                            onAddButtonClick?.invoke(query)
                                        },
                                    contentAlignment = Alignment.Center
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

@Preview(
    group = "Fuzzy Searchable List Dialog",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Fuzzy Searchable List Dialog",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun FuzzySearchableListDialogPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FuzzySearchableListDialog(
                onDismissRequest = {},
                items = listOf<ProductWithAltNames>(),
                itemText = { "test" },
            )
        }
    }
}
