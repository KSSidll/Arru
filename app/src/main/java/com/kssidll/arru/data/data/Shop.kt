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
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
): FuzzySearchSource, NameSource {
    /**
     * Converts the [Shop] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [Shop] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [Shop] csv format headers
         * @return [String] representing the [Shop] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;name"
        }

        @Ignore
        fun generate(shopId: Long = 0): Shop {
            return Shop(
                id = shopId,
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<Shop> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    @Ignore
    constructor(
        name: String
    ): this(
        0,
        name.trim()
    )

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

    @Ignore
    override fun name(): String {
        return name
    }
}

/**
 * Converts a list of [Shop] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [Shop] data as list of string with csv format
 */
fun List<Shop>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(Shop.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
    }
}
