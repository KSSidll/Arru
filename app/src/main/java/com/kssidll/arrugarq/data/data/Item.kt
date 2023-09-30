package com.kssidll.arrugarq.data.data

import androidx.room.*
import com.kssidll.arrugarq.domain.chart.*
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
    val date: Long,
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
): IChartable {
    override fun chartEntry(x: Int): ChartEntry {
        return FloatEntry(
            x.toFloat(),
            total.toFloat()
        )
    }

    override fun topAxisLabel(): String {
        return total.div(100)
            .toString()
    }

    override fun bottomAxisLabel(): String {
        return time
    }

}
