package com.kssidll.arrugarq.di.module

import com.kssidll.arrugarq.data.data.ProductCategory

fun prepopulateProductCategoryData(): List<ProductCategory> {
    return listOf(
        ProductCategory(0, "Beef"),
        ProductCategory(2, "Soda"),
        ProductCategory(4, "Internet Bill"),
        ProductCategory(0, "Tomato"),
        ProductCategory(4, "Electricity Bill"),
    )
}