package com.kssidll.arru.data.data

import androidx.collection.FloatFloatPair
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomDateString
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomTime
import java.util.Locale
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
data class TransactionBasket(
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

    /**
     * Converts the [TransactionBasket] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [TransactionBasket] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${date};${shopId};${actualTotalCost()}"
    }

    companion object {
        @Ignore
        const val COST_DIVISOR: Long = 100

        @Ignore
        const val INVALID_DATE: Long = Long.MIN_VALUE

        @Ignore
        const val INVALID_TOTAL_COST: Long = Long.MIN_VALUE

        /**
         * Returns the [String] representing the [TransactionBasket] csv format headers
         * @return [String] representing the [TransactionBasket] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;date;shopId;totalCost"
        }

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
        fun generate(transactionId: Long = 0): TransactionBasket {
            return TransactionBasket(
                id = transactionId,
                date = generateRandomTime(),
                shopId = generateRandomLongValue(),
                totalCost = generateRandomLongValue(),
            )
        }

        @Ignore
        fun generateList(amount: Int = 10): List<TransactionBasket> {
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

data class TransactionBasketWithItems(
    val id: Long,
    val date: Long,
    val shop: Shop?,
    val totalCost: Long,
    val items: List<FullItem>,
) {
    companion object {
        fun generate(transactionBasketWithItemsId: Long = 0): TransactionBasketWithItems {
            return TransactionBasketWithItems(
                id = transactionBasketWithItemsId,
                date = generateRandomTime(),
                shop = Shop.generate(),
                totalCost = generateRandomLongValue(),
                items = FullItem.generateList(),
            )
        }

        fun generateList(amount: Int = 10): List<TransactionBasketWithItems> {
            return List(amount) {
                generate(it.toLong())
            }
        }
    }
}

/**
 * Converts a list of [TransactionBasket] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [TransactionBasket] data as list of string with csv format
 */
fun List<TransactionBasket>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(TransactionBasket.csvHeaders() + "\n")
    }

    // Add rows
    this@asCsvList.forEach {
        add(it.formatAsCsvString() + "\n")
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
