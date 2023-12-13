package com.kssidll.arrugarq.helper

import android.annotation.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*
import java.sql.Date
import java.text.*
import java.util.*
import kotlin.random.Random

private val defaultTimeFrom: Long = Date.valueOf("2020-01-01").time
private val defaultTimeUntil: Long = Date.valueOf("2025-12-31").time
private const val defaultDateStringFormatting: String = "yyyy-MM-dd"

@SuppressLint("ConstantLocale")
private val defaultLocale: Locale = Locale.getDefault()
private const val defaultStringLength: Int = 10
private const val defaultStringLengthFrom: Int = 4
private const val defaultStringLengthUntil: Int = 12
private const val defaultStringAllowedCharacters: String = "pyfgcrlaoeuidhtnsqjkxbmwvz"
private const val defaultLongValueFrom: Long = 10000
private const val defaultLongValueUntil: Long = 100000
private const val defaultItemAmount: Int = 10
private const val defaultProductAmount: Int = 10
private const val defaultProducerAmount: Int = 10
private const val defaultItemSpentByTimeAmount: Int = 10
private const val defaultItemSpentByShopAmount: Int = 10
private const val defaultItemSpentByCategoryAmount: Int = 10
private const val defaultEmbeddedItemAmount: Int = 10
private const val defaultEmbeddedProductAmount: Int = 10
private const val defaultFullItemAmount: Int = 10
private const val defaultProductPriceByShopByTimeAmount: Int = 10
private const val defaultShopId: Long = 0
private const val defaultCategoryId: Long = 0
private const val defaultItemId: Long = 0
private const val defaultProductId: Long = 0
private const val defaultProducerId: Long = 0
private const val defaultVariantId: Long = 0
private const val defaultItemQuantityFrom: Long = 500
private const val defaultItemQuantityUntil: Long = 10000
private const val defaultItemPriceFrom: Long = 10000
private const val defaultItemPriceUntil: Long = 100000
private const val defaultFloatDivisionFactor: Long = 100

fun generateRandomTime(
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
): Long {
    return Random.nextLong(
        from = (timeFrom / 86400000),
        until = (timeUntil / 86400000),
    ) * 86400000
}

fun generateRandomDate(
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
): Date {
    return Date(
        generateRandomTime(
            timeFrom,
            timeUntil
        )
    )
}

fun generateRandomDateString(
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
    dateFormatting: String = defaultDateStringFormatting,
    dateLocale: Locale = defaultLocale,
): String {
    return SimpleDateFormat(
        dateFormatting,
        dateLocale
    ).format(
        generateRandomDate(
            timeFrom,
            timeUntil
        )
    )
}

fun generateRandomStringValue(
    stringLength: Int = defaultStringLength,
    allowedCharacters: String = defaultStringAllowedCharacters,
): String {
    return List(stringLength) {
        allowedCharacters[Random.nextInt(allowedCharacters.length)]
    }.toCharArray()
        .concatToString()
}

fun generateRandomStringValue(
    stringLengthFrom: Int = defaultStringLengthFrom,
    stringLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
): String {
    return generateRandomStringValue(
        stringLength = Random.nextInt(
            stringLengthFrom,
            stringLengthUntil
        ),
        allowedCharacters = allowedCharacters,
    )
}

fun generateRandomLongValue(
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): Long {
    return Random.nextLong(
        from = valueFrom,
        until = valueUntil,
    )
}

fun generateRandomFloatValue(
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
    divisionFactor: Long = defaultFloatDivisionFactor,
): Float {
    return Random.nextLong(
        from = valueFrom,
        until = valueUntil,
    )
        .toFloat()
        .div(divisionFactor)
}

fun generateRandomItem(
    itemId: Long = defaultItemId,
    productId: Long = defaultProductId,
    variantId: Long? = defaultVariantId,
    shopId: Long? = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
): Item {
    return Item(
        id = itemId,
        productId = productId,
        variantId = variantId,
        shopId = shopId,
        quantity = generateRandomLongValue(
            itemQuantityFrom,
            itemQuantityUntil
        ),
        price = generateRandomLongValue(
            itemPriceFrom,
            itemPriceUntil
        ),
        date = generateRandomTime(
            itemDateTimeFrom,
            itemDateTimeUntil
        ),
    )
}

fun generateRandomShop(
    shopId: Long = defaultShopId,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
): Shop {
    return Shop(
        id = shopId,
        name = generateRandomStringValue(
            stringLengthFrom = shopNameLengthFrom,
            stringLengthUntil = shopNameLengthUntil,
            allowedCharacters = shopNameAllowedCharacters,
        )
    )
}

