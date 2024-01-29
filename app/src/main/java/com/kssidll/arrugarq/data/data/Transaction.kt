package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.helper.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Shop::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class TransactionBasket(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val date: Long,
    @ColumnInfo(index = true) var shopId: Long?,
    val totalCost: Long,
) {
    companion object {
        @Ignore
        const val COST_DIVISOR: Long = 100

        @Ignore
        fun generate(transactionId: Long = 0): TransactionBasket {
            return TransactionBasket(
                id = transactionId,
                date = generateRandomTime(),
                shopId = generateRandomLongValue(),
                totalCost = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<TransactionBasket> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}

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
    @ColumnInfo(index = true) val itemId: Long?,
) {
    companion object {
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

data class TransactionBasketWithItems(
    val id: Long,
    val date: Long,
    val shop: Shop?,
    val totalCost: Long,
    val items: List<FullItem>,
) {
    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
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