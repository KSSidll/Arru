package com.kssidll.arrugarq.ui.screen.addproduct

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
import com.kssidll.arrugarq.ui.component.dialog.*
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.flow.*
import java.util.*

fun isNameError(value: String?) = value.isNullOrBlank()
fun isCategoryError(value: ProductCategory?) = value == null

@OptIn(ExperimentalMaterial3Api::class)
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
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(text = stringResource(R.string.item_product))
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AddProductScreenContent(
                onBack = onBack,
                onCategoryAdd = onCategoryAdd,
                onProducerAdd = onProducerAdd,
                onProductAdd = onProductAdd,
                categoriesWithAltNames = categoriesWithAltNames,
                producers = producers,
                state = state,
            )
        }
    }
}

@Composable
private fun AddProductScreenContent(
    onBack: () -> Unit,
    onCategoryAdd: () -> Unit,
    onProducerAdd: () -> Unit,
    onProductAdd: (AddProductData) -> Unit,
    categoriesWithAltNames: Flow<List<ProductCategoryWithAltNames>>,
    producers: Flow<List<ProductProducer>>,
    state: AddProductState,
) {
    var isCategorySearchDialogExpanded: Boolean by remember {
        mutableStateOf(false)
    }

    var isProducerSearchDialogExpanded: Boolean by remember {
        mutableStateOf(false)
    }

    var attemptedToSubmit: Boolean by remember {
        mutableStateOf(false)
    }

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
                                isError = if (attemptedToSubmit) isNameError(state.name.value) else false
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
                        onClick = {
                            isProducerSearchDialogExpanded = true
                        },
                        label = stringResource(R.string.item_product_producer),
                        onAddButtonClick = {
                            onProducerAdd()
                        },
                        addButtonDescription = stringResource(R.string.add_product_producer_description),
                        optional = true,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SearchField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        value = state.selectedProductCategory.value?.name ?: String(),
                        onClick = {
                            isCategorySearchDialogExpanded = true
                        },
                        label = stringResource(R.string.item_product_category),
                        onAddButtonClick = {
                            onCategoryAdd()
                        },
                        addButtonDescription = stringResource(R.string.add_product_category_description),
                        error = if (attemptedToSubmit) isCategoryError(state.selectedProductCategory.value) else false,
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
                            attemptedToSubmit = true

                            val category: ProductCategory? = state.selectedProductCategory.value
                            val producer: ProductProducer? = state.selectedProductProducer.value
                            val name: String = state.name.value

                            if (
                                !isCategoryError(category) &&
                                !isNameError(name)
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

@Preview(
    group = "Add Product Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Add Product Screen",
    name = "Light",
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
