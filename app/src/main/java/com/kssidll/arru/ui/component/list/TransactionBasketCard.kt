package com.kssidll.arru.ui.component.list

import android.content.res.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.utils.*
import com.kssidll.arru.ui.theme.*
import java.text.*
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionBasketCard(
    transaction: TransactionBasketWithItems,
    transactionClickable: Boolean = true,
    onTransactionLongClick: ((transactionId: Long) -> Unit)? = null,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
    var itemsVisible by remember {
        mutableStateOf(!transactionClickable)
    }

    val transactionModifier =
        if (!transactionClickable)
            Modifier
        else
            Modifier.combinedClickable(
                role = Role.Button,
                onClick = {
                    itemsVisible = !itemsVisible
                },
                onClickLabel = stringResource(id = R.string.transaction_items_toggle),
                onLongClick = {
                    onTransactionLongClick?.invoke(transaction.id)
                },
                onLongClickLabel = stringResource(id = R.string.edit)
            )

    Column(
        modifier = transactionModifier
            .padding(vertical = 12.dp)
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

@Preview(
    group = "Transaction Basket Card",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Transaction Basket Card",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TransactionBasketCardPreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            TransactionBasketCard(
                transaction = TransactionBasketWithItems.generate(),
                onTransactionLongClick = {},
                onItemAddClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onItemCategoryClick = {},
                onItemProducerClick = {},
                onItemShopClick = {},
            )
        }
    }
}
