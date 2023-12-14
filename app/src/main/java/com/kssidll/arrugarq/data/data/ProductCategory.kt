package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.domain.data.*
import me.xdrop.fuzzywuzzy.*

@Entity(
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class ProductCategory(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
): FuzzySearchSource {
    @Ignore
    constructor(
        name: String,
    ): this(
        0,
        name
    )

    override fun fuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(
            query,
            listOf(name)
        ).score
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductCategory::class,
            parentColumns = ["id"],
            childColumns = ["productCategoryId"],
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
data class ProductCategoryAltName(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val productCategoryId: Long,
    val name: String,
) {
    @Ignore
    constructor(
        productCategoryId: Long,
        name: String,
    ): this(
        0,
        productCategoryId,
        name
    )
}

data class ProductCategoryWithAltNames(
    @Embedded val category: ProductCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    ) val alternativeNames: List<ProductAltName>
): FuzzySearchSource, NameSource {
    override fun fuzzyScore(query: String): Int {
        val productNameScore = FuzzySearch.extractOne(
            query,
            listOf(category.name)
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
        return category.name
    }

}