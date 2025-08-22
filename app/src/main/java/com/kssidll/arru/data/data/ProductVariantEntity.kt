package com.kssidll.arru.data.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

@Entity(
    foreignKeys =
        [
            ForeignKey(
                entity = ProductEntity::class,
                parentColumns = ["id"],
                childColumns = ["productEntityId"],
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT,
            )
        ],
    indices =
        [
            Index(value = ["productEntityId"], name = "index_ProductVariantEntity_productEntityId"),
            Index(value = ["name"], name = "index_ProductVariantEntity_name"),
        ],
    tableName = "ProductVariantEntity",
)
@Immutable
data class ProductVariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var productEntityId: Long?,
    var name: String,
) : FuzzySearchSource {

    @Ignore
    constructor(productEntityId: Long?, name: String) : this(0, productEntityId, name.trim())

    /**
     * Converts the [ProductVariantEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     *
     * @return [ProductVariantEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${productEntityId};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [ProductVariantEntity] csv format headers
         *
         * @return [String] representing the [ProductVariantEntity] csv format headers
         */
        @Ignore const val CSV_HEADERS: String = "id;productId;name"

        @Ignore
        fun generate(variantId: Long = 0): ProductVariantEntity {
            return ProductVariantEntity(
                id = variantId,
                productEntityId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductVariantEntity> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    @Ignore
    override fun fuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(query, listOf(name)).score
    }

    /** @return true if name is valid, false otherwise */
    @Ignore
    fun validName(): Boolean {
        return name.isNotBlank()
    }
}

/**
 * Converts a list of [ProductVariantEntity] data to a list of strings with csv format
 *
 * @param includeHeaders whether to include the csv headers
 * @return [ProductVariantEntity] data as list of string with csv format
 */
fun List<ProductVariantEntity>.asCsvList(includeHeaders: Boolean = false): List<String> =
    buildList {
        // Add headers
        if (includeHeaders) {
            add(ProductVariantEntity.CSV_HEADERS + "\n")
        }

        // Add rows
        this@asCsvList.forEach { add(it.formatAsCsvString() + "\n") }
    }
