package com.kssidll.arrugarq.data.data

import androidx.room.*

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
    @ColumnInfo(index = true) val shopId: Long?,
    val totalCost: Long,
) {
    companion object {
        const val COST_DIVISOR: Long = 100
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
)

data class TransactionBasketWithItems(
    val id: Long,
    val date: Long,
    val shop: Shop?,
    val totalCost: Long,
    val items: List<FullItem>,
)