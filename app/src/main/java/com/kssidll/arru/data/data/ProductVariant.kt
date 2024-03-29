package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.helper.*
import me.xdrop.fuzzywuzzy.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
    ]
)
data class ProductVariant(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var productId: Long,
    var name: String,
): FuzzySearchSource {

    @Ignore
    constructor(
        productId: Long,
        name: String,
    ): this(
        0,
        productId,
        name.trim()
    )

    companion object {
        @Ignore
        fun generate(variantId: Long = 0): ProductVariant {
            return ProductVariant(
                id = variantId,
                productId = generateRandomLongValue(),
                name = generateRandomStringValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ProductVariant> {
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

    /**
     * @return true if name is valid, false otherwise
     */
    @Ignore
    fun validName(): Boolean {
        return name.isNotBlank()
    }
}