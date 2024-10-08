package com.kssidll.arru.data.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.kssidll.arru.domain.data.FuzzySearchSource
import com.kssidll.arru.domain.data.NameSource
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

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
        name.trim()
    )

    /**
     * Converts the [ProductCategory] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [ProductCategory] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [ProductCategory] csv format headers
         * @return [String] representing the [ProductCategory] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;name"
        }

        @Ignore
        fun generate(categoryId: Long = 0): ProductCategory {
            return ProductCategory(
                id = categoryId,
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductCategory> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    @Ignore
    override fun fuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(
            query,
            listOf(name)
        ).score
    }

    /**
     * @return true if name is valid, false otherwise
     */
    @Ignore
    fun validName(): Boolean {
        return name.isNotBlank()
    }
}

/**
 * Converts a list of [ProductCategory] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [ProductCategory] data as list of string with csv format
 */
fun List<ProductCategory>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ProductCategory.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
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
    constructor(
        categoryId: Long,
        name: String
    ): this(
        0,
        categoryId,
        name
    )

    constructor(
        category: ProductCategory,
        name: String
    ): this(
        category.id,
        name.trim()
    )

    companion object {
        @Ignore
        fun generate(categoryAltNameId: Long = 0): ProductCategoryAltName {
            return ProductCategoryAltName(
                id = categoryAltNameId,
                productCategoryId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductCategoryAltName> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    /**
     * @return true if name is valid, false otherwise
     */
    @Ignore
    fun validName(): Boolean {
        return name.isNotBlank()
    }
}

data class ProductCategoryWithAltNames(
    @Embedded val category: ProductCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "productCategoryId"
    ) val alternativeNames: List<ProductCategoryAltName>
): FuzzySearchSource, NameSource {
    companion object {
        fun generate(categoryId: Long = 0): ProductCategoryWithAltNames {
            return ProductCategoryWithAltNames(
                category = ProductCategory.generate(categoryId),
                alternativeNames = ProductCategoryAltName.generateList(),
            )
        }

        fun generateList(amount: Int = 10): List<ProductCategoryWithAltNames> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

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