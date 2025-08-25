package com.kssidll.arru.data.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.domain.data.interfaces.NameSource
import com.kssidll.arru.helper.generateRandomStringValue
import me.xdrop.fuzzywuzzy.FuzzySearch

@Entity(
    indices = [Index(value = ["name"], name = "index_ProductProducerEntity_name")],
    tableName = "ProductProducerEntity",
)
@Immutable
data class ProductProducerEntity(@PrimaryKey(autoGenerate = true) val id: Long, val name: String) :
    FuzzySearchSource, NameSource {
    /**
     * Converts the [ProductProducerEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     *
     * @return [ProductProducerEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${name}"
    }

    companion object {
        /**
         * Returns the [String] representing the [ProductProducerEntity] csv format headers
         *
         * @return [String] representing the [ProductProducerEntity] csv format headers
         */
        @Ignore const val CSV_HEADERS: String = "id;name"

        @Ignore
        fun generate(producerId: Long = 0): ProductProducerEntity {
            return ProductProducerEntity(id = producerId, name = generateRandomStringValue())
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductProducerEntity> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    @Ignore constructor(name: String) : this(0, name.trim())

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
 * Converts a list of [ProductProducerEntity] data to a list of strings with csv format
 *
 * @param includeHeaders whether to include the csv headers
 * @return [ProductProducerEntity] data as list of string with csv format
 */
fun List<ProductProducerEntity>.asCsvList(includeHeaders: Boolean = false): List<String> =
    buildList {
        // Add headers
        if (includeHeaders) {
            add(ProductProducerEntity.CSV_HEADERS + "\n")
        }

        // Add rows
        this@asCsvList.forEach { add(it.formatAsCsvString() + "\n") }
    }