fun generateRandomProduct(
    productId: Long = defaultProductId,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
): Product {
    return Product(
        id = productId,
        categoryId = categoryId,
        producerId = producerId,
        name = generateRandomStringValue(
            stringLengthFrom = productNameLengthFrom,
            stringLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
        ),
    )
}

fun generateRandomProductWithAltNames(
    productId: Long = defaultProductId,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
): ProductWithAltNames {
    return ProductWithAltNames(
        product = generateRandomProduct(
            productId = productId,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
        ),
        alternativeNames = listOf(),
    )
}

fun generateRandomProductWithAltNamesList(
    amount: Int = defaultProductAmount,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
): List<ProductWithAltNames> {
    return List(amount) {
        generateRandomProductWithAltNames(
            productId = it.toLong(),
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters
        )
    }
}

fun generateRandomProductVariant(
    variantId: Long = defaultVariantId,
    productId: Long = defaultProductId,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
): ProductVariant {
    return ProductVariant(
        id = variantId,
        productId = productId,
        name = generateRandomStringValue(
            stringLengthFrom = variantNameLengthFrom,
            stringLengthUntil = variantNameLengthUntil,
            allowedCharacters = allowedCharacters,
        ),
    )
}

fun generateRandomProductCategory(
    categoryId: Long = defaultCategoryId,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
): ProductCategory {
    return ProductCategory(
        id = categoryId,
        name = generateRandomStringValue(
            stringLengthFrom = categoryNameLengthFrom,
            stringLengthUntil = categoryNameLengthUntil,
            allowedCharacters = categoryNameAllowedCharacters,
        )
    )
}

fun generateRandomItemSpentByTime(
    itemTimeFrom: Long = defaultTimeFrom,
    itemTimeUntil: Long = defaultTimeUntil,
    itemDateFormatting: String = defaultDateStringFormatting,
    itemDateLocale: Locale = defaultLocale,
    itemTotalFrom: Long = defaultLongValueFrom,
    itemTotalUntil: Long = defaultLongValueUntil,
): ItemSpentByTime {
    return ItemSpentByTime(
        time = generateRandomDateString(
            timeFrom = itemTimeFrom,
            timeUntil = itemTimeUntil,
            dateFormatting = itemDateFormatting,
            dateLocale = itemDateLocale,
        ),
        total = generateRandomLongValue(
            valueFrom = itemTotalFrom,
            valueUntil = itemTotalUntil,
        ),
    )
}

fun generateRandomItemSpentByTimeList(
    amount: Int = defaultItemSpentByTimeAmount,
    itemTimeFrom: Long = defaultTimeFrom,
    itemTimeUntil: Long = defaultTimeUntil,
    itemDateFormatting: String = defaultDateStringFormatting,
    itemDateLocale: Locale = defaultLocale,
    itemTotalFrom: Long = defaultLongValueFrom,
    itemTotalUntil: Long = defaultLongValueUntil,
): List<ItemSpentByTime> {
    return List(amount) {
        generateRandomItemSpentByTime(
            itemTimeFrom = itemTimeFrom,
            itemTimeUntil = itemTimeUntil,
            itemDateFormatting = itemDateFormatting,
            itemDateLocale = itemDateLocale,
            itemTotalFrom = itemTotalFrom,
            itemTotalUntil = itemTotalUntil,
        )
    }
}

fun generateRandomItemSpentByTimeListFlow(
    amount: Int = defaultItemSpentByTimeAmount,
    itemTimeFrom: Long = defaultTimeFrom,
    itemTimeUntil: Long = defaultTimeUntil,
    itemDateFormatting: String = defaultDateStringFormatting,
    itemDateLocale: Locale = defaultLocale,
    itemTotalFrom: Long = defaultLongValueFrom,
    itemTotalUntil: Long = defaultLongValueUntil,
): Flow<List<ItemSpentByTime>> {
    return flowOf(
        generateRandomItemSpentByTimeList(
            amount = amount,
            itemTimeFrom = itemTimeFrom,
            itemTimeUntil = itemTimeUntil,
            itemDateFormatting = itemDateFormatting,
            itemDateLocale = itemDateLocale,
            itemTotalFrom = itemTotalFrom,
            itemTotalUntil = itemTotalUntil,
        )
    )
}

fun generateRandomItemSpentByShop(
    shopId: Long = defaultShopId,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): ItemSpentByShop {
    return ItemSpentByShop(
        shop = generateRandomShop(
            shopId = shopId,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
        ),
        total = generateRandomLongValue(
            valueFrom = valueFrom,
            valueUntil = valueUntil,
        )
    )
}

