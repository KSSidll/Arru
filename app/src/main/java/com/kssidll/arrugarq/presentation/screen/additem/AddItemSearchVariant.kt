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
fun AddItemSearchVariant(
    variants: Flow<List<ProductVariant>>,
    onItemClick: (ProductVariant?) -> Unit,
    onAddClick: () -> Unit,
) {
    val collectedVariants = variants.collectAsState(initial = emptyList()).value

    var filter: String by remember {
        mutableStateOf(String())
    }

    var displayedVariants: List<ProductVariant> by remember {
        mutableStateOf(listOf())
    }

    displayedVariants = collectedVariants.map { variant ->
        val variantNameScore = FuzzySearch.extractOne(
            filter,
            listOf(variant.name)
        ).score

        variant to variantNameScore
    }
        .sortedByDescending { (_, score) ->
            score
        }
        .map { (variant, _) ->
            variant
        }

    Column {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.5f),
            reverseLayout = true
        ) {
            items(items = displayedVariants) {
                AddItemItemVariant(
                    item = it,
                    onItemClick = { variant ->
                        onItemClick(variant)
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
                            contentDescription = stringResource(R.string.add_product_variant_description),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        AddItemItemVariant(
            item = ProductVariant(
                0,
                stringResource(R.string.item_product_variant_default_value)
            ),
            onItemClick = {
                onItemClick(null)
            }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

    }
}

@Preview(
    group = "AddItemSearchVariant",
    name = "Add Item Search Variant Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddItemSearchVariant",
    name = "Add Item Search Variant Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddItemSearchVariantPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddItemSearchVariant(
                variants = flowOf(),
                onItemClick = {},
                onAddClick = {},
            )
        }
    }
}