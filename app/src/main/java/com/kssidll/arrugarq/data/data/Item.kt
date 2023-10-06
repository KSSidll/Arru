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
        ),
        ForeignKey(
            entity = Shop::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val productId: Long,
    @ColumnInfo(index = true) val variantId: Long?,
    @ColumnInfo(index = true) val shopId: Long?,
    val quantity: Long,
    val price: Long,
    @ColumnInfo(index = true) val date: Long,
) {
    constructor(
        productId: Long,
        variantId: Long?,
        shopId: Long?,
        quantity: Long,
        price: Long,
        date: Long
    ): this(
        0,
        productId,
        variantId,
        shopId,
        quantity,
        price,
        date
    )
}

data class ItemSpentByTime(
    val time: String,
    val total: Long,
): Chartable {
    override fun getValue(): Float {
        return total.toFloat()
            .div(100)
    }

    override fun getSortValue(): Long {
        return total
    }

    override fun chartEntry(x: Int): ChartEntry {
        return FloatEntry(
            x.toFloat(),
            getValue()
        )
    }

    override fun startAxisLabel(): String? {
        return null
    }

    override fun topAxisLabel(): String {
        return getValue().formatToCurrency(dropDecimal = true)
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
): Rankable {
    override fun getValue(): Float {
        return total.toFloat()
            .div(100)
    }

    override fun getSortValue(): Long {
        return total
    }

    override fun getDisplayName(): String {
        return shop.name
    }

    override fun getDisplayValue(): String {
        return getValue()
            .formatToCurrency(dropDecimal = true)
    }

    override fun getIdentificator(): Long {
        return shop.id
    }
}

data class ItemSpentByCategory(
    @Embedded val category: ProductCategory,
    val total: Long,
): Rankable {
    override fun getValue(): Float {
        return total.toFloat()
            .div(100)
    }

    override fun getSortValue(): Long {
        return total
    }

    override fun getDisplayName(): String {
        return category.name
    }

    override fun getDisplayValue(): String {
        return getValue()
            .formatToCurrency(dropDecimal = true)
    }

    override fun getIdentificator(): Long {
        return category.id
    }
}