fun generateRandomItemSpentByShopList(
    amount: Int = defaultItemSpentByShopAmount,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): List<ItemSpentByShop> {
    return List(amount) { index ->
        generateRandomItemSpentByShop(
            shopId = index.toLong(),
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
            valueFrom = valueFrom,
            valueUntil = valueUntil,
        )
    }
}

fun generateRandomItemSpentByShopListFlow(
    amount: Int = defaultItemSpentByShopAmount,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): Flow<List<ItemSpentByShop>> {
    return flowOf(
        generateRandomItemSpentByShopList(
            amount = amount,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
            valueFrom = valueFrom,
            valueUntil = valueUntil,
        )
    )
}

fun generateRandomItemSpentByCategory(
    categoryId: Long = defaultCategoryId,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): ItemSpentByCategory {
    return ItemSpentByCategory(
        category = generateRandomProductCategory(
            categoryId = categoryId,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
        ),
        total = generateRandomLongValue(
            valueFrom = valueFrom,
            valueUntil = valueUntil,
        )
    )
}

fun generateRandomItemSpentByCategoryList(
    amount: Int = defaultItemSpentByCategoryAmount,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): List<ItemSpentByCategory> {
    return List(amount) { index ->
        generateRandomItemSpentByCategory(
            categoryId = index.toLong(),
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            valueFrom = valueFrom,
            valueUntil = valueUntil,
        )
    }
}

fun generateRandomItemSpentByCategoryListFlow(
    amount: Int = defaultItemSpentByCategoryAmount,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): Flow<List<ItemSpentByCategory>> {
    return flowOf(
        generateRandomItemSpentByCategoryList(
            amount = amount,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            valueFrom = valueFrom,
            valueUntil = valueUntil,
        )
    )
}

fun generateRandomItemList(
    amount: Int = defaultItemAmount,
    productId: Long = defaultProductId,
    variantId: Long? = defaultVariantId,
    shopId: Long? = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil
): List<Item> {
    return List(amount) { index ->
        generateRandomItem(
            itemId = index.toLong(),
            productId = productId,
            variantId = variantId,
            shopId = shopId,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
        )
    }
}

fun generateRandomItemListFlow(
    amount: Int = defaultItemAmount,
    productId: Long = defaultProductId,
    variantId: Long? = defaultVariantId,
    shopId: Long? = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil
): Flow<List<Item>> {
    return flowOf(
        generateRandomItemList(
            amount = amount,
            productId = productId,
            variantId = variantId,
            shopId = shopId,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
        )
    )
}

fun generateRandomEmbeddedItem(
    itemId: Long = defaultItemId,
    productId: Long = defaultProductId,
    variantId: Long = defaultVariantId,
    shopId: Long = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
): EmbeddedItem {
    return EmbeddedItem(
        item = generateRandomItem(
            itemId = itemId,
            productId = productId,
            variantId = variantId,
            shopId = shopId,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
        ),
        product = generateRandomProduct(
            productId = productId,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
        ),
        variant = generateRandomProductVariant(
            variantId = variantId,
            productId = productId,
            variantNameLengthFrom = variantNameLengthFrom,
            variantNameLengthUntil = variantNameLengthUntil,
            allowedCharacters = allowedCharacters,
        ),
        shop = generateRandomShop(
            shopId = shopId,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
        ),
    )
}

fun generateRandomEmbeddedItemList(
    amount: Int = defaultEmbeddedItemAmount,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
): List<EmbeddedItem> {
    return List(amount) { index ->
        generateRandomEmbeddedItem(
            itemId = index.toLong(),
            productId = index.toLong(),
            variantId = index.toLong(),
            shopId = index.toLong(),
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
            categoryId = index.toLong(),
            producerId = index.toLong(),
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            variantNameLengthFrom = variantNameLengthFrom,
            variantNameLengthUntil = variantNameLengthUntil,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
        )
    }
}

fun generateRandomEmbeddedItemListFlow(
    amount: Int = defaultEmbeddedItemAmount,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
): Flow<List<EmbeddedItem>> {
    return flowOf(
        generateRandomEmbeddedItemList(
            amount = amount,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            variantNameLengthFrom = variantNameLengthFrom,
            variantNameLengthUntil = variantNameLengthUntil,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
        )
    )
}

fun generateRandomProducer(
    producerId: Long = defaultProducerId,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): ProductProducer {
    return ProductProducer(
        id = producerId,
        name = generateRandomStringValue(
            stringLengthFrom = producerNameLengthFrom,
            stringLengthUntil = producerNameLengthUntil,
            allowedCharacters = producerNameAllowedCharacters,
        )
    )
}

