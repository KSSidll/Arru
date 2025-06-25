package com.kssidll.arru.data.data

import androidx.collection.FloatFloatPair
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.data.RankSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomDate
import com.kssidll.arru.helper.generateRandomDateString
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.util.Locale
import kotlin.math.log10

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TransactionBasket::class,
            parentColumns = ["id"],
            childColumns = ["transactionBasketId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ProductVariant::class,
            parentColumns = ["id"],
            childColumns = ["variantId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var transactionBasketId: Long,
    @ColumnInfo(index = true) var productId: Long,
    @ColumnInfo(index = true) var variantId: Long?,
    var quantity: Long,
    var price: Long,
) {
    @Ignore
    constructor(
        transactionBasketId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long,
    ): this(
        0,
        transactionBasketId,
        productId,
        variantId,
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

    /**
     * Converts the [Item] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [Item] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${transactionBasketId};${productId};${variantId};${actualQuantity()};${actualPrice()}"
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

        /**
         * Returns the [String] representing the [Item] csv format headers
         * @return [String] representing the [Item] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;transactionBasketId;productId;variantId;quantity;price"
        }

        @Ignore
        fun generate(itemId: Long = 0): Item {
            return Item(
                id = itemId,
                transactionBasketId = generateRandomLongValue(),
                productId = generateRandomLongValue(),
                variantId = generateRandomLongValue(),
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
        fun generateList(amount: Int = 10): List<Item> {
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

/**
 * Converts a list of [Item] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [Item] data as list of string with csv format
 */
fun List<Item>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(Item.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
    }
}

data class FullItem(
    val id: Long,
    val quantity: Long,
    val price: Long,
    val product: Product,
    val variant: ProductVariant?,
    val category: ProductCategory,
    val producer: ProductProducer?,
    val date: Long,
    val shop: Shop?,
) {
    fun actualQuantity(): Float {
        return Item.actualQuantity(quantity)
    }

    fun actualPrice(): Float {
        return Item.actualPrice(price)
    }

    companion object {
        fun generate(itemId: Long = 0): FullItem {
            return FullItem(
                id = itemId,
                quantity = generateRandomLongValue(),
                price = generateRandomLongValue(),
                product = Product.generate(),
                variant = ProductVariant.generate(),
                category = ProductCategory.generate(),
                producer = ProductProducer.generate(),
                date = generateRandomDate().time,
                shop = Shop.generate(),
            )
        }

        fun generateList(amount: Int = 10): List<FullItem> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}


data class ItemSpentByTime(
    val time: String,
    val total: Long,
): ChartSource {
    companion object {
        fun generate(): ItemSpentByTime {
            return ItemSpentByTime(
                time = generateRandomDateString(),
                total = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): ImmutableList<ItemSpentByTime> {
            return List(amount) {
                generate()
            }.toImmutableList()
        }
    }

    override fun value(): Float {
        return total.toFloat()
            .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun chartEntry(x: Int): FloatFloatPair {
        return FloatFloatPair(
            x.toFloat(),
            value()
        )
    }

    override fun startAxisLabel(locale: Locale): String? {
        return null
    }

    override fun topAxisLabel(locale: Locale): String? {
        return value().formatToCurrency(locale, dropDecimal = true)
    }

    override fun bottomAxisLabel(locale: Locale): String? {
        return time
    }

    override fun endAxisLabel(locale: Locale): String? {
        return null
    }
}

data class TransactionTotalSpentByTime(
    val time: String,
    val total: Long,
): ChartSource {
    companion object {
        fun generate(): TransactionTotalSpentByTime {
            return TransactionTotalSpentByTime(
                time = generateRandomDateString(),
                total = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): ImmutableList<TransactionTotalSpentByTime> {
            return List(amount) {
                generate()
            }.toImmutableList()
        }
    }

    override fun value(): Float {
        return total.toFloat()
            .div(TransactionBasket.COST_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun chartEntry(x: Int): FloatFloatPair {
        return FloatFloatPair(
            x.toFloat(),
            value()
        )
    }

    override fun startAxisLabel(locale: Locale): String? {
        return null
    }

    override fun topAxisLabel(locale: Locale): String? {
        return value().formatToCurrency(locale, dropDecimal = true)
    }

    override fun bottomAxisLabel(locale: Locale): String? {
        return time
    }

    override fun endAxisLabel(locale: Locale): String? {
        return null
    }
}

data class TransactionTotalSpentByShop(
    @Embedded val shop: Shop,
    val total: Long,
): RankSource {
    companion object {
        fun generate(shopId: Long = 0): TransactionTotalSpentByShop {
            return TransactionTotalSpentByShop(
                shop = Shop.generate(shopId),
                total = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): List<TransactionTotalSpentByShop> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    override fun value(): Float {
        return total.toFloat()
            .div(TransactionBasket.COST_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun displayName(): String {
        return shop.name
    }

    override fun displayValue(locale: Locale): String {
        return value().formatToCurrency(locale, dropDecimal = true)
    }

    override fun identificator(): Long {
        return shop.id
    }
}

data class ItemSpentByCategory(
    @Embedded val category: ProductCategory,
    val total: Long,
): RankSource {
    companion object {
        fun generate(categoryId: Long = 0): ItemSpentByCategory {
            return ItemSpentByCategory(
                category = ProductCategory.generate(categoryId),
                total = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): List<ItemSpentByCategory> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    override fun value(): Float {
        return total.toFloat()
            .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun displayName(): String {
        return category.name
    }

    override fun displayValue(locale: Locale): String {
        return value()
            .formatToCurrency(locale, dropDecimal = true)
    }

    override fun identificator(): Long {
        return category.id
    }
}

data class ProductPriceByShopByTime(
    @Embedded val product: Product?,
    val variantName: String?,
    val producerName: String?,
    val price: Long?,
    val shopName: String?,
    val time: String,
) {
    companion object {
        fun generate(productId: Long = 0): ProductPriceByShopByTime {
            return ProductPriceByShopByTime(
                product = Product.generate(productId),
                variantName = generateRandomStringValue(),
                producerName = generateRandomStringValue(),
                price = generateRandomLongValue(),
                shopName = generateRandomStringValue(),
                time = generateRandomDateString(),
            )
        }

        fun generateList(amount: Int = 10): ImmutableList<ProductPriceByShopByTime> {
            return List(amount) {
                generate(it.toLong())
            }.toImmutableList()
        }
    }
}