package com.kssidll.arrugarq.helper

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

fun getFakeSpentByTimeData(): List<ItemSpentByTime> {
    return listOf(
        ItemSpentByTime(
            time = "2022-08",
            total = 34821,
        ),
        ItemSpentByTime(
            time = "2022-09",
            total = 25000,
        ),
        ItemSpentByTime(
            time = "2022-10",
            total = 50000,
        ),
        ItemSpentByTime(
            time = "2022-11",
            total = 12345,
        ),
    )
}

fun getFakeSpentByTimeDataFlow(): Flow<List<ItemSpentByTime>> {
    return flowOf(getFakeSpentByTimeData())
}

fun getFakeSpentByShopData(): List<ItemSpentByShop> {
    return listOf(
        ItemSpentByShop(
            shop = Shop("test1"),
            total = 168200,
        ),
        ItemSpentByShop(
            shop = Shop("test2"),
            total = 10000,
        ),
        ItemSpentByShop(
            shop = Shop("test3"),
            total = 100000,
        ),
        ItemSpentByShop(
            shop = Shop("test4"),
            total = 61000,
        ),
        ItemSpentByShop(
            shop = Shop("test5"),
            total = 27600,
        ),
    )
}

fun getFakeSpentByShopDataFlow(): Flow<List<ItemSpentByShop>> {
    return flowOf(getFakeSpentByShopData())
}

fun getFakeSpentByCategoryData(): List<ItemSpentByCategory> {
    return listOf(
        ItemSpentByCategory(
            category = ProductCategory("test1"),
            total = 168200,
        ),
        ItemSpentByCategory(
            category = ProductCategory("test2"),
            total = 10000,
        ),
        ItemSpentByCategory(
            category = ProductCategory("test3"),
            total = 100000,
        ),
        ItemSpentByCategory(
            category = ProductCategory("test4"),
            total = 61000,
        ),
        ItemSpentByCategory(
            category = ProductCategory("test5"),
            total = 27600,
        ),
    )
}

fun getFakeSpentByCategoryDataFlow(): Flow<List<ItemSpentByCategory>> {
    return flowOf(getFakeSpentByCategoryData())
}
