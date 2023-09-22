package com.kssidll.arrugarq.presentation.screen.addproduct

import android.content.res.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.theme.*
import com.kssidll.arrugarq.presentation.widget.other.*
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
    val optionalBorderAlpha = 0.40f

    Column {
        var isCategorySearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        var isProducerSearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        BackHandler(
            enabled = isCategorySearchExpanded || isProducerSearchExpanded
        ) {
            isCategorySearchExpanded = false
            isProducerSearchExpanded = false
        }

        var categoryError: Boolean by remember {
            mutableStateOf(false)
        }

        var nameError: Boolean by remember {
            mutableStateOf(false)
        }

        SecondaryAppBar(
            onBack = {
                if (
                    !isCategorySearchExpanded &&
                    !isProducerSearchExpanded
                ) {
                    onBack()
                }
                isCategorySearchExpanded = false
                isProducerSearchExpanded = false
            }
        ) {
            Text(text = stringResource(R.string.item_product))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            if (isProducerSearchExpanded) {
                AddProductSearchProducer(
                    producers = producers,
                    onItemClick = { producer ->
                        state.selectedProductProducer.value = producer
                        isProducerSearchExpanded = false
                    },
                    onAddClick = onProducerAdd
                )
            } else if (isCategorySearchExpanded) {
                AddProductSearchCategory(
                    categoriesWithAltNames = categoriesWithAltNames,
                    onItemClick = { category ->
                        state.selectedProductCategory.value = category
                        isCategorySearchExpanded = false
                    },
                    onAddClick = onCategoryAdd
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
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(0.85f),
                                    singleLine = true,
                                    value = state.name.value,
                                    onValueChange = {
                                        state.name.value = it
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        cursorColor = MaterialTheme.colorScheme.outline,
                                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                                    ),
                                    textStyle = TextStyle.Default.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 16.sp
                                    ),
                                    suffix = {
                                        Text(
                                            text = stringResource(R.string.item_product),
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .alpha(0.5F)
                                        )
                                    },
                                    isError = nameError
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                singleLine = true,
                                value = state.selectedProductProducer.value?.name ?: String(),
                                onValueChange = {

                                },
                                modifier = Modifier
                                    .onFocusEvent {
                                        if (it.isFocused) {
                                            isProducerSearchExpanded = true
                                        }
                                    }
                                    .fillMaxSize(),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = optionalBorderAlpha),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = optionalBorderAlpha)
                                ),
                                suffix = {
                                    Text(
                                        text = stringResource(R.string.item_product_producer),
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                            .padding(end = 6.dp)
                                    )
                                },
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1F)
                                            .clickable {
                                                onProducerAdd()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val lineColor =
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
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
                                            contentDescription = stringResource(R.string.add_product_producer_description),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                singleLine = true,
                                value = state.selectedProductCategory.value?.name ?: String(),
                                onValueChange = {

                                },
                                modifier = Modifier
                                    .onFocusEvent {
                                        if (it.isFocused) {
                                            isCategorySearchExpanded = true
                                        }
                                    }
                                    .fillMaxSize(),
                                textStyle = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                suffix = {
                                    Text(
                                        text = stringResource(R.string.item_product_category),
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .alpha(0.5F)
                                            .padding(end = 6.dp)
                                    )
                                },
                                isError = categoryError,
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1F)
                                            .clickable {
                                                onCategoryAdd()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val lineColor = MaterialTheme.colorScheme.outline
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
                                            contentDescription = stringResource(R.string.add_product_category_description),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxHeight(0.4f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {

                        Button(
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
