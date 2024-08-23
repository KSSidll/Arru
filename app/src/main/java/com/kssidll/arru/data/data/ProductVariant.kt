package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.domain.data.FuzzySearchSource
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

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
    @ColumnInfo(index = true) var productId: Long,
    var name: String,
): FuzzySearchSource {

    @Ignore
    constructor(
        productId: Long,
        name: String,
    ): this(
        0,
        productId,
        name.trim()
    )

    /**
     * Converts the [ProductVariant] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [ProductVariant] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${productId};${name}"
    }


    companion object {
        /**
         * Returns the [String] representing the [ProductVariant] csv format headers
         * @return [String] representing the [ProductVariant] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;productId;name"
        }

        @Ignore
        fun generate(variantId: Long = 0): ProductVariant {
            return ProductVariant(
                id = variantId,
                productId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductVariant> {
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
 * Converts a list of [ProductVariant] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [ProductVariant] data as list of string with csv format
 */
fun List<ProductVariant>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ProductVariant.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
    }
}
