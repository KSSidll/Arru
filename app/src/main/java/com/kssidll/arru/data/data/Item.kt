package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomLongValue
import kotlin.math.log10

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var transactionId: Long,
    var quantity: Long,
    var price: Long,
) {
    @Ignore
    constructor(
        transactionId: Long,
        quantity: Long,
        price: Long,
    ): this(
        0,
        transactionId,
        quantity,
        price,
    )

    @Ignore
    fun actualQuantity(): Double {
        return quantity.toDouble()
            .div(QUANTITY_DIVISOR)
    }

    @Ignore
    fun actualPrice(): Double {
        return price.toDouble()
            .div(PRICE_DIVISOR)
    }

    companion object {
        @Ignore
        const val PRICE_DIVISOR: Long = 100

        @Ignore
        const val QUANTITY_DIVISOR: Long = 1000

        @Ignore
        const val INVALID_PRICE: Long = Long.MIN_VALUE

        @Ignore
        const val INVALID_QUANTITY: Long = Long.MIN_VALUE

        @Ignore
        const val INVALID_PRODUCT_ID: Long = Long.MIN_VALUE

        @Ignore
        fun actualQuantity(quantity: Long): Float {
            return quantity.toFloat()
                .div(QUANTITY_DIVISOR)
        }

        @Ignore
        fun actualPrice(price: Long): Float {
            return price.toFloat()
                .div(PRICE_DIVISOR)
        }

        @Ignore
        fun generate(itemId: Long = 0): ItemEntity {
            return ItemEntity(
                id = itemId,
                transactionId = generateRandomLongValue(),
                quantity = generateRandomLongValue(),
                price = generateRandomLongValue(),
            )
        }

        @Ignore
        fun quantityFromString(string: String): Long? {
            val rFactor = log10(QUANTITY_DIVISOR.toFloat()).toInt()

            if (!RegexHelper.isFloat(
                    string,
                    rFactor
                )
            ) {
                return null
            }

            val factor = rFactor - string.dropWhile { it.isDigit() }
                .drop(1).length

            val remainder = "".padEnd(
                factor,
                '0'
            )
            return string.filter { it.isDigit() }
                .plus(remainder)
                .toLongOrNull()
        }

        @Ignore
        fun priceFromString(string: String): Long? {
            val rFactor = log10(PRICE_DIVISOR.toFloat()).toInt()

            if (!RegexHelper.isFloat(
                    string,
                    rFactor
                )
            ) {
                return null
            }

            val factor = rFactor - string.dropWhile { it.isDigit() }
                .drop(1).length

            val remainder = "".padEnd(
                factor,
                '0'
            )
            return string.filter { it.isDigit() }
                .plus(remainder)
                .toLongOrNull()
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ItemEntity> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    /**
     * @return true if quantity is valid, false otherwise
     */
    @Ignore
    fun validQuantity(): Boolean {
        return quantity != INVALID_QUANTITY && quantity > 0
    }

    /**
     * @return true if price is valid, false otherwise
     */
    @Ignore
    fun validPrice(): Boolean {
        return price != INVALID_PRICE
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class ItemTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val itemId: Long,
    @ColumnInfo(index = true) val tagId: Long,
)