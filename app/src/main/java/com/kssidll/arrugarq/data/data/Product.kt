package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.domain.data.*
import me.xdrop.fuzzywuzzy.*

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
            value = ["producerId", "name"],
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
    @Ignore
    constructor(
        categoryId: Long,
        producerId: Long?,
        name: String,
    ): this(
        0,
        categoryId,
        producerId,
        name
    )
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
    @Ignore
    constructor(
        productId: Long,
        name: String,
    ): this(
        0,
        productId,
        name
    )
}

data class ProductWithAltNames(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    ) val alternativeNames: List<ProductAltName>
): FuzzySearchSource, NameSource {
    override fun fuzzyScore(query: String): Int {
        val productNameScore = FuzzySearch.extractOne(
            query,
            listOf(product.name)
        ).score
        val bestAlternativeNamesScore = if (alternativeNames.isNotEmpty()) {
            FuzzySearch.extractOne(
                query,
                alternativeNames.map { it.name }).score
        } else -1

        return maxOf(
            productNameScore,
            bestAlternativeNamesScore
        )
    }

    override fun name(): String {
        return product.name
    }

}

data class EmbeddedProduct(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id",
    ) val category: ProductCategory,
    @Relation(
        parentColumn = "producerId",
        entityColumn = "id",
    ) val producer: ProductProducer?,
)

