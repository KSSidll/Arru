package com.kssidll.arru.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kssidll.arru.helper.generateRandomDate
import com.kssidll.arru.helper.generateRandomIntValue
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue

data class TransactionPreview(
    val id: Long,
    val date: Long,
    val totalCost: Long,
    val firstShopTagName: String?,
    val shopTagAmount: Int,
    val otherTagAmount: Int,
    val itemsVisible: MutableState<Boolean> = mutableStateOf(false)
) {
    companion object {
        @Composable
        fun generate(): TransactionPreview {
            return TransactionPreview(
                id = generateRandomLongValue(),
                date = generateRandomDate().time,
                totalCost = generateRandomLongValue(),
                firstShopTagName = generateRandomStringValue(),
                shopTagAmount = generateRandomIntValue(
                    1,
                    99
                ),
                otherTagAmount = generateRandomIntValue(
                    1,
                    99
                ),
            )
        }

        @Composable
        fun generateList(): List<TransactionPreview> {
            return List(10) {
                this.generate()
            }
        }
    }
}

data class TransactionSpentByTime(
    val time: String,
    val totalSpent: Long
)
