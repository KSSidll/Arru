package com.kssidll.arru.data.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomLongValue
import kotlin.math.log10

@Entity(
    foreignKeys =
        [
            ForeignKey(
                entity = TransactionEntity::class,
                parentColumns = ["id"],
                childColumns = ["transactionEntityId"],
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT,
            ),
            ForeignKey(
                entity = ProductEntity::class,
                parentColumns = ["id"],
                childColumns = ["productEntityId"],
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT,
            ),
            ForeignKey(
                entity = ProductVariantEntity::class,
                parentColumns = ["id"],
                childColumns = ["productVariantEntityId"],
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT,
            ),
        ],
    indices =
        [
            Index(value = ["transactionEntityId"], name = "index_ItemEntity_transactionEntityId"),
            Index(value = ["productEntityId"], name = "index_ItemEntity_productEntityId"),
            Index(
                value = ["productVariantEntityId"],
                name = "index_ItemEntity_productVariantEntityId",
            ),
        ],
    tableName = "ItemEntity",
)
@Immutable
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val transactionEntityId: Long,
    val productEntityId: Long,
    val productVariantEntityId: Long?,
    val quantity: Long,
    val price: Long,
) {
    @Ignore
    constructor(
        transactionEntityId: Long,
        productEntityId: Long,
        productVariantEntityId: Long?,
        quantity: Long,
        price: Long,
    ) : this(0, transactionEntityId, productEntityId, productVariantEntityId, quantity, price)

    @Ignore
    fun actualQuantity(): Double {
        return quantity.toDouble().div(QUANTITY_DIVISOR)
    }

    @Ignore
    fun actualPrice(): Double {
        return price.toDouble().div(PRICE_DIVISOR)
    }

    /**
     * Converts the [ItemEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     *
     * @return [ItemEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${transactionEntityId};${productEntityId};${productVariantEntityId};${actualQuantity()};${actualPrice()}"
    }

    companion object {
        @Ignore const val PRICE_DIVISOR: Long = 100

        @Ignore const val QUANTITY_DIVISOR: Long = 1000

        @Ignore const val INVALID_PRICE: Long = Long.MIN_VALUE

        @Ignore const val INVALID_QUANTITY: Long = Long.MIN_VALUE

        @Ignore
        fun actualQuantity(quantity: Long): Float {
            return quantity.toFloat().div(QUANTITY_DIVISOR)
        }

        @Ignore
        fun actualPrice(price: Long): Float {
            return price.toFloat().div(PRICE_DIVISOR)
        }

        /**
         * Returns the [String] representing the [ItemEntity] csv format headers
         *
         * @return [String] representing the [ItemEntity] csv format headers
         */
        @Ignore
        const val CSV_HEADERS: String = "id;transactionBasketId;productId;variantId;quantity;price"

        @Ignore
        fun generate(itemId: Long = 0): ItemEntity {
            return ItemEntity(
                id = itemId,
                transactionEntityId = generateRandomLongValue(),
                productEntityId = generateRandomLongValue(),
                productVariantEntityId = generateRandomLongValue(),
                quantity = generateRandomLongValue(),
                price = generateRandomLongValue(),
            )
        }

        @Ignore
        fun quantityFromString(string: String): Long? {
            val rFactor = log10(QUANTITY_DIVISOR.toFloat()).toInt()

            if (!RegexHelper.isFloat(string, rFactor)) {
                return null
            }

            val factor = rFactor - string.dropWhile { it.isDigit() }.drop(1).length

            val remainder = "".padEnd(factor, '0')
            return string.filter { it.isDigit() }.plus(remainder).toLongOrNull()
        }

        @Ignore
        fun priceFromString(string: String): Long? {
            val rFactor = log10(PRICE_DIVISOR.toFloat()).toInt()

            if (!RegexHelper.isFloat(string, rFactor)) {
                return null
            }

            val factor = rFactor - string.dropWhile { it.isDigit() }.drop(1).length

            val remainder = "".padEnd(factor, '0')
            return string.filter { it.isDigit() }.plus(remainder).toLongOrNull()
        }

        @Ignore
        fun generateList(amount: Int = 10): List<ItemEntity> {
            return List(amount) { generate(it.toLong()) }
        }
    }

    /** @return true if quantity is valid, false otherwise */
    @Ignore
    fun validQuantity(): Boolean {
        return quantity != INVALID_QUANTITY && quantity > 0
    }

    /** @return true if price is valid, false otherwise */
    @Ignore
    fun validPrice(): Boolean {
        return price != INVALID_PRICE
    }
}

/**
 * Converts a list of [ItemEntity] data to a list of strings with csv format
 *
 * @param includeHeaders whether to include the csv headers
 * @return [ItemEntity] data as list of string with csv format
 */
fun List<ItemEntity>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(ItemEntity.CSV_HEADERS + "\n")
    }

    // Add rows
    this@asCsvList.forEach { add(it.formatAsCsvString() + "\n") }
}
