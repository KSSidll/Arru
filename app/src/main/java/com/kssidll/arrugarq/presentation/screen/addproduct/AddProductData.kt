package com.kssidll.arrugarq.presentation.screen.addproduct

import java.util.*

data class AddProductData(
    var name: String,
    var categoryId: Long,
    var producerId: Optional<Long>,
)