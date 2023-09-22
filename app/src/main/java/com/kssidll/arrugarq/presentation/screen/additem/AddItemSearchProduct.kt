package com.kssidll.arrugarq.presentation.screen.additem

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.theme.*
import kotlinx.coroutines.flow.*
import me.xdrop.fuzzywuzzy.*

@Composable
fun AddItemSearchProduct(
    productsWithAltNames: Flow<List<ProductWithAltNames>>,
    onItemClick: (Product) -> Unit,
    onAddClick: () -> Unit,
) {
    val collectedProductsWithAltNames =
        productsWithAltNames.collectAsState(initial = emptyList()).value

    var filter: String by remember {
        mutableStateOf(String())
    }

    var displayedProducts: List<ProductWithAltNames> by remember {
        mutableStateOf(listOf())
    }

    displayedProducts = collectedProductsWithAltNames.map { productWithAltNames ->
        val productNameScore = FuzzySearch.extractOne(
            filter,
            listOf(productWithAltNames.product.name)
        ).score
        val bestAlternativeNamesScore = if (productWithAltNames.alternativeNames.isNotEmpty()) {
            FuzzySearch.extractOne(
                filter,
                productWithAltNames.alternativeNames.map { it.name }).score
        } else -1

        val maxScore = maxOf(
            productNameScore,
            bestAlternativeNamesScore
        )

        productWithAltNames to maxScore
    }
        .sortedByDescending { (_, score) ->
            score
        }
        .map { (productWithAltNames, _) ->
            productWithAltNames
        }

    Column {

        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.5f),
            reverseLayout = true
        ) {
            // TODO show the producent if it's not containted within the name
            items(items = displayedProducts) {
                AddItemItemProduct(
                    item = it.product,
                    onItemClick = { product: Product ->
                        onItemClick(product)
                    }
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
                value = filter,
                onValueChange = {
                    filter = it
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
                        text = stringResource(R.string.search),
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
                                onAddClick()
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
                            contentDescription = stringResource(R.string.add_product_description),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(
    group = "AddItemSearchProduct",
    name = "Add Item Search Product Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddItemSearchProduct",
    name = "Add Item Search Product Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddItemSearchProductPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddItemSearchProduct(
                productsWithAltNames = flowOf(),
                onItemClick = {},
                onAddClick = {},
            )
        }
    }
}
