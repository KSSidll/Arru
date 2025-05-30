package com.kssidll.arru.data.database

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.provider.DocumentsContractCompat
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket
import com.kssidll.arru.data.repository.ImportRepositorySource
import java.io.BufferedReader
import java.io.InputStreamReader

data class DocumentInfo(
    val documentId: String,
    val displayName: String,
    val mimeType: String?,
)

suspend fun importDataFromUris(
    context: Context,
    uri: Uri,
    importRepository: ImportRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    onError: () -> Unit,
) {
    val treeUri = DocumentsContractCompat.buildDocumentUriUsingTree(
        uri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val childrenUri = DocumentsContractCompat.buildChildDocumentsUriUsingTree(
        treeUri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val documents = mutableListOf<DocumentInfo>()

    context.contentResolver.query(
        childrenUri,
        arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
        ),
        null,
        null,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val documentId: String
            val displayName: String

            try {
                documentId = cursor.getString(0)
                displayName = cursor.getString(1)
            } catch (_: Exception) {
                continue
            }

            val mimeType: String? = try {
                cursor.getString(2)
            } catch (_: Exception) {
                null
            }

            documents.add(DocumentInfo(documentId, displayName, mimeType))
        }
    }

    val csvDocuments =
        documents.filter { it.mimeType == "text/csv" || it.displayName.endsWith(".csv", ignoreCase = true) }

    val jsonDocuments =
        documents.filter { it.mimeType == "application/json" || it.displayName.endsWith(".json", ignoreCase = true) }

    if (csvDocuments.isNotEmpty()) {
        handleCsvImport(
            context = context,
            treeUri = treeUri,
            documents = csvDocuments,
            importRepository = importRepository,
            onMaxProgressChange = onMaxProgressChange,
            onProgressChange = onProgressChange,
            onFinished = onFinished,
            onError = onError
        )

        return
    }

    if (jsonDocuments.isNotEmpty()) {
        handleJsonImport(
            context = context,
            treeUri = treeUri,
            documents = jsonDocuments,
            importRepository = importRepository,
            onMaxProgressChange = onMaxProgressChange,
            onProgressChange = onProgressChange,
            onFinished = onFinished,
            onError = onError
        )

        return
    }
}

