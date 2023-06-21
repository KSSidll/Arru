package com.kssidll.arrugarq.data.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = Shop::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val productId: Long,
    @ColumnInfo(index = true) val shopId: Long?,
    val quantity: Long,
    val unitMeasure: Long?,
    val price: Long,
    val date: Long,
) {
    constructor(
        productId: Long,
        shopId: Long?,
        quantity: Long,
        unitMeasure: Long?,
        price: Long,
        date: Long
    ) : this (0, productId, shopId, quantity, unitMeasure, price, date)
}
