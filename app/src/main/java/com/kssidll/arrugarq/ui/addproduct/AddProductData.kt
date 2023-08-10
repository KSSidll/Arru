package com.kssidll.arrugarq.ui.addproduct

import java.util.*

data class AddProductData(
    var name: String,
    var categoryId: Long,
    var producerId: Optional<Long>,
)