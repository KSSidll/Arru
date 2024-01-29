package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.helper.*
import com.patrykandpatrick.vico.core.entry.*

@Entity(
    foreignKeys = [
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
    @ColumnInfo(index = true) var productId: Long,
    @ColumnInfo(index = true) val variantId: Long?,
    val quantity: Long,
    val price: Long,
) {
    @Ignore
    constructor(
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long,
    ): this(
        0,
        productId,
        variantId,
        quantity,
        price,
    )

    @Ignore
    constructor(
        id: Long = 0,
        productId: Long,
        variantId: Long?,
        actualQuantity: Double,
        actualPrice: Double,
    ): this(
        id,
        productId,
        variantId,
        actualQuantity.times(QUANTITY_DIVISOR)
            .toLong(),
        actualPrice.times(PRICE_DIVISOR)
            .toLong(),
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
        fun actualQuantity(quantity: Long): Double {
            return quantity.toDouble()
                .div(QUANTITY_DIVISOR)
        }

        @Ignore
        fun actualPrice(price: Long): Double {
            return price.toDouble()
                .div(PRICE_DIVISOR)
        }

        @Ignore
        fun generate(itemId: Long = 0): Item {
            return Item(
                id = itemId,
                productId = generateRandomLongValue(),
                variantId = generateRandomLongValue(),
                quantity = generateRandomLongValue(),
                price = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<Item> {
            return List(amount) {
                generate(it.toLong())
            }
        }
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
    fun actualQuantity(): Double {
        return Item.actualQuantity(quantity)
    }

    fun actualPrice(): Double {
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
        @Suppress("MemberVisibilityCanBePrivate")
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

    override fun value(): Double {
        return total.toDouble()
            .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
    }

    override fun sortValue(): Long {
        return total
    }

    override fun chartEntry(x: Int): ChartEntry {
        return FloatEntry(
            x.toFloat(),
            value().toFloat()
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

data class ItemSpentByShop(
    @Embedded val shop: Shop,
    val total: Long,
): RankSource {
    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        fun generate(shopId: Long = 0): ItemSpentByShop {
            return ItemSpentByShop(
                shop = Shop.generate(shopId),
                total = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): List<ItemSpentByShop> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    override fun value(): Double {
        return total.toDouble()
            .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
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
        @Suppress("MemberVisibilityCanBePrivate")
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

    override fun value(): Double {
        return total.toDouble()
            .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
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
        @Suppress("MemberVisibilityCanBePrivate")
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