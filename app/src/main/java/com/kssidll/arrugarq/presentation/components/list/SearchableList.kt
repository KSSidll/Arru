package com.kssidll.arrugarq.presentation.components.list

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.theme.*

/**
 * @param T: Type of the item, needs to implement IFuzzySearchable
 * @param items: Items to be displayed in the list
 * @param onItemClick: Function called when an item is clicked
 * @param itemText: Transfarmation used to determine what to display on the item card
 * @param showAddButton: Whether to show an Add/'+' button in the search field
 * @param onAddButtonClick: Function called when the Add/'+' button is clicked
 * @param addButtonDescription: Description for the Add/'+' button icon
 * @param showDefaultValueItem: Whether to show a default, null value item under the search field
 * @param defaultItemText: String to display on the default, null value item
 * @param spaceUnderSearchField: How much height offset there should be under the search field. BEWARE, default, null value item is drawn in that space
 */
@Composable
fun <T> FuzzySearchableList(
    items: List<T>,
    itemText: (T) -> String,
    onItemClick: ((T?) -> Unit)? = null,
    showAddButton: Boolean = true,
    onAddButtonClick: (() -> Unit)? = null,
    addButtonDescription: String? = null,
    showDefaultValueItem: Boolean = false,
    defaultItemText: String = String(),
    spaceUnderSearchField: Dp = 100.dp,
) where T: IFuzzySearchable {
    var query: String by rememberSaveable {
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

    Column {

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(items = displayedItems) {
                ClickableListItem(
                    onClick = {
                        onItemClick?.invoke(it)
                    },
                    text = itemText(it)
                )
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                value = query,
                onValueChange = {
                    query = it
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                textStyle = TextStyle.Default.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                suffix = {
                    Text(
                        text = stringResource(com.kssidll.arrugarq.R.string.search),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .alpha(0.5F)
                            .padding(end = 6.dp)
                    )
                },
                trailingIcon = {
                    if (showAddButton) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1F)
                                .clickable {
                                    onAddButtonClick?.invoke()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = lineColor,
                                    start = Offset(
                                        0F,
                                        0F
                                    ),
                                    end = Offset(
                                        0F,
                                        size.height
                                    ),
                                    strokeWidth = Dp.Hairline.value
                                )
                            }
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
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Column(
            modifier = Modifier.height(spaceUnderSearchField)
        ) {
            if (showDefaultValueItem) {
                ClickableListItem(
                    onClick = {
                        onItemClick?.invoke(null)
                    },
                    text = defaultItemText
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Preview(
    group = "SearchableList",
    name = "Searchable List Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchableList",
    name = "Searchable List Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun SearchableListPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FuzzySearchableList(
                items = listOf<ProductWithAltNames>(),
                itemText = { "test" },
            )
        }
    }
}
