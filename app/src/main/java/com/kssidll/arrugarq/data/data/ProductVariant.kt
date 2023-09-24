package com.kssidll.arrugarq.data.data

import androidx.room.*
import me.xdrop.fuzzywuzzy.*

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
) : IFuzzySearchable {
    constructor(
        productId: Long,
        name: String,
    ): this(
        0,
        productId,
        name
    )

    override fun getFuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(query, listOf(name)).score
    }

}