suspend fun handleCsvImport(
    context: Context,
    treeUri: Uri,
    documents: List<DocumentInfo>,
    importRepository: ImportRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    onError: () -> Unit,
) {
    // Get the documents
    var exportDocument: DocumentInfo? = null

    var shopDocument: DocumentInfo? = null
    var producerDocument: DocumentInfo? = null
    var categoryDocument: DocumentInfo? = null
    var transactionDocument: DocumentInfo? = null
    var productDocument: DocumentInfo? = null
    var variantDocument: DocumentInfo? = null
    var itemDocument: DocumentInfo? = null

    documents.forEach { documentInfo ->
        if (documentInfo.displayName == "export.csv") exportDocument = documentInfo

        if (documentInfo.displayName == "shop.csv") shopDocument = documentInfo
        if (documentInfo.displayName == "producer.csv") producerDocument = documentInfo
        if (documentInfo.displayName == "category.csv") categoryDocument = documentInfo
        if (documentInfo.displayName == "transaction.json") transactionDocument = documentInfo
        if (documentInfo.displayName == "product.csv") productDocument = documentInfo
        if (documentInfo.displayName == "variant.csv") variantDocument = documentInfo
        if (documentInfo.displayName == "item.csv") itemDocument = documentInfo
    }

    // Prepare data
    val shopList = mutableListOf<Shop>()
    val producerList = mutableListOf<ProductProducer>()
    val categoryList = mutableListOf<ProductCategory>()
    val transactionList = mutableListOf<TransactionBasket>()
    val productList = mutableListOf<Product>()
    val variantList = mutableListOf<ProductVariant>()
    val itemList = mutableListOf<Item>()

    // Process

    // Compact csv export
    if (exportDocument != null) {
        onMaxProgressChange(3)

        val csvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, exportDocument.documentId)
        if (csvUri == null) {
            onError()
            return
        }

        data class CompactCsvRow(
            val transactionDate: Long,
            val transactionTotalPrice: Long,
            val shop: String?,
            val product: String?,
            val variant: String?,
            val category: String?,
            val producer: String?,
            val price: Long?,
            val quantity: Long?,
        )

        val compactCsvData = mutableListOf<CompactCsvRow>()

        context.contentResolver.openInputStream(csvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                if (headers == "transactionDate;transactionTotalPrice;shop;product;variant;category;producer;price;quantity") { // File version 1.0, @since v2.5.0
                    val transactionCostDivisor = 100
                    val priceDivisor = 100
                    val quantityDivisor = 1000

                    reader.forEachLine {
                        val text = it.split(";")

                        val transactionDate = text[0].toLong()
                        val transactionTotalPrice =
                            text[1].split(".", ",")
                                .let { (it[0].toLong() * transactionCostDivisor) + it[1].padEnd(2, '0').toLong() }
                        val shop = if (text[2] != "null") text[2] else null
                        val product = if (text[3] != "null") text[3] else null
                        val variant = if (text[4] != "null") text[4] else null
                        val category = if (text[5] != "null") text[5] else null
                        val producer = if (text[6] != "null") text[6] else null
                        val price =
                            if (text[7] != "null") text[7].split(".", ",")
                                .let { (it[0].toLong() * priceDivisor) + it[1].padEnd(2, '0').toLong() } else null
                        val quantity =
                            if (text[8] != "null") text[8].split(".", ",")
                                .let { (it[0].toLong() * quantityDivisor) + it[1].padEnd(3, '0').toLong() } else null

                        compactCsvData.add(
                            CompactCsvRow(
                                transactionDate = transactionDate,
                                transactionTotalPrice = transactionTotalPrice,
                                shop = shop,
                                product = product,
                                variant = variant,
                                category = category,
                                producer = producer,
                                price = price,
                                quantity = quantity,
                            )
                        )
                    }
                } else {
                    // Failed to determine file version
                    onError()
                    return
                }
            }

            onProgressChange(1)
        }

        // map object from csv data to id to get distinct

        val shops = mutableMapOf<String, Long>()
        val producers = mutableMapOf<String, Long>()
        val categories = mutableMapOf<String, Long>()
        val transactions =
            mutableMapOf<Pair<Long, Long>, Long>() // transaction is per day and we assume each has unique total
        val products = mutableMapOf<String, Long>()
        val variants = mutableMapOf<String, Long>()

        compactCsvData.forEach { data ->
            // handle shops
            data.shop?.let { shop ->
                if (shops[shop] == null) {
                    shops.put(shop, shops.size.toLong())
                    shopList.add(
                        Shop(
                            id = shops[shop]!!,
                            name = shop
                        )
                    )
                }
            }

            // handle producers
            data.producer?.let { producer ->
                if (producers[producer] == null) {
                    producers.put(producer, producers.size.toLong())
                    producerList.add(
                        ProductProducer(
                            id = producers[producer]!!,
                            name = producer
                        )
                    )
                }
            }

            // handle categories
            data.category?.let { category ->
                if (categories[category] == null) {
                    categories.put(category, categories.size.toLong())
                    categoryList.add(
                        ProductCategory(
                            id = categories[category]!!,
                            name = category
                        )
                    )
                }
            }

            // handle transactions
            if (transactions[Pair(data.transactionDate, data.transactionTotalPrice)] == null) {
                transactions.put(
                    Pair(data.transactionDate, data.transactionTotalPrice),
                    transactions.size.toLong()
                )
                transactionList.add(
                    TransactionBasket(
                        id = transactions[Pair(data.transactionDate, data.transactionTotalPrice)]!!,
                        date = data.transactionDate,
                        shopId = data.shop?.let { shops[it] },
                        totalCost = data.transactionTotalPrice
                    )
                )
            }

            // handle products
            data.product?.let { product ->
                if (products[product] == null) {
                    products.put(product, products.size.toLong())
                    productList.add(
                        Product(
                            id = products[product]!!,
                            categoryId = categories[data.category]!!,
                            producerId = data.producer?.let { producers[it] },
                            name = data.product,
                        )
                    )
                }
            }

            // handle variants
            data.variant?.let { variant ->
                if (variants[variant] == null) {
                    variants.put(variant, variants.size.toLong())
                    variantList.add(
                        ProductVariant(
                            id = variants[variant]!!,
                            productId = products[data.product]!!,
                            name = variant
                        )
                    )
                }
            }

            data.quantity?.let { quantity ->
                data.price?.let { price ->
                    itemList.add(
                        Item(
                            id = itemList.size.toLong(),
                            transactionBasketId = transactions[Pair(
                                data.transactionDate,
                                data.transactionTotalPrice
                            )]!!,
                            productId = products[data.product]!!,
                            variantId = data.variant?.let { variants[it] },
                            quantity = quantity,
                            price = price
                        )
                    )
                }
            }
        }

        onProgressChange(2)

        importRepository.insertAll(
            shops = shopList,
            producers = producerList,
            categories = categoryList,
            transactions = transactionList,
            products = productList,
            variants = variantList,
            items = itemList
        )

        onProgressChange(3)

        onFinished()

        return
    }

    // Raw csv export
    if (
        shopDocument != null
        && producerDocument != null
        && categoryDocument != null
        && transactionDocument != null
        && productDocument != null
        && variantDocument != null
        && itemDocument != null
    ) {
        onFinished()

        return
    }
}

suspend fun handleJsonImport(
    context: Context,
    treeUri: Uri,
    documents: List<DocumentInfo>,
    importRepository: ImportRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    onError: () -> Unit,
) {
    onFinished()

    return
}
