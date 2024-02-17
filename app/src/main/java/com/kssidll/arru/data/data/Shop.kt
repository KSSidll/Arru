package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.helper.*
import me.xdrop.fuzzywuzzy.*

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
    companion object {
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