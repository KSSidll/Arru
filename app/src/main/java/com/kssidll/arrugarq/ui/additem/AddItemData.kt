package com.kssidll.arrugarq.ui.additem

import java.util.Optional

data class AddItemData(
    var productId: Long,
    var variantId: Optional<Long>,
    var shopId: Optional<Long>,
    var quantity: Long,
    var price: Float,
    var date: Long,
)