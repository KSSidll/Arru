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
    indices = [Index(value = ["name"], name = "index_ShopEntity_name")],
    tableName = "ShopEntity",
)
@Immutable
data class ShopEntity(@PrimaryKey(autoGenerate = true) val id: Long, val name: String) :
    FuzzySearchSource, NameSource {
    /**
     * Converts the [ShopEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     *
     * @return [ShopEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [ShopEntity] csv format headers
         *
         * @return [String] representing the [ShopEntity] csv format headers
         */
        @Ignore const val CSV_HEADERS: String = "id;name"

        @Ignore
        fun generate(shopId: Long = 0): ShopEntity {
            return ShopEntity(id = shopId, name = generateRandomStringValue())
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ShopEntity> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    @Ignore constructor(name: String) : this(0, name.trim())

    @Ignore
    override fun fuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(query, listOf(name)).score
    }

    /** @return true if name is valid, false otherwise */
    @Ignore
    fun validName(): Boolean {
        return name.isNotBlank()
    }

    @Ignore
    override fun name(): String {
        return name
    }
}

/**
 * Converts a list of [ShopEntity] data to a list of strings with csv format
 *
 * @param includeHeaders whether to include the csv headers
 * @return [ShopEntity] data as list of string with csv format
 */
fun List<ShopEntity>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ShopEntity.CSV_HEADERS + "\n")
    }

    // Add rows
    this@asCsvList.forEach { add(it.formatAsCsvString() + "\n") }
}

@Immutable
data class TotalSpentByShop(@Embedded val shop: ShopEntity, val total: Long) : RankSource {
    companion object {
        @Ignore
        fun generate(shopId: Long = 0): TotalSpentByShop {
            return TotalSpentByShop(
                shop = ShopEntity.generate(shopId),
                total = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<TotalSpentByShop> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    @Ignore
    override fun value(): Float {
        return total.toFloat().div(TransactionEntity.COST_DIVISOR)
    }

    @Ignore
    override fun sortValue(): Long {
        return total
    }

    @Ignore
    override fun displayName(): String {
        return shop.name
    }

    @Ignore
    override fun displayValue(locale: Locale): String {
        return value().formatToCurrency(locale, dropDecimal = true)
    }

    @Ignore
    override fun identificator(): Long {
        return shop.id
    }
}
