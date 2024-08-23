package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.domain.data.FuzzySearchSource
import com.kssidll.arru.domain.data.NameSource
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

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
    @ColumnInfo(index = true) var categoryId: Long,
    @ColumnInfo(index = true) var producerId: Long?,
    val name: String,
): FuzzySearchSource {
    @Ignore
    constructor(
        categoryId: Long,
        producerId: Long?,
        name: String,
    ): this(
        0,
        categoryId,
        producerId,
        name.trim()
    )

    /**
     * Converts the [Product] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [Product] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${categoryId};${producerId};${name}"
    }

    companion object {
        @Ignore
        const val INVALID_CATEGORY_ID: Long = Long.MIN_VALUE

        /**
         * Returns the [String] representing the [Product] csv format headers
         * @return [String] representing the [Product] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;categoryId;producerId"
        }

        @Ignore
        fun generate(productId: Long = 0): Product {
            return Product(
                id = productId,
                categoryId = generateRandomLongValue(),
                producerId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<Product> {
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
 * Converts a list of [Product] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [Product] data as list of string with csv format
 */
fun List<Product>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(Product.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
    }
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
        product: Product,
        name: String
    ): this(
        id = 0,
        productId = product.id,
        name = name.trim()
    )

    companion object {
        @Ignore
        fun generate(productWithAltNameId: Long = 0): ProductAltName {
            return ProductAltName(
                id = productWithAltNameId,
                productId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductAltName> {
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

data class ProductWithAltNames(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    ) val alternativeNames: List<ProductAltName>
): FuzzySearchSource, NameSource {
    companion object {
        fun generate(productId: Long = 0): ProductWithAltNames {
            return ProductWithAltNames(
                product = Product.generate(productId),
                alternativeNames = ProductAltName.generateList(),
            )
        }

        fun generateList(amount: Int = 10): List<ProductWithAltNames> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

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