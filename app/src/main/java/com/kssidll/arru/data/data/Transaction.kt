package com.kssidll.arru.data.data

import androidx.room.*
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomTime
import kotlin.math.log10

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var date: Long,
    var totalCost: Long,
) {
    @Ignore
    constructor(
        date: Long,
        totalCost: Long,
    ): this(
        0,
        date,
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
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class TransactionTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val transactionId: Long,
    @ColumnInfo(index = true) val tagId: Long,
)
