package com.kssidll.arrugarq.ui.component.dialog

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
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.component.button.*
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.theme.*

/**
 * @param T: Type of the item, needs to implement IFuzzySearchable
 * @param onDismissRequest: Function called when the user tries to dismiss the Dialog by clicking outside. This is also called when the Add/'+' button is clicked
 * @param items: Items to be displayed in the list
 * @param itemText: Transfarmation used to determine what to display on the item card
 * @param onItemClick: Function called when an item is clicked
 * @param showAddButton: Whether to show an Add/'+' button in the search field
 * @param onAddButtonClick: Function called when the Add/'+' button is clicked
 * @param addButtonDescription: Description for the Add/'+' button icon
 * @param showDefaultValueItem: Whether to show a default, null value item under the search field
 * @param defaultItemText: String to display on the default, null value item
 * @param shape: Shape of the Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FuzzySearchableListDialog(
    onDismissRequest: () -> Unit,
    items: List<T>,
    itemText: (T) -> String,
    onItemClick: ((T?) -> Unit)? = null,
    showAddButton: Boolean = true,
    onAddButtonClick: (() -> Unit)? = null,
    addButtonDescription: String? = null,
    showDefaultValueItem: Boolean = false,
    defaultItemText: String = String(),
    shape: Shape = ShapeDefaults.ExtraLarge,
) where T: IFuzzySearchable {
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

    AlertDialog(onDismissRequest = onDismissRequest) {
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
                        ClickableListItem(
                            onClick = {
                                onItemClick?.invoke(it)
                            },
                            text = itemText(it)
                        )
                        HorizontalDivider()
                    }
                }


                if (showDefaultValueItem) {
                    HorizontalDivider()
                    ClickableListItem(
                        onClick = {
                            onItemClick?.invoke(null)
                        },
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
                                text = stringResource(com.kssidll.arrugarq.R.string.search),
                                fontSize = 16.sp,
                            )
                        },
                        colors = styledTextFieldColorDefaults(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        trailingIcon = {
                            if (showAddButton) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            onDismissRequest()
                                            onAddButtonClick?.invoke()
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
