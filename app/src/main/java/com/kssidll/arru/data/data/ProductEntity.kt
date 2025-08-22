package com.kssidll.arru.data.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.domain.data.interfaces.NameSource
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

@Entity(
    foreignKeys =
        [
            ForeignKey(
                entity = ProductCategoryEntity::class,
                parentColumns = ["id"],
                childColumns = ["productCategoryEntityId"],
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT,
            ),
            ForeignKey(
                entity = ProductProducerEntity::class,
                parentColumns = ["id"],
                childColumns = ["productProducerEntityId"],
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT,
            ),
        ],
    indices =
        [
            Index(
                value = ["productCategoryEntityId"],
                name = "index_ProductEntity_productCategoryEntityId",
            ),
            Index(
                value = ["productProducerEntityId"],
                name = "index_ProductEntity_productProducerEntityId",
            ),
            Index(value = ["name"], name = "index_ProductEntity_name"),
        ],
    tableName = "ProductEntity",
)
@Immutable
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var productCategoryEntityId: Long,
    var productProducerEntityId: Long?,
    val name: String,
) : FuzzySearchSource, NameSource {
    @Ignore
    constructor(
        categoryEntityId: Long,
        producerEntityId: Long?,
        name: String,
    ) : this(0, categoryEntityId, producerEntityId, name.trim())

    /**
     * Converts the [ProductEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     *
     * @return [ProductEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${productCategoryEntityId};${productProducerEntityId};${name}"
    }

    companion object {
        @Ignore const val INVALID_CATEGORY_ID: Long = Long.MIN_VALUE

        /**
         * Returns the [String] representing the [ProductEntity] csv format headers
         *
         * @return [String] representing the [ProductEntity] csv format headers
         */
        @Ignore const val CSV_HEADERS: String = "id;categoryId;producerId;name"

        @Ignore
        fun generate(productId: Long = 0): ProductEntity {
            return ProductEntity(
                id = productId,
                productCategoryEntityId = generateRandomLongValue(),
                productProducerEntityId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductEntity> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    @Ignore
    override fun fuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(query, listOf(name)).score
    }

    @Ignore
    override fun name(): String {
        return name
    }

    /** @return true if name is valid, false otherwise */
    @Ignore
    fun validName(): Boolean {
        return name.isNotBlank()
    }
}

/**
 * Converts a list of [ProductEntity] data to a list of strings with csv format
 *
 * @param includeHeaders whether to include the csv headers
 * @return [ProductEntity] data as list of string with csv format
 */
fun List<ProductEntity>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ProductEntity.CSV_HEADERS + "\n")
    }

    // Add rows
    this@asCsvList.forEach { add(it.formatAsCsvString() + "\n") }
}
