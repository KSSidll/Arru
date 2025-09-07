package com.kssidll.arru.ui.component.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.DAY_IN_MILIS
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.itemList(
    transactionItems: LazyPagingItems<Item>,
    onItemClick: ((item: Item) -> Unit)? = null,
    onItemLongClick: ((item: Item) -> Unit)? = null,
    onCategoryClick: ((item: Item) -> Unit)? = null,
    onCategoryLongClick: ((item: Item) -> Unit)? = null,
    onProducerClick: ((item: Item) -> Unit)? = null,
    onProducerLongClick: ((item: Item) -> Unit)? = null,
    onShopClick: ((item: Item) -> Unit)? = null,
    onShopLongClick: ((item: Item) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val itemCount = transactionItems.itemCount
    var lastItem: Item? = null

    // FIXME this will iterate through the whole loop every time item fetch happens, check if
    // paging3 can add sticky headers as separators when you see this
    for (index in 0 until itemCount) {
        val item = transactionItems.peek(index)

        if (item != null) {
            // ... yeah
            if (
                index == 0 ||
                    (lastItem != null && item.date / DAY_IN_MILIS != lastItem.date / DAY_IN_MILIS)
            ) {
                stickyHeader(contentType = "date header") {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier.width(600.dp).align(Alignment.Center),
                        ) {
                            Surface(
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text =
                                            SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                                                .format(
                                                    item.date -
                                                        TimeZone.getDefault().getOffset(item.date)
                                                ),
                                        style = Typography.headlineMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(contentType = "item") {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.width(600.dp).align(Alignment.Center),
                    ) {
                        ItemCard(
                            item = transactionItems[index]!!,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick,
                            onCategoryClick = onCategoryClick,
                            onCategoryLongClick = onCategoryLongClick,
                            onProducerClick = onProducerClick,
                            onProducerLongClick = onProducerLongClick,
                            onShopClick = onShopClick,
                            onShopLongClick = onShopLongClick,
                        )
                    }
                }
            }
        }

        lastItem = item
    }
}

@PreviewLightDark
@Composable
private fun ItemListPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val transactionItems =
                flowOf(PagingData.from(Item.generateList())).collectAsLazyPagingItems()
            LazyColumn {
                itemList(
                    transactionItems = transactionItems,
                    onItemClick = {},
                    onItemLongClick = {},
                    onCategoryClick = {},
                    onProducerClick = {},
                    onShopClick = {},
                )
            }
        }
    }
}
