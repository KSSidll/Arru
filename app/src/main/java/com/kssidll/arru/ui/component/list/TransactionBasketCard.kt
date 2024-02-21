package com.kssidll.arru.ui.component.list

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.utils.*
import com.kssidll.arru.ui.theme.*
import java.text.*
import java.util.*

private val HEADER_HEIGHT: Dp = 105.dp

fun LazyListScope.transactionBasketCard(
    modifier: Modifier = Modifier,
    transaction: TransactionBasketWithItems,
    itemsVisible: Boolean,
    onTransactionClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionClickLabel: String? = null,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionLongClickLabel: String? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    headerColor: Color,
) {
    transactionBasketCardHeader(
        modifier = modifier,
        transaction = transaction,
        itemsVisible = itemsVisible,
        onTransactionClick = onTransactionClick,
        onTransactionClickLabel = onTransactionClickLabel,
        onTransactionLongClick = onTransactionLongClick,
        onTransactionLongClickLabel = onTransactionLongClickLabel,
        onItemAddClick = onItemAddClick,
        onItemShopClick = onItemShopClick,
        headerColor = headerColor,
    )

    item {
        Column(
            modifier = modifier
                .animateContentSize()
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = itemsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    ),
                ) {
                    Column {
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
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.transactionBasketCardHeader(
    modifier: Modifier = Modifier,
    transaction: TransactionBasketWithItems,
    itemsVisible: Boolean,
    onTransactionClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionClickLabel: String? = null,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionLongClickLabel: String? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    headerColor: Color,
) {
    stickyHeader {
        Column(
            modifier = modifier
                .animateContentSize()
                .fillMaxWidth()
        ) {
            val transactionModifier =
                if (onTransactionClick != null && onTransactionLongClick != null) {
                    Modifier.combinedClickable(
                        role = Role.Button,
                        onClick = {
                            onTransactionClick(transaction.id)
                        },
                        onClickLabel = onTransactionClickLabel,
                        onLongClick = {
                            onTransactionLongClick(transaction.id)
                        },
                        onLongClickLabel = onTransactionLongClickLabel,
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
                                Button(
                                    onClick = {
                                        onItemShopClick(transaction.shop.id)
                                    },
                                    contentPadding = PaddingValues(
                                        vertical = 0.dp,
                                        horizontal = 12.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary,
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
                            text = transaction.totalCost.toFloat()
                                .div(TransactionBasket.COST_DIVISOR)
                                .formatToCurrency(),
                            style = Typography.titleLarge,
                        )

                        Spacer(Modifier.width(5.dp))

                        Icon(
                            imageVector = Icons.Outlined.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = itemsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Surface(color = headerColor) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = if (transaction.items.isEmpty()) {
                            RoundedCornerShape(12.dp)
                        } else {
                            RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                            )
                        },
                    ) {
                        Column {
                            OutlinedIconButton(
                                onClick = {
                                    onItemAddClick(transaction.id)
                                },
                                shape = ShapeDefaults.Medium,
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary
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
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
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
fun TransactionBasketCardPreview() {
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
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun TransactionBasketCardHeaderPlaceholderSizePreview() {
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
                    modifier = Modifier.height(HEADER_HEIGHT)
                )
            }
        }
    }
}
