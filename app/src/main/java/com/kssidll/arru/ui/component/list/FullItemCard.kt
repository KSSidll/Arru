package com.kssidll.arru.ui.component.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.PrecisionManufacturing
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.R
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun FullItemCard(
    item: FullItem,
    onItemClick: ((item: FullItem) -> Unit)? = null,
    onItemLongClick: ((item: FullItem) -> Unit)? = null,
    onCategoryClick: ((category: ProductCategory) -> Unit)? = null,
    onProducerClick: ((producer: ProductProducer) -> Unit)? = null,
    onShopClick: ((shop: Shop) -> Unit)? = null,
) {
    val currencyLocale = LocalCurrencyFormatLocale.current

    val itemModifier =
        if (onItemClick != null || onItemLongClick != null)
            Modifier.combinedClickable(
                role = Role.Button,
                onClick = {
                    onItemClick?.invoke(item)
                },
                onClickLabel = stringResource(id = R.string.select),
                onLongClick = {
                    onItemLongClick?.invoke(item)
                },
                onLongClickLabel = stringResource(id = R.string.edit)
            )
        else Modifier

    Column(
        modifier = itemModifier
            .heightIn(min = 60.dp)
            .padding(
                vertical = 6.dp,
                horizontal = 16.dp
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight(),
            ) {
                Box(
                    modifier = Modifier.weight(1F),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = item.product.name,
                        style = Typography.titleLarge,
                    )
                }

                Row {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = item.variant?.name
                            ?: stringResource(R.string.item_product_variant_default_value),
                        textAlign = TextAlign.Center,
                        style = Typography.labelMedium,
                    )
                }
            }

            Column(
                modifier = Modifier.width(IntrinsicSize.Min),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.actualQuantity()
                            .toString()
                            .removeSuffix(".0"),
                        style = Typography.bodyLarge,
                    )

                    Spacer(Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.ShoppingBasket,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.actualPrice()
                            .formatToCurrency(currencyLocale),
                        style = Typography.bodyLarge,
                    )

                    Spacer(Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.Sell,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.height(2.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = (item.actualQuantity() * item.actualPrice()).formatToCurrency(
                            currencyLocale
                        ),
                        style = Typography.bodyLarge,
                    )

                    Spacer(Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Spacer(Modifier.height(3.dp))

        Row {
            FlowRow(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.Center,
            ) {
                if (onCategoryClick != null) {
                    val category = item.category
                    Button(
                        modifier = Modifier.padding(end = 3.dp),
                        onClick = {
                            onCategoryClick.invoke(category)
                        },
                        contentPadding = PaddingValues(
                            vertical = 0.dp,
                            horizontal = 12.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ) {
                        Text(
                            text = category.name,
                            textAlign = TextAlign.Center,
                            style = Typography.labelMedium,
                        )
                    }

                }

                if (onProducerClick != null) {
                    val producer = item.producer
                    if (producer != null) {
                        Button(
                            modifier = Modifier.padding(end = 3.dp),
                            onClick = {
                                onProducerClick.invoke(producer)
                            },
                            contentPadding = PaddingValues(
                                vertical = 0.dp,
                                horizontal = 12.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PrecisionManufacturing,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                            )
                            Text(
                                text = producer.name,
                                textAlign = TextAlign.Center,
                                style = Typography.labelMedium,
                            )
                        }
                    }
                }
            }

            if (onShopClick != null) {
                val shop = item.shop
                if (shop != null) {
                    Button(
                        onClick = {
                            onShopClick.invoke(shop)
                        },
                        contentPadding = PaddingValues(
                            vertical = 0.dp,
                            horizontal = 12.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ) {
                        Text(
                            text = shop.name,
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
    }
}

@PreviewLightDark
@Composable
private fun FullItemCardPreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            FullItemCard(
                item = FullItem.generate(),
                onItemClick = {},
                onItemLongClick = {},
                onCategoryClick = {},
                onProducerClick = {},
                onShopClick = {},
            )
        }
    }
}
