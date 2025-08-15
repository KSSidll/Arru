package com.kssidll.arru.data.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.FuzzySearchSource
import com.kssidll.arru.domain.data.NameSource
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

@Entity(
    indices = [
        Index(value = ["name"])
    ],
    tableName = "ProductCategoryEntity"
)
data class ProductCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
): FuzzySearchSource, NameSource {
    @Ignore
    constructor(
        name: String,
    ): this(
        0,
        name.trim()
    )

    /**
     * Converts the [ProductCategoryEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [ProductCategoryEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [ProductCategoryEntity] csv format headers
         * @return [String] representing the [ProductCategoryEntity] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;name"
        }

        @Ignore
        fun generate(categoryId: Long = 0): ProductCategoryEntity {
            return ProductCategoryEntity(
                id = categoryId,
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductCategoryEntity> {
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
 * Converts a list of [ProductCategoryEntity] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [ProductCategoryEntity] data as list of string with csv format
 */
fun List<ProductCategoryEntity>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ProductCategoryEntity.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
    }
}