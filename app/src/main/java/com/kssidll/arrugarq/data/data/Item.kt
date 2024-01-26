package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.domain.utils.*
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
        const val PRICE_DIVISOR: Long = 100
        const val QUANTITY_DIVISOR: Long = 1000
    }
}

data class EmbeddedItem(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id",
    ) val product: Product,
    @Relation(
        parentColumn = "variantId",
        entityColumn = "id",
    ) val variant: ProductVariant?,
)

data class FullItem(
    val embeddedItem: EmbeddedItem,
    val embeddedProduct: EmbeddedProduct,
)

data class ItemSpentByTime(
    val time: String,
    val total: Long,
): ChartSource {
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
)