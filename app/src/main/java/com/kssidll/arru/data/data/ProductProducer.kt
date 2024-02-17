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
data class ProductProducer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var name: String,
): FuzzySearchSource, NameSource {
    companion object {
        @Ignore
        fun generate(producerId: Long = 0): ProductProducer {
            return ProductProducer(
                id = producerId,
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductProducer> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    @Ignore
    constructor(
        name: String,
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
