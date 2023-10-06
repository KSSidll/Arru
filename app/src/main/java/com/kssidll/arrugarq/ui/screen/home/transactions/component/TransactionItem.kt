package com.kssidll.arrugarq.ui.screen.home.transactions.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*
import java.sql.*

@Composable
fun LazyItemScope.TransactionItem(
    embeddedItem: EmbeddedItem,
) {
    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .fillParentMaxWidth()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(Modifier.weight(1F)) {
            Text(embeddedItem.product.name)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            ProvideTextStyle(
                value = Typography.bodyLarge
            ) {
                Row(
                    modifier = Modifier.widthIn(min = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        embeddedItem.item.actualQuantity()
                            .toString()
                            .removeSuffix(".0")
                    )
                }

                Text(" x ")

                Row(
                    modifier = Modifier.widthIn(min = 64.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        embeddedItem.item.actualPrice()
                            .formatToCurrency()
                    )
                }


                Row(
                    modifier = Modifier.widthIn(min = 88.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        (embeddedItem.item.actualQuantity() * embeddedItem.item.actualPrice())
                            .formatToCurrency()
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
        Surface {
            LazyColumn {
                item {
                    TransactionItem(
                        embeddedItem = generateRandomEmbeddedItem(
                            itemDateTimeFrom = Date.valueOf("2022-06-01").time,
                            itemDateTimeUntil = Date.valueOf("2022-06-04").time,
                        ),
                    )
                }
            }
        }
    }
}
