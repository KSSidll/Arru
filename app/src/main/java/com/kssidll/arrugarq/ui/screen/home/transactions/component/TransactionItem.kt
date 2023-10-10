package com.kssidll.arrugarq.ui.screen.home.transactions.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LazyItemScope.TransactionItem(
    fullItem: FullItem,
) {
    Column(
        modifier = Modifier
            .heightIn(min = 60.dp)
            .fillParentMaxWidth()
            .padding(
                vertical = 6.dp,
                horizontal = 16.dp
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(Modifier.weight(1F)) {
                Text(fullItem.embeddedItem.product.name)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(
                    value = Typography.bodyLarge
                ) {
                    Spacer(Modifier.width(8.dp))

                    Row(
                        modifier = Modifier.widthIn(min = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            fullItem.embeddedItem.item.actualQuantity()
                                .toString()
                                .removeSuffix(".0")
                        )
                    }

                    Text(" x ")

                    Row(
                        modifier = Modifier.widthIn(min = 60.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            fullItem.embeddedItem.item.actualPrice()
                                .formatToCurrency()
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    Row(
                        modifier = Modifier.widthIn(min = 80.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            (fullItem.embeddedItem.item.actualQuantity() * fullItem.embeddedItem.item.actualPrice())
                                .formatToCurrency()
                        )
                    }
                }
            }
        }

        Row {
            FlowRow(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.Center,
            ) {
                val variant = fullItem.embeddedItem.variant
                Button(
                    modifier = Modifier
                        .padding(end = 3.dp),
                    onClick = {

                    },
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = variant?.name ?: stringResource(R.string.item_product_variant_default_value),
                        textAlign = TextAlign.Center,
                        style = Typography.labelSmall,
                    )
                }

                val category = fullItem.embeddedProduct.category
                Button(
                    modifier = Modifier.padding(end = 3.dp),
                    onClick = {

                    },
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = category.name,
                        textAlign = TextAlign.Center,
                        style = Typography.labelSmall,
                    )
                }

                val producer = fullItem.embeddedProduct.producer
                if (producer != null) {
                    Button(
                        modifier = Modifier.padding(end = 3.dp),
                        onClick = {

                        },
                        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PrecisionManufacturing,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = producer.name,
                            textAlign = TextAlign.Center,
                            style = Typography.labelSmall,
                        )
                    }
                }
            }

            val shop = fullItem.embeddedItem.shop
            if (shop != null) {
                Button(
                    onClick = {

                    },
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = shop.name,
                        textAlign = TextAlign.Center,
                        style = Typography.labelSmall,
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

@Preview(
    group = "Transaction Item",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Transaction Item",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TransactionItemPreview() {
    ArrugarqTheme {
        Surface(Modifier.fillMaxWidth()) {
            LazyColumn {
                item {
                    TransactionItem(
                        fullItem = generateRandomFullItem(),
                    )
                }
            }
        }
    }
}
