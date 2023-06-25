package com.kssidll.arrugarq.ui.addproduct

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arrugarq.data.data.ProductCategory
import com.kssidll.arrugarq.ui.shared.SecondaryAppBar
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    onCategoryAdd: () -> Unit,
    onProductAdd: (AddProductData) -> Unit,
    categories: Flow<List<ProductCategory>>,
    state: AddProductState,
) {
    Column {
        var isCategorySearchExpanded: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        BackHandler(
            enabled = isCategorySearchExpanded
        ) {
            isCategorySearchExpanded = false
        }

        var isCategoryError: Boolean by remember {
            mutableStateOf(false)
        }

        var isNameError: Boolean by remember {
            mutableStateOf(false)
        }

        SecondaryAppBar(
            onBack = {
                if (
                    !isCategorySearchExpanded
                ) {
                    onBack()
                }
                isCategorySearchExpanded = false
            }
        ) {
            Text(text = "Product")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            if (isCategorySearchExpanded) {
                val collectedCategories = categories.collectAsState(initial = emptyList()).value

                LazyColumn {
                    items(items = collectedCategories) {
                        AddProductItemCategory(
                            item = it,
                            onItemClick = { type ->
                                state.selectedProductCategory.value = type
                                isCategorySearchExpanded = false
                            }
                        )
                        Divider()
                    }
                }
            } else {
                Column {
                    Column(
                        modifier = Modifier.fillMaxHeight(0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.Center
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
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                placeholder = {
                                    Text(
                                        text = "Category",
                                        modifier = Modifier
                                            .alpha(0.5F)
                                    )
                                },
                                isError = isCategoryError,
                                trailingIcon = {
                                    BoxWithConstraints {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .aspectRatio(1F)
                                                .clickable {
                                                    onCategoryAdd()
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val lineColor = MaterialTheme.colorScheme.onBackground
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                drawLine(
                                                    color = lineColor,
                                                    start = Offset(0F, 0F),
                                                    end = Offset(0F, size.height),
                                                    strokeWidth = Dp.Hairline.value
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add new Product Category",
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }

                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                OutlinedTextField(
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
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                    placeholder = {
                                        Text(
                                            text = "Name",
                                            modifier = Modifier
                                                .alpha(0.5F)
                                        )
                                    },
                                    isError = isNameError
                                )
                            }
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
                                val name: String = state.name.value

                                isCategoryError = category == null
                                isNameError = name.isEmpty()

                                if (
                                    !isCategoryError &&
                                    !isNameError
                                ) {
                                    onProductAdd(
                                        AddProductData(
                                            categoryId = category!!.id,
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
                                    contentDescription = "Add Product",
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Product",
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

@Preview(group = "AddProductScreen", name = "Add Product Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddProductScreen", name = "Add Product Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
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
                onProductAdd = {},
                categories = flowOf(),
                state = AddProductState()
            )
        }
    }
}
