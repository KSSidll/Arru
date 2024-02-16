package com.kssidll.arrugarq.ui.component.list

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.ui.theme.*
import java.text.*
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionBasketCard(
    transaction: TransactionBasketWithItems,
    onTransactionLongClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
    var itemsVisible by remember {
        mutableStateOf(false)
    }

    val transactionModifier = Modifier.combinedClickable(
        role = Role.Button,
        onClick = {
            itemsVisible = !itemsVisible
        },
        onClickLabel = stringResource(id = R.string.transaction_items_toggle),
        onLongClick = {
            onTransactionLongClick(transaction.id)
        },
        onLongClickLabel = stringResource(id = R.string.edit)
    )

    Column(
        modifier = transactionModifier
            .padding(bottom = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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

        AnimatedVisibility(visible = itemsVisible) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = ShapeDefaults.Medium
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