fun generateRandomProducerList(
    amount: Int = defaultProducerAmount,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): List<ProductProducer> {
    return List(amount) {
        generateRandomProducer(
            producerId = it.toLong(),
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    }
}

fun generateRandomProducerListFlow(
    amount: Int = defaultProducerAmount,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): Flow<List<ProductProducer>> {
    return flowOf(
        generateRandomProducerList(
            amount = amount,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    )
}

fun generateRandomEmbeddedProduct(
    productId: Long = defaultProductId,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): EmbeddedProduct {
    return EmbeddedProduct(
        product = generateRandomProduct(
            productId = productId,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
        ),
        category = generateRandomProductCategory(
            categoryId = categoryId,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
        ),
        producer = generateRandomProducer(
            producerId = producerId,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        ),
    )
}

fun generateRandomEmbeddedProductList(
    amount: Int = defaultEmbeddedProductAmount,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): List<EmbeddedProduct> {
    return List(amount) {
        generateRandomEmbeddedProduct(
            productId = it.toLong(),
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    }
}

fun generateRandomEmbeddedProductListFlow(
    amount: Int = defaultEmbeddedProductAmount,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): Flow<List<EmbeddedProduct>> {
    return flowOf(
        generateRandomEmbeddedProductList(
            amount = amount,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    )
}

fun generateRandomFullItem(
    itemId: Long = defaultItemId,
    productId: Long = defaultProductId,
    variantId: Long = defaultVariantId,
    shopId: Long = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): FullItem {
    return FullItem(
        embeddedItem = generateRandomEmbeddedItem(
            itemId = itemId,
            productId = productId,
            variantId = variantId,
            shopId = shopId,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            variantNameLengthFrom = variantNameLengthFrom,
            variantNameLengthUntil = variantNameLengthUntil,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
        ),
        embeddedProduct = generateRandomEmbeddedProduct(
            productId = productId,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    )
}

fun generateRandomFullItemList(
    amount: Int = defaultFullItemAmount,
    variantId: Long = defaultVariantId,
    shopId: Long = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): List<FullItem> {
    return List(amount) {
        generateRandomFullItem(
            itemId = it.toLong(),
            productId = it.toLong(),
            variantId = variantId,
            shopId = shopId,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            variantNameLengthFrom = variantNameLengthFrom,
            variantNameLengthUntil = variantNameLengthUntil,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    }
}

fun generateRandomFullItemListFlow(
    amount: Int = defaultFullItemAmount,
    variantId: Long = defaultVariantId,
    shopId: Long = defaultShopId,
    itemQuantityFrom: Long = defaultItemQuantityFrom,
    itemQuantityUntil: Long = defaultItemQuantityUntil,
    itemPriceFrom: Long = defaultItemPriceFrom,
    itemPriceUntil: Long = defaultItemPriceUntil,
    itemDateTimeFrom: Long = defaultTimeFrom,
    itemDateTimeUntil: Long = defaultTimeUntil,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
    variantNameLengthFrom: Int = defaultStringLengthFrom,
    variantNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameLengthFrom: Int = defaultStringLengthFrom,
    shopNameLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    categoryNameLengthFrom: Int = defaultStringLengthFrom,
    categoryNameLengthUntil: Int = defaultStringLengthUntil,
    categoryNameAllowedCharacters: String = defaultStringAllowedCharacters,
    producerNameLengthFrom: Int = defaultStringLengthFrom,
    producerNameLengthUntil: Int = defaultStringLengthUntil,
    producerNameAllowedCharacters: String = defaultStringAllowedCharacters,
): Flow<List<FullItem>> {
    return flowOf(
        generateRandomFullItemList(
            amount = amount,
            variantId = variantId,
            shopId = shopId,
            itemQuantityFrom = itemQuantityFrom,
            itemQuantityUntil = itemQuantityUntil,
            itemPriceFrom = itemPriceFrom,
            itemPriceUntil = itemPriceUntil,
            itemDateTimeFrom = itemDateTimeFrom,
            itemDateTimeUntil = itemDateTimeUntil,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = allowedCharacters,
            variantNameLengthFrom = variantNameLengthFrom,
            variantNameLengthUntil = variantNameLengthUntil,
            shopNameLengthFrom = shopNameLengthFrom,
            shopNameLengthUntil = shopNameLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
            categoryNameLengthFrom = categoryNameLengthFrom,
            categoryNameLengthUntil = categoryNameLengthUntil,
            categoryNameAllowedCharacters = categoryNameAllowedCharacters,
            producerNameLengthFrom = producerNameLengthFrom,
            producerNameLengthUntil = producerNameLengthUntil,
            producerNameAllowedCharacters = producerNameAllowedCharacters,
        )
    )
}

fun generateRandomProductPriceByShopByTime(
    productId: Long = defaultProductId,
    categoryId: Long = defaultCategoryId,
    producerId: Long = defaultProducerId,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    productAllowedCharacters: String = defaultStringAllowedCharacters,
    priceValueFrom: Long = defaultLongValueFrom,
    priceValueUntil: Long = defaultLongValueUntil,
    shopNameStringLengthFrom: Int = defaultStringLengthFrom,
    shopNameStringLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    variantNameStringLengthFrom: Int = defaultStringLengthFrom,
    variantNameStringLengthUntil: Int = defaultStringLengthUntil,
    variantNameAllowedCharacters: String = defaultStringAllowedCharacters,
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
    dateFormatting: String = defaultDateStringFormatting,
    dateLocale: Locale = defaultLocale,
): ProductPriceByShopByTime {
    return ProductPriceByShopByTime(
        product = generateRandomProduct(
            productId = productId,
            categoryId = categoryId,
            producerId = producerId,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            allowedCharacters = productAllowedCharacters,
        ),
        price = generateRandomLongValue(
            valueFrom = priceValueFrom,
            valueUntil = priceValueUntil,
        ),
        shopName = generateRandomStringValue(
            stringLengthFrom = shopNameStringLengthFrom,
            stringLengthUntil = shopNameStringLengthUntil,
            allowedCharacters = shopNameAllowedCharacters,
        ),
        time = generateRandomDateString(
            timeFrom = timeFrom,
            timeUntil = timeUntil,
            dateFormatting = dateFormatting,
            dateLocale = dateLocale,
        ),
        variantName = generateRandomStringValue(
            stringLengthFrom = variantNameStringLengthFrom,
            stringLengthUntil = variantNameStringLengthUntil,
            allowedCharacters = variantNameAllowedCharacters,
        ),
    )
}

fun generateRandomProductPriceByShopByTimeList(
    amount: Int = defaultProductPriceByShopByTimeAmount,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    productAllowedCharacters: String = defaultStringAllowedCharacters,
    priceValueFrom: Long = defaultLongValueFrom,
    priceValueUntil: Long = defaultLongValueUntil,
    shopNameStringLengthFrom: Int = defaultStringLengthFrom,
    shopNameStringLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
    dateFormatting: String = defaultDateStringFormatting,
    dateLocale: Locale = defaultLocale,
): List<ProductPriceByShopByTime> {
    return List(amount) {
        generateRandomProductPriceByShopByTime(
            productId = it.toLong(),
            categoryId = it.toLong(),
            producerId = it.toLong(),
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            productAllowedCharacters = productAllowedCharacters,
            priceValueFrom = priceValueFrom,
            priceValueUntil = priceValueUntil,
            shopNameStringLengthFrom = shopNameStringLengthFrom,
            shopNameStringLengthUntil = shopNameStringLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
            timeFrom = timeFrom,
            timeUntil = timeUntil,
            dateFormatting = dateFormatting,
            dateLocale = dateLocale,
        )
    }
}

fun generateRandomProductPriceByShopByTimeListFlow(
    amount: Int = defaultProductPriceByShopByTimeAmount,
    productNameLengthFrom: Int = defaultStringLengthFrom,
    productNameLengthUntil: Int = defaultStringLengthUntil,
    productAllowedCharacters: String = defaultStringAllowedCharacters,
    priceValueFrom: Long = defaultLongValueFrom,
    priceValueUntil: Long = defaultLongValueUntil,
    shopNameStringLengthFrom: Int = defaultStringLengthFrom,
    shopNameStringLengthUntil: Int = defaultStringLengthUntil,
    shopNameAllowedCharacters: String = defaultStringAllowedCharacters,
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
    dateFormatting: String = defaultDateStringFormatting,
    dateLocale: Locale = defaultLocale,
): Flow<List<ProductPriceByShopByTime>> {
    return flowOf(
        generateRandomProductPriceByShopByTimeList(
            amount = amount,
            productNameLengthFrom = productNameLengthFrom,
            productNameLengthUntil = productNameLengthUntil,
            productAllowedCharacters = productAllowedCharacters,
            priceValueFrom = priceValueFrom,
            priceValueUntil = priceValueUntil,
            shopNameStringLengthFrom = shopNameStringLengthFrom,
            shopNameStringLengthUntil = shopNameStringLengthUntil,
            shopNameAllowedCharacters = shopNameAllowedCharacters,
            timeFrom = timeFrom,
            timeUntil = timeUntil,
            dateFormatting = dateFormatting,
            dateLocale = dateLocale,
        )
    )
}