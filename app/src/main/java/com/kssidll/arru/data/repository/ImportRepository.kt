package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ImportDao
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket

class ImportRepository(private val dao: ImportDao): ImportRepositorySource {
    override suspend fun insertAll(
        shops: List<Shop>,
        producers: List<ProductProducer>,
        categories: List<ProductCategory>,
        transactions: List<TransactionBasket>,
        products: List<Product>,
        variants: List<ProductVariant>,
        items: List<Item>
    ) {
        dao.insertAll(
            shops = shops,
            producers = producers,
            categories = categories,
            transactions = transactions,
            products = products,
            variants = variants,
            items = items
        )
    }

}