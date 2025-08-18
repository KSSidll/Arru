package com.kssidll.arru.domain.data.data

import androidx.collection.FloatFloatPair
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.generateRandomDateString
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import com.kssidll.arru.helper.generateRandomTime
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class Transaction(
    val id: Long,
    val date: Long,
    val shopId: Long?,
    val shopName: String?,
    val totalCost: Long,
    val note: String?,
    val items: ImmutableList<Item>,
) {
    companion object {
        fun generate(id: Long = 0): Transaction {
            return Transaction(
                id = id,
                date = generateRandomTime(),
                shopId = generateRandomLongValue(),
                shopName = generateRandomStringValue(),
                totalCost = generateRandomLongValue(),
                note = generateRandomStringValue(),
                items = Item.generateList().toImmutableList(),
            )
        }

        fun generateList(amount: Int = 10): List<Transaction> {
            return List(amount) { generate(it.toLong()) }
        }
    }
}

data class TransactionSpentChartData(
    @ColumnInfo("data_order") val dataOrder: Long,
    val date: String,
    val value: Long,
) : ChartSource {
    override fun value(): Float {
        return value.toFloat().div(TransactionEntity.COST_DIVISOR)
    }

    @Ignore
    override fun dataOrder(): Long {
        return dataOrder
    }

    override fun sortValue(): Long {
        return value
    }

    override fun chartEntry(): FloatFloatPair {
        return FloatFloatPair(dataOrder.toFloat(), value())
    }

    override fun chartEntry(x: Int): FloatFloatPair {
        return FloatFloatPair(x.toFloat(), value())
    }

    override fun startAxisLabel(locale: Locale): String? {
        return null
    }

    override fun topAxisLabel(locale: Locale): String? {
        return value().formatToCurrency(locale, dropDecimal = true)
    }

    override fun endAxisLabel(locale: Locale): String? {
        return null
    }

    override fun bottomAxisLabel(locale: Locale): String? {
        return date
    }

    companion object {
        fun generate(): ItemSpentChartData {
            return ItemSpentChartData(
                dataOrder = 0,
                date = generateRandomDateString(),
                value = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): ImmutableList<ItemSpentChartData> {
            return List(amount) { generate() }.toImmutableList()
        }
    }
}
