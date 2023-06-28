package com.kssidll.arrugarq.di.module

import com.kssidll.arrugarq.data.data.ProductCategoryType

fun prepopulateProductCategoryTypeData(): List<ProductCategoryType> {
    return listOf(
        ProductCategoryType(0, "Food Ingredient"),
        ProductCategoryType(1, "Fast Food"),
        ProductCategoryType(2, "Drink"),
        ProductCategoryType(3, "Sweets"),
        ProductCategoryType(4, "Bills"),
    )
}