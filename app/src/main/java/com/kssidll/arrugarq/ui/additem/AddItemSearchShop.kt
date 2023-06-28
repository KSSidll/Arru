package com.kssidll.arrugarq.ui.additem

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arrugarq.data.data.Shop
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.xdrop.fuzzywuzzy.FuzzySearch

@Composable
fun AddItemSearchShop(
    shops: Flow<List<Shop>>,
    onItemClick: (Shop) -> Unit,
    onAddClick: () -> Unit,
) {
    val collectedShops = shops.collectAsState(initial = emptyList()).value

    var filter: String by remember {
        mutableStateOf(String())
    }

    var displayedShops: List<Shop> by remember {
        mutableStateOf(listOf())
    }

    displayedShops = collectedShops.map { shop ->
        val productNameScore = FuzzySearch.extractOne(filter, listOf(shop.name)).score

        shop to productNameScore
    }.sortedByDescending { (_, score) ->
        score
    }.map { (shop, _) ->
        shop
    }

    Column {
        LazyColumn (
            modifier = Modifier.fillMaxHeight(0.5f),
            reverseLayout = true
        ) {
            items(items = displayedShops) {
                AddItemItemShop(
                    item = it,
                    onItemClick = { shop ->
                        onItemClick(shop)
                    }
                )
                Divider()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = MaterialTheme.colorScheme.outline)

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
                        text = "Filter",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .alpha(0.5F)
                            .padding(end = 6.dp)
                    )
                },
                trailingIcon = {
                    BoxWithConstraints {
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
                                    start = Offset(0F, 0F),
                                    end = Offset(0F, size.height),
                                    strokeWidth = Dp.Hairline.value
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add new Shop",
                                modifier = Modifier.size(40.dp)
                            )
                        }

                    }
                }
            )
        }
        Divider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(group = "AddItemSearchShop", name = "Add Item Search Shop Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddItemSearchShop", name = "Add Item Search Shop Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddItemSearchShopPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddItemSearchShop(
                shops = flowOf(),
                onItemClick = {},
                onAddClick = {},
            )
        }
    }
}
