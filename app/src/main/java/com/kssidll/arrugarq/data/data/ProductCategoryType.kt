package com.kssidll.arrugarq.data.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class ProductCategoryType(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
) {
    constructor(
        name: String
    ) : this (0, name)
}
