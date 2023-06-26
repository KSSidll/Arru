package com.kssidll.arrugarq.ui.addproduct

import java.util.Optional

data class AddProductData(
    var name: String,
    var categoryId: Long,
    var producerId: Optional<Long>,
)