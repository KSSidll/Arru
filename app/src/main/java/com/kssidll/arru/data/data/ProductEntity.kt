package com.kssidll.arru.data.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.FuzzySearchSource
import com.kssidll.arru.domain.data.NameSource
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryEntityId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ProductProducerEntity::class,
            parentColumns = ["id"],
            childColumns = ["producerEntityId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(value = ["categoryEntityId"]),
        Index(value = ["producerEntityId"]),
        Index(value = ["name"]),
    ],
    tableName = "ProductEntity"
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var categoryEntityId: Long,
    var producerEntityId: Long?,
    val name: String,
): FuzzySearchSource, NameSource {
    @Ignore
    constructor(
        categoryEntityId: Long,
        producerEntityId: Long?,
        name: String,
    ): this(
        0,
        categoryEntityId,
        producerEntityId,
        name.trim()
    )

    /**
     * Converts the [ProductEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [ProductEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${categoryEntityId};${producerEntityId};${name}"
    }

    companion object {
        @Ignore
        const val INVALID_CATEGORY_ID: Long = Long.MIN_VALUE

        /**
         * Returns the [String] representing the [ProductEntity] csv format headers
         * @return [String] representing the [ProductEntity] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;categoryId;producerId;name"
        }

        @Ignore
        fun generate(productId: Long = 0): ProductEntity {
            return ProductEntity(
                id = productId,
                categoryEntityId = generateRandomLongValue(),
                producerEntityId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductEntity> {
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

    @Ignore
    override fun name(): String {
        return name
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
 * Converts a list of [ProductEntity] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [ProductEntity] data as list of string with csv format
 */
fun List<ProductEntity>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ProductEntity.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
    }
}