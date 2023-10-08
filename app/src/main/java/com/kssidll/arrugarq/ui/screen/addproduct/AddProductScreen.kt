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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddProductScreen(
    onBack: () -> Unit,
    state: AddProductScreenState,
    onCategoryAdd: () -> Unit,
    onProducerAdd: () -> Unit,
    onProductAdd: () -> Unit,
    categoriesWithAltNames: Flow<List<ProductCategoryWithAltNames>>,
    producers: Flow<List<ProductProducer>>,
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
                state = state,
                onCategoryAdd = onCategoryAdd,
                onProducerAdd = onProducerAdd,
                onProductAdd = onProductAdd,
                categoriesWithAltNames = categoriesWithAltNames,
                producers = producers,
            )
        }
    }
}

@Composable
private fun AddProductScreenContent(
    state: AddProductScreenState,
    onCategoryAdd: () -> Unit,
    onProducerAdd: () -> Unit,
    onProductAdd: () -> Unit,
    categoriesWithAltNames: Flow<List<ProductCategoryWithAltNames>>,
    producers: Flow<List<ProductProducer>>,
) {
    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        if (state.isProducerSearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isProducerSearchDialogExpanded.value = false
                },
                items = producers.collectAsState(emptyList()).value,
                itemText = { it.name },
                onItemClick = {
                    state.selectedProductProducer.value = it
                    state.isProducerSearchDialogExpanded.value = false
                },
                onAddButtonClick = onProducerAdd,
                addButtonDescription = stringResource(R.string.add_product_producer_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
            )
        } else if (state.isCategorySearchDialogExpanded.value) {
            FuzzySearchableListDialog(
                onDismissRequest = {
                    state.isCategorySearchDialogExpanded.value = false
                },
                items = categoriesWithAltNames.collectAsState(emptyList()).value,
                onItemClick = {
                    state.selectedProductCategory.value = it?.productCategory
                    state.validateSelectedProductCategory()
                    state.isCategorySearchDialogExpanded.value = false
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
                                    state.validateName()
                                },
                                label = {
                                    Text(
                                        text = stringResource(R.string.item_product)
                                    )
                                },
                                isError = if (state.attemptedToSubmit.value) state.nameError.value else false
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
                            state.isProducerSearchDialogExpanded.value = true
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
                            state.isCategorySearchDialogExpanded.value = true
                        },
                        label = stringResource(R.string.item_product_category),
                        onAddButtonClick = {
                            onCategoryAdd()
                        },
                        addButtonDescription = stringResource(R.string.add_product_category_description),
                        error = if (state.attemptedToSubmit.value) state.selectedProductCategoryError.value else false,
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
                            onProductAdd()
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
                state = AddProductScreenState(),
                onCategoryAdd = {},
                onProducerAdd = {},
                onProductAdd = {},
                categoriesWithAltNames = flowOf(),
                producers = flowOf(),
            )
        }
    }
}
