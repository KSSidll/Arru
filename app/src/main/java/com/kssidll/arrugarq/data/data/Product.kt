package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.helper.*
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

    companion object {
        @Ignore
        const val INVALID_CATEGORY_ID: Long = Long.MIN_VALUE

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
        @Suppress("MemberVisibilityCanBePrivate")
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