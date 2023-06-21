package com.kssidll.arrugarq.ui.additem

import java.util.Optional

data class AddItemData(
    var productId: Long,
    var shopId: Optional<Long>,
    var quantity: Long,
    var unitMeasure: Optional<Long>,
    var price: Float,
    var date: Long,
)