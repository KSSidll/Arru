package com.kssidll.arrugarq.presentation.screen.addproduct

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.component.field.*
import com.kssidll.arrugarq.presentation.component.dialog.*
import com.kssidll.arrugarq.presentation.component.other.*
import com.kssidll.arrugarq.presentation.theme.*
import kotlinx.coroutines.flow.*
import java.util.*

@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    onCategoryAdd: () -> Unit,
    onProducerAdd: () -> Unit,
    onProductAdd: (AddProductData) -> Unit,
    categoriesWithAltNames: Flow<List<ProductCategoryWithAltNames>>,
    producers: Flow<List<ProductProducer>>,
    state: AddProductState,
) {

    Column {
        var isCategorySearchDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }

        var isProducerSearchDialogExpanded: Boolean by remember {
            mutableStateOf(false)
        }

        var categoryError: Boolean by remember {
            mutableStateOf(false)
        }

        var nameError: Boolean by remember {
            mutableStateOf(false)
        }

        SecondaryAppBar(onBack = onBack) {
            Text(text = stringResource(R.string.item_product))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            if (isProducerSearchDialogExpanded) {
                FuzzySearchableListDialog(
                    onDismissRequest = {
                        isProducerSearchDialogExpanded = false
                    },
                    items = producers.collectAsState(emptyList()).value,
                    itemText = { it.name },
                    onItemClick = {
                        state.selectedProductProducer.value = it
                        isProducerSearchDialogExpanded = false
                    },
                    onAddButtonClick = onProducerAdd,
                    addButtonDescription = stringResource(R.string.add_product_producer_description),
                    showDefaultValueItem = true,
                    defaultItemText = stringResource(R.string.no_value),
                )
            } else if (isCategorySearchDialogExpanded) {
                FuzzySearchableListDialog(
                    onDismissRequest = {
                        isCategorySearchDialogExpanded = false
                    },
                    items = categoriesWithAltNames.collectAsState(emptyList()).value,
                    onItemClick = {
                        state.selectedProductCategory.value = it?.productCategory
                        isCategorySearchDialogExpanded = false
                    },
                    itemText = { it.productCategory.name },
                    onAddButtonClick = onCategoryAdd,
                    addButtonDescription = stringResource(R.string.add_product_category_description),
                )
            } else {
                Column {
                    Column(
                        modifier = Modifier.fillMaxHeight(0.6f),
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StyledOutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(0.85f),
                                    singleLine = true,
                                    value = state.name.value,
                                    onValueChange = {
                                        state.name.value = it
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(R.string.item_product)
                                        )
                                    },
                                    isError = nameError
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        SearchField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            value = state.selectedProductProducer.value?.name ?: String(),
                            onFocus = {
                                isProducerSearchDialogExpanded = true
                            },
                            label = stringResource(R.string.item_product_producer),
                            onAddButtonClick = {
                                onProducerAdd()
                            },
                            addButtonDescription = stringResource(R.string.add_product_producer_description),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SearchField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            value = state.selectedProductCategory.value?.name ?: String(),
                            onFocus = {
                                isCategorySearchDialogExpanded = true
                            },
                            label = stringResource(R.string.item_product_category),
                            onAddButtonClick = {
                                onCategoryAdd()
                            },
                            addButtonDescription = stringResource(R.string.add_product_category_description),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxHeight(0.4f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            onClick = {
                                val category: ProductCategory? = state.selectedProductCategory.value
                                val producer: ProductProducer? = state.selectedProductProducer.value
                                val name: String = state.name.value

                                categoryError = category == null
                                nameError = name.isEmpty()

                                if (
                                    !categoryError &&
                                    !nameError
                                ) {
                                    onProductAdd(
                                        AddProductData(
                                            categoryId = category!!.id,
                                            producerId = Optional.ofNullable(producer?.id),
                                            name = name,
                                        )
                                    )
                                    onBack()
                                }
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.add_product_description),
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.item_product_add),
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    group = "AddProductScreen",
    name = "Add Product Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddProductScreen",
    name = "Add Product Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddProductScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddProductScreen(
                onBack = {},
                onCategoryAdd = {},
                onProducerAdd = {},
                onProductAdd = {},
                categoriesWithAltNames = flowOf(),
                producers = flowOf(),
                state = AddProductState(),
            )
        }
    }
}
