package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.data.RankSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.*
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.FloatEntry
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
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProductVariant::class,
            parentColumns = ["id"],
            childColumns = ["variantId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var transactionId: Long,
    @ColumnInfo(index = true) var productId: Long,
    @ColumnInfo(index = true) var variantId: Long?,
    var quantity: Long,
    var price: Long,
) {
    @Ignore
    constructor(
        transactionId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long,
    ): this(
        0,
        transactionId,
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


data class Item(
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
        return ItemEntity.actualQuantity(quantity)
    }

    fun actualPrice(): Float {
        return ItemEntity.actualPrice(price)
    }

    companion object {
        fun generate(itemId: Long = 0): Item {
            return Item(
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

        fun generateList(amount: Int = 10): List<Item> {
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

        fun generateList(amount: Int = 10): List<ItemSpentByTime> {
            return List(amount) {
                generate()
            }
        }
    }

    override fun value(): Float {
        return total.toFloat()
            .div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun chartEntry(x: Int): ChartEntry {
        return FloatEntry(
            x.toFloat(),
            value()
        )
    }

    override fun startAxisLabel(): String? {
        return null
    }

    override fun topAxisLabel(): String {
        return value().formatToCurrency(dropDecimal = true)
    }

    override fun bottomAxisLabel(): String {
        return time
    }

    override fun endAxisLabel(): String? {
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

        fun generateList(amount: Int = 10): List<TransactionTotalSpentByTime> {
            return List(amount) {
                generate()
            }
        }
    }

    override fun value(): Float {
        return total.toFloat()
            .div(TransactionEntity.COST_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun chartEntry(x: Int): ChartEntry {
        return FloatEntry(
            x.toFloat(),
            value()
        )
    }

    override fun startAxisLabel(): String? {
        return null
    }

    override fun topAxisLabel(): String {
        return value().formatToCurrency(dropDecimal = true)
    }

    override fun bottomAxisLabel(): String {
        return time
    }

    override fun endAxisLabel(): String? {
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
            .div(TransactionEntity.COST_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun displayName(): String {
        return shop.name
    }

    override fun displayValue(): String {
        return value().formatToCurrency(dropDecimal = true)
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
            .div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun displayName(): String {
        return category.name
    }

    override fun displayValue(): String {
        return value()
            .formatToCurrency(dropDecimal = true)
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

        fun generateList(amount: Int = 10): List<ProductPriceByShopByTime> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}