package com.kssidll.arrugarq.data.data

import androidx.room.*

@Entity(
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
) {
    constructor(
        name: String
    ): this(
        0,
        name
    )
}
