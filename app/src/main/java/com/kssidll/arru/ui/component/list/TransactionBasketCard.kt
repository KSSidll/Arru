package com.kssidll.arru.ui.component.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.data.data.TransactionBasket
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

private val HEADER_HEIGHT: Dp = 105.dp

fun LazyListScope.transactionBasketCard(
    modifier: Modifier = Modifier,
    transaction: TransactionBasketWithItems,
    itemsVisible: Boolean,
    onTransactionClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    headerColor: Color,
    currencyLocale: Locale,
) {
    transactionBasketCardHeader(
        modifier = modifier,
        transaction = transaction,
        itemsVisible = itemsVisible,
        onTransactionClick = onTransactionClick,
        onTransactionLongClick = onTransactionLongClick,
        onItemAddClick = onItemAddClick,
        onItemShopClick = onItemShopClick,
        headerColor = headerColor,
        currencyLocale = currencyLocale,
    )

    item(
        // ensure no reuse because each item has different size and elements
        // without this, scrolling up on a list may reuse the expanded item on a hidden item
        // creating a gap in the list
        contentType = transaction.id
    ) {
        AnimatedVisibility(
            visible = itemsVisible,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(12.dp),
            ) {
                Column {
                    val onItemClick = remember(onItemClick) { onItemClick }
                    val onItemLongClick = remember(onItemLongClick) { onItemLongClick }
                    val onItemCategoryClick = remember(onItemCategoryClick) { onItemCategoryClick }
                    val onItemProducerClick = remember(onItemProducerClick) { onItemProducerClick }

                    transaction.items.forEach { item ->
                        FullItemCard(
                            item = item,
                            onItemClick = {
                                onItemClick(it.product.id)
                            },
                            onItemLongClick = {
                                onItemLongClick(it.id)
                            },
                            onCategoryClick = {
                                onItemCategoryClick(it.id)
                            },
                            onProducerClick = {
                                onItemProducerClick(it.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.transactionBasketCardHeader(
    modifier: Modifier = Modifier,
    transaction: TransactionBasketWithItems,
    itemsVisible: Boolean,
    onTransactionClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    headerColor: Color,
    currencyLocale: Locale
) {
    stickyHeader(
        key = transaction.id,
        contentType = TransactionBasketWithItems
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            val transactionModifier =
                if (onTransactionClick != null && onTransactionLongClick != null) {
                    Modifier.combinedClickable(
                        role = Role.Button,
                        onClick = {
                            onTransactionClick(transaction.id)
                        },
                        onLongClick = {
                            onTransactionLongClick(transaction.id)
                        }
                    )
                } else if (onTransactionClick != null) {
                    Modifier.clickable(
                        role = Role.Button,
                    ) {
                        onTransactionClick(transaction.id)
                    }
                } else Modifier

            Surface(color = headerColor) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = transactionModifier
                        .height(HEADER_HEIGHT)
                        .fillMaxWidth()
                        .padding(vertical = 9.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = 20.dp,
                                end = 8.dp
                            )
                    ) {
                        Text(
                            text = SimpleDateFormat(
                                "d MMMM, yyyy",
                                Locale.getDefault()
                            ).format(transaction.date),
                            style = Typography.headlineSmall,
                        )

                        Spacer(Modifier.height(5.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    end = 20.dp
                                )
                        ) {
                            if (transaction.shop != null) {
                                val onItemShopClick = remember(transaction.shop.id) { onItemShopClick }

                                Button(
                                    onClick = {
                                        onItemShopClick(transaction.shop.id)
                                    },
                                    contentPadding = PaddingValues(
                                        vertical = 0.dp,
                                        horizontal = 12.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                ) {
                                    Text(
                                        text = transaction.shop.name,
                                        textAlign = TextAlign.Center,
                                        style = Typography.labelMedium,
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.Store,
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp),
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            end = 20.dp,
                        )
                    ) {
                        Text(
                            text = TransactionBasket.actualTotalCost(transaction.totalCost)
                                .formatToCurrency(currencyLocale),
                            style = Typography.titleLarge,
                        )

                        Spacer(Modifier.width(5.dp))

                        Icon(
                            imageVector = Icons.Outlined.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = itemsVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                Surface(
                    color = headerColor,
                    shape = RoundedCornerShape(
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    ),
                ) {
                    Column {
                        if (!transaction.note.isNullOrBlank()) {
                            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                                Text(
                                    text = transaction.note,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        OutlinedIconButton(
                            onClick = {
                                onItemAddClick(transaction.id)
                            },
                            shape = ShapeDefaults.Medium,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 8.dp
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.transactionBasketCardHeaderPlaceholder(
    modifier: Modifier = Modifier,
) {
    stickyHeader {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(HEADER_HEIGHT)
        )
    }
}

@PreviewLightDark
@Composable
private fun TransactionBasketCardPreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            val color = MaterialTheme.colorScheme.background
            LazyColumn {
                transactionBasketCard(
                    transaction = TransactionBasketWithItems.generate(),
                    itemsVisible = true,
                    onItemAddClick = {},
                    onItemClick = {},
                    onItemLongClick = {},
                    onItemCategoryClick = {},
                    onItemProducerClick = {},
                    onItemShopClick = {},
                    headerColor = color,
                    currencyLocale = Locale.getDefault()
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TransactionBasketCardHeaderPlaceholderSizePreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            val color = MaterialTheme.colorScheme.background
            LazyColumn {
                transactionBasketCardHeader(
                    transaction = TransactionBasketWithItems.generate(),
                    itemsVisible = false,
                    onItemAddClick = {},
                    onItemShopClick = {},
                    headerColor = color,
                    currencyLocale = Locale.getDefault(),
                    modifier = Modifier.height(HEADER_HEIGHT)
                )
            }
        }
    }
}
