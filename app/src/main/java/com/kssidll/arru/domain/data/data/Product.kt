package com.kssidll.arru.domain.data.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import com.kssidll.arru.helper.generateRandomDateString
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

// @Immutable
// data class Product(
//     val id: Long
// ) {
//     companion object {
//         fun fromEntity(entity: ProductEntity): Product {
//             return Product(
//                 id = entity.id
//             )
//         }
//     }
// }

@Immutable
data class ProductPriceByShopByVariantByProducerByTime(
    @ColumnInfo("data_order") val dataOrder: Long,
    val date: String,
    val value: Long?,
    val shopName: String?,
    val productVariantName: String?,
    val productProducerName: String?,
) {
    companion object {
        fun generate(idx: Long): ProductPriceByShopByVariantByProducerByTime {
            return ProductPriceByShopByVariantByProducerByTime(
                dataOrder = idx,
                productVariantName = generateRandomStringValue(),
                productProducerName = generateRandomStringValue(),
                value = generateRandomLongValue(),
                shopName = generateRandomStringValue(),
                date = generateRandomDateString(),
            )
        }

        fun generateList(
            amount: Int = 10
        ): ImmutableList<ProductPriceByShopByVariantByProducerByTime> {
            return List(amount) { generate(it.toLong()) }.toImmutableList()
        }
    }
}
