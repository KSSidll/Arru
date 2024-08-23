package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TransactionBasket::class,
            parentColumns = ["id"],
            childColumns = ["transactionBasketId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class TransactionBasketItem(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val transactionBasketId: Long,
    @ColumnInfo(index = true) val itemId: Long,
) {
    @Ignore
    constructor(
        transactionBasketId: Long,
        itemId: Long
    ): this(
        0,
        transactionBasketId,
        itemId
    )

    /**
     * Converts the [TransactionBasket] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [TransactionBasket] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${transactionBasketId};${itemId}"
    }

    companion object {
        /**
         * Returns the [String] representing the [TransactionBasketItem] csv format headers
         * @return [String] representing the [TransactionBasketItem] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;transactionBasketId;itemId"
        }

        @Ignore
        fun generate(transactionBasketItemId: Long = 0): TransactionBasketItem {
            return TransactionBasketItem(
                id = transactionBasketItemId,
                transactionBasketId = generateRandomLongValue(),
                itemId = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<TransactionBasketItem> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}

/**
 * Converts a list of [TransactionBasketItem] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [TransactionBasketItem] data as list of string with csv format
 */
fun List<TransactionBasketItem>.asCsvList(includeHeaders: Boolean = false): List<String> =
    buildList {
        // Add headers
        if (includeHeaders) {
            add(TransactionBasketItem.csvHeaders() + "\n")
        }

        // Add rows
        this@asCsvList.forEach {
            add(it.formatAsCsvString() + "\n")
        }
    }

data class TransactionBasketWithItems(
    val id: Long,
    val date: Long,
    val shop: Shop?,
    val totalCost: Long,
    val items: List<FullItem>,
) {
    companion object {
        fun generate(transactionBasketWithItemsId: Long = 0): TransactionBasketWithItems {
            return TransactionBasketWithItems(
                id = transactionBasketWithItemsId,
                date = generateRandomTime(),
                shop = Shop.generate(),
                totalCost = generateRandomLongValue(),
                items = FullItem.generateList(),
            )
        }

        fun generateList(amount: Int = 10): List<TransactionBasketWithItems> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}