package com.kssidll.arru.ui.component.model

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.PrecisionManufacturing
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.data.data.TagEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.model.TransactionPreview
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

private val HEADER_HEIGHT: Dp = 105.dp

fun LazyListScope.transactionPreviewCard(
    modifier: Modifier = Modifier,
    transaction: TransactionPreview,
    itemsVisible: Boolean,
    onTransactionClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionClickLabel: String? = null,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionLongClickLabel: String? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    headerColor: Color,
) {
    transactionPreviewCardHeader(
        modifier = modifier,
        transaction = transaction,
        itemsVisible = itemsVisible,
        onTransactionClick = onTransactionClick,
        onTransactionClickLabel = onTransactionClickLabel,
        onTransactionLongClick = onTransactionLongClick,
        onTransactionLongClickLabel = onTransactionLongClickLabel,
        onItemAddClick = onItemAddClick,
        headerColor = headerColor,
    )

    item(
        // ensure no reuse because each item has different size and elements
        // without this, scrolling up on a list may reuse the expanded item on a hidden item
        // creating a gap in the list
        contentType = transaction.itemsVisible.value
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
                    //                    transaction.items.forEach { item ->
                    //                        FullItemCard(
                    //                            item = item,
                    //                            onItemClick = {
                    //                                onItemClick(it.product.id)
                    //                            },
                    //                            onItemLongClick = {
                    //                                onItemLongClick(it.id)
                    //                            },
                    //                            onCategoryClick = {
                    //                                onItemCategoryClick(it.id)
                    //                            },
                    //                            onProducerClick = {
                    //                                onItemProducerClick(it.id)
                    //                            },
                    //                        )
                    //                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
fun LazyListScope.transactionPreviewCardHeader(
    modifier: Modifier = Modifier,
    transaction: TransactionPreview,
    itemsVisible: Boolean,
    onTransactionClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionClickLabel: String? = null,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onTransactionLongClickLabel: String? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    headerColor: Color,
) {

    val shopTag = transaction.tags.find { it.id == TagEntity.System.SHOP.id }
    val producerTag = transaction.tags.find { it.id == TagEntity.System.PRODUCER.id }
    val otherTags = transaction.tags.filterNot {
        it.id == TagEntity.System.SHOP.id || it.id == TagEntity.System.PRODUCER.id
    }

    stickyHeader(
        key = transaction.id,
        contentType = TransactionPreview
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
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

            Surface(
                color = headerColor,
                modifier = transactionModifier
                    .height(HEADER_HEIGHT)
                    .fillMaxWidth()
                    .padding(vertical = 9.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 8.dp
                            )
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = SimpleDateFormat(
                                    "d MMMM, yyyy",
                                    Locale.getDefault()
                                ).format(transaction.date),
                                style = Typography.titleMedium,
                            )
                        }

                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(
                                        end = 20.dp,
                                    )
                                    .align(Alignment.BottomEnd)
                            ) {
                                Text(
                                    text = TransactionEntity.actualTotalCost(transaction.totalCost)
                                        .formatToCurrency(),
                                    style = Typography.headlineSmall,
                                )

                                Spacer(Modifier.width(5.dp))

                                Icon(
                                    imageVector = Icons.Outlined.Payment,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(
                                start = 24.dp,
                                end = 8.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (shopTag != null && shopTag.childrenTags.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(3.dp))

                                Button(
                                    onClick = {
                                        // TODO
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
                                    if (shopTag.childrenTags.size > 1) {
                                        Text(
                                            text = shopTag.childrenTags.size.toString(),
                                            style = Typography.labelLarge,
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Rounded.Store,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = shopTag.childrenTags.first().name,
                                        style = Typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }

                            if (producerTag != null && producerTag.childrenTags.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(3.dp))

                                Button(
                                    onClick = {
                                        // TODO
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
                                    if (producerTag.childrenTags.size > 1) {
                                        Text(
                                            text = producerTag.childrenTags.size.toString(),
                                            style = Typography.labelLarge,
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Rounded.PrecisionManufacturing,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = producerTag.childrenTags.first().name,
                                        style = Typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(
                                    start = 3.dp,
                                    end = 20.dp
                                )
                        ) {
                            if (otherTags.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        // TODO
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
                                        text = otherTags.size.toString(),
                                        style = Typography.labelLarge,
                                    )

                                    Icon(
                                        imageVector = Icons.Rounded.Label,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
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
private fun TransactionPreviewCardPreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            val color = MaterialTheme.colorScheme.background
            val transaction = TransactionPreview.generate()

            LazyColumn {
                transactionPreviewCard(
                    transaction = transaction,
                    itemsVisible = true,
                    onItemAddClick = {},
                    headerColor = color,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TransactionPreviewCardHeaderPlaceholderSizePreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            val color = MaterialTheme.colorScheme.background
            val transaction = TransactionPreview.generate()

            LazyColumn {
                transactionPreviewCardHeader(
                    transaction = transaction,
                    itemsVisible = false,
                    onItemAddClick = {},
                    headerColor = color,
                    modifier = Modifier.height(HEADER_HEIGHT)
                )
            }
        }
    }
}