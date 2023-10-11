package com.kssidll.arrugarq.ui.screen.home.transactions.component

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
            .clickable {

            }
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
            Row(
                modifier = Modifier.weight(1F),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = fullItem.embeddedItem.product.name,
                    style = Typography.titleLarge,
                )
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
                val variant = fullItem.embeddedItem.variant
                Button(
                    modifier = Modifier
                        .padding(end = 3.dp),
                    onClick = {

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
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = variant?.name
                            ?: stringResource(R.string.item_product_variant_default_value),
                        textAlign = TextAlign.Center,
                        style = Typography.labelMedium,
                    )
                }

                val category = fullItem.embeddedProduct.category
                Button(
                    modifier = Modifier.padding(end = 3.dp),
                    onClick = {

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

                val producer = fullItem.embeddedProduct.producer
                if (producer != null) {
                    Button(
                        modifier = Modifier.padding(end = 3.dp),
                        onClick = {

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

            val shop = fullItem.embeddedItem.shop
            if (shop != null) {
                Button(
                    onClick = {

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
