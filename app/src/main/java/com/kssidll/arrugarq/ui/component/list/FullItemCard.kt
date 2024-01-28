package com.kssidll.arrugarq.ui.component.list

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
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
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun LazyItemScope.FullItemCard(
    fullItem: FullItem,
    onItemClick: (item: FullItem) -> Unit,
    onItemLongClick: (item: FullItem) -> Unit,
    itemClickable: Boolean = true,
    onCategoryClick: (category: ProductCategory) -> Unit,
    showCategory: Boolean = true,
    onProducerClick: (producer: ProductProducer) -> Unit,
    showProducer: Boolean = true,
) {
    val itemModifier =
        if (itemClickable)
            Modifier.combinedClickable(
                role = Role.Button,
                onClick = {
                    onItemClick(fullItem)
                },
                onClickLabel = stringResource(id = R.string.select),
                onLongClick = {
                    onItemLongClick(fullItem)
                },
                onLongClickLabel = stringResource(id = R.string.edit)
            )
        else Modifier

    Column(
        modifier = itemModifier
            .heightIn(min = 60.dp)
            .fillParentMaxWidth()
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
                        text = fullItem.embeddedItem.product.name,
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
                        text = fullItem.embeddedItem.variant?.name
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
                        text = fullItem.embeddedItem.item.actualQuantity()
                            .toString()
                            .removeSuffix(".0"),
                        style = Typography.bodyLarge,
                    )

                    Spacer(Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.ShoppingBasket,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }

                Spacer(Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = fullItem.embeddedItem.item.actualPrice()
                            .formatToCurrency(),
                        style = Typography.bodyLarge,
                    )

                    Spacer(Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.Sell,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }

                Spacer(Modifier.height(2.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = (fullItem.embeddedItem.item.actualQuantity() * fullItem.embeddedItem.item.actualPrice()).formatToCurrency(),
                        style = Typography.bodyLarge,
                    )

                    Spacer(Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
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
                if (showCategory) {
                    val category = fullItem.embeddedProduct.category
                    Button(
                        modifier = Modifier.padding(end = 3.dp),
                        onClick = {
                            onCategoryClick(category)
                        },
                        contentPadding = PaddingValues(
                            vertical = 0.dp,
                            horizontal = 12.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                    ) {
                        Text(
                            text = category.name,
                            textAlign = TextAlign.Center,
                            style = Typography.labelMedium,
                        )
                    }

                }

                if (showProducer) {
                    val producer = fullItem.embeddedProduct.producer
                    if (producer != null) {
                        Button(
                            modifier = Modifier.padding(end = 3.dp),
                            onClick = {
                                onProducerClick(producer)
                            },
                            contentPadding = PaddingValues(
                                vertical = 0.dp,
                                horizontal = 12.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
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
        }
    }
}

@Preview(
    group = "Full Item Card",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Full Item Card",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun FullItemCardPreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            LazyColumn {
                item {
                    FullItemCard(
                        fullItem = FullItem.generate(),
                        onItemClick = {},
                        onItemLongClick = {},
                        onCategoryClick = {},
                        onProducerClick = {},
                    )
                }
            }
        }
    }
}
