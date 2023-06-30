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
    ]
)
data class ProductVariant(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val productId: Long,
    val name: String,
) {
    constructor(
        productId: Long,
        name: String,
    ) : this (0, productId, name)
}