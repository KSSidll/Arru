package com.kssidll.arru.data.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.domain.data.interfaces.NameSource
import com.kssidll.arru.domain.data.interfaces.RankSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import java.util.Locale
import me.xdrop.fuzzywuzzy.FuzzySearch

@Entity(
    indices = [Index(value = ["name"], name = "index_ProductCategoryEntity_name")],
    tableName = "ProductCategoryEntity",
)
@Immutable
data class ProductCategoryEntity(@PrimaryKey(autoGenerate = true) val id: Long, val name: String) :
    FuzzySearchSource, NameSource {
    @Ignore constructor(name: String) : this(0, name.trim())

    /**
     * Converts the [ProductCategoryEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     *
     * @return [ProductCategoryEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [ProductCategoryEntity] csv format headers
         *
         * @return [String] representing the [ProductCategoryEntity] csv format headers
         */
        @Ignore const val CSV_HEADERS: String = "id;name"

        @Ignore
        fun generate(categoryId: Long = 0): ProductCategoryEntity {
            return ProductCategoryEntity(id = categoryId, name = generateRandomStringValue())
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductCategoryEntity> {
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
 * Converts a list of [ProductCategoryEntity] data to a list of strings with csv format
 *
 * @param includeHeaders whether to include the csv headers
 * @return [ProductCategoryEntity] data as list of string with csv format
 */
fun List<ProductCategoryEntity>.asCsvList(includeHeaders: Boolean = false): List<String> =
    buildList {
        // Add headers
        if (includeHeaders) {
            add(ProductCategoryEntity.CSV_HEADERS + "\n")
        }

        // Add rows
        this@asCsvList.forEach { add(it.formatAsCsvString() + "\n") }
    }

@Immutable
data class TotalSpentByCategory(@Embedded val category: ProductCategoryEntity, val total: Long) :
    RankSource {
    companion object {
        @Ignore
        fun generate(categoryId: Long = 0): TotalSpentByCategory {
            return TotalSpentByCategory(
                category = ProductCategoryEntity.generate(categoryId),
                total = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<TotalSpentByCategory> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    @Ignore
    override fun value(): Float {
        return total.toFloat().div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
    }

    @Ignore
    override fun sortValue(): Long {
        return total
    }

    @Ignore
    override fun displayName(): String {
        return category.name
    }

    @Ignore
    override fun displayValue(locale: Locale): String {
        return value().formatToCurrency(locale, dropDecimal = true)
    }

    @Ignore
    override fun identificator(): Long {
        return category.id
    }
}
