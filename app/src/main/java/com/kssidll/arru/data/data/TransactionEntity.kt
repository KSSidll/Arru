package com.kssidll.arru.data.data

import androidx.collection.FloatFloatPair
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomDateString
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import com.kssidll.arru.helper.generateRandomTime
import java.util.Locale
import kotlin.math.log10

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopEntityId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(value = ["date"]),
        Index(value = ["shopEntityId"]),
    ],
    tableName = "TransactionEntity"
)
@Immutable
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: Long,
    val shopEntityId: Long?,
    val totalCost: Long,
    val note: String?,
) {
    @Ignore
    constructor(
        date: Long,
        totalCost: Long,
        shopEntityId: Long?,
        note: String?
    ): this(
        0,
        date,
        shopEntityId,
        totalCost,
        note
    )

    @Ignore
    fun actualTotalCost(): Float {
        return actualTotalCost(totalCost)
    }

    /**
     * Converts the [TransactionEntity] data to a string with csv format
     *
     * Doesn't include the csv headers
     * @return [TransactionEntity] data as [String] with csv format
     */
    @Ignore
    fun formatAsCsvString(): String {
        return "${id};${date};${shopEntityId};${actualTotalCost()};${note}"
    }

    companion object {
        @Ignore
        const val COST_DIVISOR: Long = 100

        @Ignore
        const val INVALID_DATE: Long = Long.MIN_VALUE

        @Ignore
        const val INVALID_TOTAL_COST: Long = Long.MIN_VALUE

        /**
         * Returns the [String] representing the [TransactionEntity] csv format headers
         * @return [String] representing the [TransactionEntity] csv format headers
         */
        @Ignore
        fun csvHeaders(): String {
            return "id;date;shopId;totalCost;note"
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
        fun generate(transactionId: Long = 0): TransactionEntity {
            return TransactionEntity(
                id = transactionId,
                date = generateRandomTime(),
                shopEntityId = generateRandomLongValue(),
                totalCost = generateRandomLongValue(),
                note = generateRandomStringValue()
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

@Immutable
data class TransactionBasketWithItems(
    val id: Long,
    val date: Long,
    val shop: ShopEntity?,
    val totalCost: Long,
    val note: String?,
    val items: List<FullItem>,
) {
    companion object {
        fun generate(transactionBasketWithItemsId: Long = 0): TransactionBasketWithItems {
            return TransactionBasketWithItems(
                id = transactionBasketWithItemsId,
                date = generateRandomTime(),
                shop = ShopEntity.generate(),
                totalCost = generateRandomLongValue(),
                note = generateRandomStringValue(),
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
 * Converts a list of [TransactionEntity] data to a list of strings with csv format
 * @param includeHeaders whether to include the csv headers
 * @return [TransactionEntity] data as list of string with csv format
 */
fun List<TransactionEntity>.asCsvList(includeHeaders: Boolean = false): List<String> = buildList {
    // Add headers
    if (includeHeaders) {
        add(TransactionEntity.csvHeaders() + "\n")
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
            .div(TransactionEntity.COST_DIVISOR)
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
