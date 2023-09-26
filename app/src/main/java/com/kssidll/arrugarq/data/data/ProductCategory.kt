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
) {
    constructor(
        name: String,
    ): this(
        0,
        name
    )
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
    @Embedded val productCategory: ProductCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    ) val alternativeNames: List<ProductAltName>
): IFuzzySearchable {
    override fun getFuzzyScore(query: String): Int {
        val productNameScore = FuzzySearch.extractOne(
            query,
            listOf(productCategory.name)
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

}