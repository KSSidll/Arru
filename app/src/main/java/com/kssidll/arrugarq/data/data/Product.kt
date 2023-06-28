package com.kssidll.arrugarq.data.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ProductProducer::class,
            parentColumns = ["id"],
            childColumns = ["producerId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val categoryId: Long,
    @ColumnInfo(index = true) val producerId: Long?,
    val name: String,
) {
    constructor(
        categoryId: Long,
        producerId: Long?,
        name: String,
    ) : this (0, categoryId, producerId, name)
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class ProductAltName(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val productId: Long,
    val name: String,
) {
    constructor(
        productId: Long,
        name: String,
    ) : this(0, productId, name)
}

data class ProductWithAltNames(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    ) val alternativeNames: List<ProductAltName>
)