package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomDateString
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomTime
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlin.math.log10

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Shop::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var date: Long,
    @ColumnInfo(index = true) var shopId: Long?,
    var totalCost: Long,
) {
    @Ignore
    constructor(
        date: Long,
        totalCost: Long,
        shopId: Long?
    ): this(
        0,
        date,
        shopId,
        totalCost
    )

    @Ignore
    fun actualTotalCost(): Float {
        return actualTotalCost(totalCost)
    }

    companion object {
        @Ignore
        const val COST_DIVISOR: Long = 100

        @Ignore
        const val INVALID_DATE: Long = Long.MIN_VALUE

        @Ignore
        const val INVALID_TOTAL_COST: Long = Long.MIN_VALUE

        @Ignore
        fun actualTotalCost(cost: Long): Float {
            return cost.toFloat()
                .div(COST_DIVISOR)
        }

        @Ignore
        fun totalCostFromString(string: String): Long? {
            val rFactor = log10(COST_DIVISOR.toFloat()).toInt()

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
        fun generate(transactionId: Long = 0): TransactionEntity {
            return TransactionEntity(
                id = transactionId,
                date = generateRandomTime(),
                shopId = generateRandomLongValue(),
                totalCost = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<TransactionEntity> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }

    /**
     * @return true if date is valid, false otherwise
     */
    @Ignore
    fun validDate(): Boolean {
        return date != INVALID_DATE && date > 0
    }

    /**
     * @return true if total cost is valid, false otherwise
     */
    @Ignore
    fun validTotalCost(): Boolean {
        return totalCost != INVALID_TOTAL_COST
    }
}

data class Transaction(
    val id: Long,
    val date: Long,
    val shop: Shop?,
    val totalCost: Long,
    val items: List<Item>,
) {
    companion object {
        fun generate(transactionBasketWithItemsId: Long = 0): Transaction {
            return Transaction(
                id = transactionBasketWithItemsId,
                date = generateRandomTime(),
                shop = Shop.generate(),
                totalCost = generateRandomLongValue(),
                items = Item.generateList(),
            )
        }

        fun generateList(amount: Int = 10): List<Transaction> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}

data class TransactionSpentByTime(
    val time: String,
    val total: Long,
): ChartSource {
    companion object {
        fun generate(): TransactionSpentByTime {
            return TransactionSpentByTime(
                time = generateRandomDateString(),
                total = generateRandomLongValue(),
            )
        }

        fun generateList(amount: Int = 10): List<TransactionSpentByTime> {
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
