package com.kssidll.arrugarq.data.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
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
    val name: String,
) {
    constructor(
        categoryId: Long,
        name: String,
    ) : this (0, categoryId, name)
}
