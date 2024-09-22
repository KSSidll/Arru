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
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.fullItemListContent(
    transactionItems: LazyPagingItems<FullItem>,
    onItemClick: ((item: FullItem) -> Unit)? = null,
    onItemLongClick: ((item: FullItem) -> Unit)? = null,
    onCategoryClick: ((category: ProductCategory) -> Unit)? = null,
    onProducerClick: ((producer: ProductProducer) -> Unit)? = null,
    onShopClick: ((shop: Shop) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val itemCount = transactionItems.itemCount
    var lastItem: FullItem? = null

    // FIXME this will iterate through the whole loop every time item fetch happens, check if paging3 can add sticky headers as separators when you see this
    for (index in 0 until itemCount) {
        val item = transactionItems.peek(index)

        if (item != null) {
            //... yeah
            if (index == 0 || (lastItem != null && item.date / DAY_IN_MILIS != lastItem.date / DAY_IN_MILIS)) {
                stickyHeader {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                                .width(600.dp)
                                .align(Alignment.Center)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    bottomStart = 24.dp,
                                    bottomEnd = 24.dp,
                                ),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = SimpleDateFormat(
                                            "MMM d, yyyy",
                                            Locale.getDefault()
                                        ).format(item.date),
                                        style = Typography.headlineMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier
                            .width(600.dp)
                            .align(Alignment.Center)
                    ) {
                        FullItemCard(
                            item = transactionItems[index]!!,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick,
                            onCategoryClick = onCategoryClick,
                            onProducerClick = onProducerClick,
                            onShopClick = onShopClick,
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
private fun FullItemListContentPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val transactionItems =
                flowOf(PagingData.from(FullItem.generateList())).collectAsLazyPagingItems()
            LazyColumn {
                fullItemListContent(
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
