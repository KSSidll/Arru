package com.kssidll.arru.data.database

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import androidx.core.provider.DocumentsContractCompat
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.repository.ImportRepositorySource
import java.io.BufferedReader
import java.io.InputStreamReader

data class DocumentInfo(
    val documentId: String,
    val displayName: String,
    val mimeType: String?,
)

sealed class ImportError() {
    data class FailedToOpenFile(val fileName: String): ImportError()
    data class FailedToDetermineVersion(val name: String): ImportError()
    data class MissingFiles(val expectedFileGroups: List<List<String>>): ImportError()
    data object ParseError: ImportError()
}

suspend fun importDataFromUris(
    context: Context,
    uri: Uri,
    importRepository: ImportRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    onError: (error: ImportError) -> Unit,
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

        // don't continue if a csv was found
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

        // don't continue if a json was found
        return
    }


    Log.e("Import", "failed to find expected files")
    onError(
        ImportError.MissingFiles(
            listOf(
                listOf(
                    "arru-export.json"
                ), listOf(
                    "arru-export.csv"
                ), listOf(
                    "arru-export-category.csv",
                    "arru-export-item.csv",
                    "arru-export-producer.csv",
                    "arru-export-product.csv",
                    "arru-export-shop.csv",
                    "arru-export-transaction.csv",
                    "arru-export-variant.csv",
                )
            )
        )
    )
    return
}

suspend fun handleCsvImport(
    context: Context,
    treeUri: Uri,
    documents: List<DocumentInfo>,
    importRepository: ImportRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    onError: (error: ImportError) -> Unit,
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


    // @since 2.5.8
    // new naming schema for files
    documents.forEach { documentInfo ->
        if (documentInfo.displayName == "arru-export.csv") exportDocument = documentInfo

        if (documentInfo.displayName == "arru-export-shop.csv") shopDocument = documentInfo
        if (documentInfo.displayName == "arru-export-producer.csv") producerDocument = documentInfo
        if (documentInfo.displayName == "arru-export-category.csv") categoryDocument = documentInfo
        if (documentInfo.displayName == "arru-export-transaction.csv") transactionDocument = documentInfo
        if (documentInfo.displayName == "arru-export-product.csv") productDocument = documentInfo
        if (documentInfo.displayName == "arru-export-variant.csv") variantDocument = documentInfo
        if (documentInfo.displayName == "arru-export-item.csv") itemDocument = documentInfo
    }

    // Fallback old naming schema, only read if no new detected
    documents.forEach { documentInfo ->
        if (documentInfo.displayName == "export.csv" && exportDocument == null) exportDocument = documentInfo

        if (documentInfo.displayName == "shop.csv" && shopDocument == null) shopDocument = documentInfo
        if (documentInfo.displayName == "producer.csv" && producerDocument == null) producerDocument = documentInfo
        if (documentInfo.displayName == "category.csv" && categoryDocument == null) categoryDocument = documentInfo
        if (documentInfo.displayName == "transaction.csv" && transactionDocument == null) transactionDocument = documentInfo
        if (documentInfo.displayName == "product.csv" && productDocument == null) productDocument = documentInfo
        if (documentInfo.displayName == "variant.csv" && variantDocument == null) variantDocument = documentInfo
        if (documentInfo.displayName == "item.csv" && itemDocument == null) itemDocument = documentInfo
    }

    // Prepare data
    val shopList = mutableListOf<Shop>()
    val producerList = mutableListOf<ProductProducer>()
    val categoryList = mutableListOf<ProductCategory>()
    val transactionList = mutableListOf<TransactionEntity>()
    val productList = mutableListOf<Product>()
    val variantList = mutableListOf<ProductVariant>()
    val itemEntityList = mutableListOf<ItemEntity>()

    // Process

    // Compact csv export
    if (exportDocument != null) {
        onMaxProgressChange(3)

        val csvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, exportDocument.documentId)
        if (csvUri == null) {
            Log.e("ImportCompactCsv", "could not build export csv uri")
            onError(ImportError.FailedToOpenFile(exportDocument.displayName))
            return
        }

        data class CompactCsvRow(
            val transactionId: Long,
            val transactionDate: Long,
            val transactionTotalPrice: Long,
            val shop: String?,
            val product: String?,
            val variant: String?,
            val variantGlobal: Boolean,
            val category: String?,
            val producer: String?,
            val price: Long?,
            val quantity: Long?,
            val transactionNote: String?
        )

        val compactCsvData = mutableListOf<CompactCsvRow>()

        context.contentResolver.openInputStream(csvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportCompactCsv", "could not open export csv file")
                onError(ImportError.FailedToOpenFile(exportDocument.displayName))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                when (headers) {
                    // File version 2.0, @since v2.5.7
                    "transactionId;transactionDate;transactionTotalPrice;shop;product;variant;variantGlobal;category;producer;price;quantity;transactionNote" -> {
                        reader.forEachLine { line ->
                            if (line.isNotBlank()) {
                                val text = line.split(";")

                                val transactionId = text[0].toLong()
                                val transactionDate = text[1].toLong()
                                val transactionTotalPrice = text[2].split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                                val shop = if (text[3] != "null") text[3] else null
                                val product = if (text[4] != "null") text[4] else null
                                val variant = if (text[5] != "null") text[5] else null
                                val variantGlobal = text[6] == "true"
                                val category = if (text[7] != "null") text[7] else null
                                val producer = if (text[8] != "null") text[8] else null
                                val price = if (text[9] != "null") text[9].split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() } else null
                                val quantity = if (text[10] != "null") text[10].split(".", ",").let { it[0].plus(it[1].padEnd(3, '0')).toLong() } else null
                                val transactionNote = if (text[11] != "null") text[11] else null

                                compactCsvData.add(
                                    CompactCsvRow(
                                        transactionId = transactionId,
                                        transactionDate = transactionDate,
                                        transactionTotalPrice = transactionTotalPrice,
                                        shop = shop,
                                        product = product,
                                        variant = variant,
                                        variantGlobal = variantGlobal,
                                        category = category,
                                        producer = producer,
                                        price = price,
                                        quantity = quantity,
                                        transactionNote = transactionNote
                                    )
                                )
                            }
                        }
                    }

                    // File version 1.0, @since v2.5.0
                    "transactionDate;transactionTotalPrice;shop;product;variant;category;producer;price;quantity" -> {
                        reader.forEachLine { line ->
                            if (line.isNotBlank()) {
                                val text = line.split(";")

                                val transactionDate = text[0].toLong()
                                val transactionTotalPrice = text[1].split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                                val shop = if (text[2] != "null") text[2] else null
                                val product = if (text[3] != "null") text[3] else null
                                val variant = if (text[4] != "null") text[4] else null
                                val category = if (text[5] != "null") text[5] else null
                                val producer = if (text[6] != "null") text[6] else null
                                val price = if (text[7] != "null") text[7].split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() } else null
                                val quantity = if (text[8] != "null") text[8].split(".", ",").let { it[0].plus(it[1].padEnd(3, '0')).toLong() } else null

                                val transactionId = if (compactCsvData.isEmpty()) {
                                    0L
                                } else {
                                    val prev = compactCsvData.last()

                                    if (prev.transactionDate != transactionDate || prev.transactionTotalPrice != transactionTotalPrice) {
                                        compactCsvData.last().transactionId + 1
                                    } else {
                                        compactCsvData.last().transactionId
                                    }

                                }

                                compactCsvData.add(
                                    CompactCsvRow(
                                        transactionId = transactionId,
                                        transactionDate = transactionDate,
                                        transactionTotalPrice = transactionTotalPrice,
                                        shop = shop,
                                        product = product,
                                        variant = variant,
                                        variantGlobal = false,
                                        category = category,
                                        producer = producer,
                                        price = price,
                                        quantity = quantity,
                                        transactionNote = null
                                    )
                                )
                            }
                        }
                    }

                    // Unknown version
                    else -> {
                        // Failed to determine file version
                        Log.e("ImportCompactCsv", "failed to determine file version")
                        onError(ImportError.FailedToDetermineVersion(exportDocument.displayName))
                        return
                    }
                }
            }

            onProgressChange(1)
        }

        // map object from csv data to id to get distinct

        val shops = mutableMapOf<String, Long>()
        val producers = mutableMapOf<String, Long>()
        val categories = mutableMapOf<String, Long>()
        val transactions = mutableMapOf<Long, Long>()
        val products = mutableMapOf<String, Long>()
        val variants = mutableMapOf<Pair<String, Long?>, Long>() // variant is per name and product, product can be null if variant is global

        compactCsvData.forEach { data ->
            // handle shops
            data.shop?.let { shop ->
                if (shops[shop] == null) {
                    shops.put(shop, shops.size.toLong() + 1) // id of 0 causes a foreign key constraint fail
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
                    producers.put(producer, producers.size.toLong() + 1) // id of 0 causes a foreign key constraint fail
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
                    categories.put(
                        category,
                        categories.size.toLong() + 1
                    ) // id of 0 causes a foreign key constraint fail
                    categoryList.add(
                        ProductCategory(
                            id = categories[category]!!,
                            name = category
                        )
                    )
                }
            }

            // handle transactions
            if (transactions[data.transactionId] == null) {
                transactions.put(
                    data.transactionId,
                    transactions.size.toLong() + 1 // id of 0 causes a foreign key constraint fail
                )
                transactionList.add(
                    TransactionEntity(
                        id = transactions[data.transactionId]!!,
                        date = data.transactionDate,
                        shopId = data.shop?.let { shops[it] },
                        totalCost = data.transactionTotalPrice,
                        note = data.transactionNote
                    )
                )
            }

            // handle products
            data.product?.let { product ->
                if (products[product] == null) {
                    products.put(product, products.size.toLong() + 1) // id of 0 causes a foreign key constraint fail
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
                if (data.variantGlobal) {
                    if (variants[Pair(variant, null)] == null) {
                        variants.put(
                            Pair(variant, null),
                            variants.size.toLong() + 1
                        ) // id of 0 causes a foreign key constraint fail
                        variantList.add(
                            ProductVariant(
                                id = variants[Pair(variant, null)]!!,
                                productId = null,
                                name = variant
                            )
                        )
                    }
                } else if (variants[Pair(variant, products[data.product]!!)] == null) {
                    variants.put(
                        Pair(variant, products[data.product]!!),
                        variants.size.toLong() + 1
                    ) // id of 0 causes a foreign key constraint fail
                    variantList.add(
                        ProductVariant(
                            id = variants[Pair(variant, products[data.product]!!)]!!,
                            productId = products[data.product]!!,
                            name = variant
                        )
                    )
                }
            }

            // handle items
            data.quantity?.let { quantity ->
                data.price?.let { price ->
                    itemEntityList.add(
                        ItemEntity(
                            id = itemEntityList.size.toLong() + 1, // id of 0 causes a foreign key constraint fail
                            transactionEntityId = transactions[data.transactionId]!!,
                            productId = products[data.product]!!,
                            variantId = data.variant?.let {
                                if (data.variantGlobal) {
                                    variants[Pair(it, null)]
                                } else variants[Pair(it, products[data.product]!!)]
                            },
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
            entities = itemEntityList
        )

        onProgressChange(3)

        onFinished()

        return
    }

    // Raw csv export
    // From newest to oldest
    // Files version 1.0, @since v2.5.0
    if (
        shopDocument != null
        && producerDocument != null
        && categoryDocument != null
        && transactionDocument != null
        && productDocument != null
        && variantDocument != null
        && itemDocument != null
    ) {
        onMaxProgressChange(8)

        val shopCsvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, shopDocument.documentId)
        val producerCsvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, producerDocument.documentId)
        val categoryCsvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, categoryDocument.documentId)
        val transactionCsvUri =
            DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, transactionDocument.documentId)
        val productCsvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, productDocument.documentId)
        val variantCsvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, variantDocument.documentId)
        val itemCsvUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, itemDocument.documentId)

        if (
            shopCsvUri == null
            || producerCsvUri == null
            || categoryCsvUri == null
            || transactionCsvUri == null
            || productCsvUri == null
            || variantCsvUri == null
            || itemCsvUri == null
        ) {
            Log.e("ImportRawCsv", "missing csv files")
            onError(
                ImportError.MissingFiles(
                    listOf(
                        listOf(
                            "arru-export-category.csv",
                            "arru-export-item.csv",
                            "arru-export-producer.csv",
                            "arru-export-product.csv",
                            "arru-export-shop.csv",
                            "arru-export-transaction.csv",
                            "arru-export-variant.csv",
                        )
                    )
                )
            )
            return
        }

        // read shop csv
        context.contentResolver.openInputStream(shopCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open shop csv file")
                onError(ImportError.FailedToOpenFile("arru-export-shop.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;name") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong()
                            val name = text[1]

                            shopList.add(
                                Shop(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    name = name
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    Log.e("ImportRawCsv", "failed to determine file version of shop.csv")
                    onError(ImportError.FailedToDetermineVersion("arru-export-shop.csv"))
                    return
                }
            }
        }

        onProgressChange(1)

        // read producer csv
        context.contentResolver.openInputStream(producerCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open producer csv file")
                onError(ImportError.FailedToOpenFile("arru-export-producer.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;name") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong()
                            val name = text[1]

                            producerList.add(
                                ProductProducer(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    name = name
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    Log.e("ImportRawCsv", "failed to determine file version of producer.csv")
                    onError(ImportError.FailedToDetermineVersion("arru-export-producer.csv"))
                    return
                }
            }
        }

        onProgressChange(2)

        // read categories csv
        context.contentResolver.openInputStream(categoryCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open categories csv file")
                onError(ImportError.FailedToOpenFile("arru-export-category.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;name") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong()
                            val name = text[1]

                            categoryList.add(
                                ProductCategory(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    name = name
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    Log.e("ImportRawCsv", "failed to determine file version of category.csv")
                    onError(ImportError.FailedToDetermineVersion("arru-export-category.csv"))
                    return
                }
            }
        }

        onProgressChange(3)

        // read transactions csv
        context.contentResolver.openInputStream(transactionCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open transactions csv file")
                onError(ImportError.FailedToOpenFile("arru-export-transaction.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                when (headers) {
                    // File version 2.0, @since v2.5.7
                    "id;date;shopId;totalCost;note" -> {
                        val transactionCostDivisor = 100

                        reader.forEachLine { line ->
                            if (line.isNotBlank()) {
                                val text = line.split(";")

                                val id = text[0].toLong()
                                val date = text[1].toLong()
                                val shopId = if (text[2] != "null") text[2].toLong() else null
                                val totalCost =
                                    text[3].split(".", ",")
                                        .let { (it[0].toLong() * transactionCostDivisor) + it[1].padEnd(2, '0').toLong() }
                                val note = if (text[4] != "null") text[4] else null

                                transactionList.add(
                                    TransactionEntity(
                                        id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                        date = date,
                                        shopId = shopId?.plus(1), // id can be 0 but 0 causes a foreign key constraint fail
                                        totalCost = totalCost,
                                        note = note
                                    )
                                )
                            }
                        }
                    }

                    // File version 1.0, @since v2.5.0
                    "id;date;shopId;totalCost" -> {
                        val transactionCostDivisor = 100

                        reader.forEachLine { line ->
                            if (line.isNotBlank()) {
                                val text = line.split(";")

                                val id = text[0].toLong()
                                val date = text[1].toLong()
                                val shopId = if (text[2] != "null") text[2].toLong() else null
                                val totalCost =
                                    text[3].split(".", ",")
                                        .let { (it[0].toLong() * transactionCostDivisor) + it[1].padEnd(2, '0').toLong() }

                                transactionList.add(
                                    TransactionEntity(
                                        id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                        date = date,
                                        shopId = shopId?.plus(1), // id can be 0 but 0 causes a foreign key constraint fail
                                        totalCost = totalCost,
                                        note = null
                                    )
                                )
                            }
                        }
                    }
                    else -> {
                        // Failed to determine file version
                        Log.e("ImportRawCsv", "failed to determine file version of transaction.csv")
                        onError(ImportError.FailedToDetermineVersion("arru-export-transaction.csv"))
                        return
                    }
                }
            }
        }

        onProgressChange(4)

        // read products csv
        context.contentResolver.openInputStream(productCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open products csv file")
                onError(ImportError.FailedToOpenFile("arru-export-product.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;categoryId;producerId;name") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong()
                            val categoryId = text[1].toLong()
                            val producerId = if (text[2] != "null") text[2].toLong() else null
                            val name = text[3]

                            productList.add(
                                Product(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    categoryId = categoryId + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    producerId = producerId?.plus(1), // id can be 0 but 0 causes a foreign key constraint fail
                                    name = name
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    Log.e("ImportRawCsv", "failed to determine file version of product.csv")
                    onError(ImportError.FailedToDetermineVersion("arru-export-product.csv"))
                    return
                }
            }
        }

        onProgressChange(5)

        // read variants csv
        context.contentResolver.openInputStream(variantCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open variants csv file")
                onError(ImportError.FailedToOpenFile("arru-export-variant.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                // productId optional @since v2.5.7
                if (headers == "id;productId;name") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong() + 1 // id can be 0 but 0 causes a foreign key constraint fail
                            val productId = if (text[1] != "null") text[1].toLong() + 1 else null // id can be 0 but 0 causes a foreign key constraint fail
                            val name = text[2]

                            variantList.add(
                                ProductVariant(
                                    id = id,
                                    productId = productId,
                                    name = name
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    Log.e("ImportRawCsv", "failed to determine file version of variant.csv")
                    onError(ImportError.FailedToDetermineVersion("arru-export-variant.csv"))
                    return
                }
            }
        }

        onProgressChange(6)

        // read items csv
        context.contentResolver.openInputStream(itemCsvUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportRawCsv", "failed to open items csv file")
                onError(ImportError.FailedToOpenFile("arru-export-item.csv"))
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;transactionBasketId;productId;variantId;quantity;price") {
                    val priceDivisor = 100
                    val quantityDivisor = 1000

                    reader.forEachLine { line ->
                        if (line.isNotBlank()) {
                            val text = line.split(';')

                            val id = text[0].toLong()
                            val transactionBasketId = text[1].toLong()
                            val productId = text[2].toLong()
                            val variantId = if (text[3] != "null") text[3].toLong() else null
                            val quantity =
                                text[4].split(".", ",")
                                    .let { (it[0].toLong() * quantityDivisor) + it[1].padEnd(3, '0').toLong() }
                            val price =
                                text[5].split(".", ",")
                                    .let { (it[0].toLong() * priceDivisor) + it[1].padEnd(2, '0').toLong() }

                            itemEntityList.add(
                                ItemEntity(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    transactionEntityId = transactionBasketId + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    productId = productId + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    variantId = variantId?.plus(1), // id can be 0 but 0 causes a foreign key constraint fail
                                    quantity = quantity,
                                    price = price,
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    Log.e("ImportRawCsv", "failed to determine file version of item.csv")
                    onError(ImportError.FailedToDetermineVersion("arru-export-item.csv"))
                    return
                }
            }
        }

        onProgressChange(7)

        importRepository.insertAll(
            shops = shopList,
            producers = producerList,
            categories = categoryList,
            transactions = transactionList,
            products = productList,
            variants = variantList,
            entities = itemEntityList
        )

        onProgressChange(8)

        onFinished()

        return
    }

    // did not find expected files if we are here

    Log.e("ImportCsv", "failed to find expected files")
    onError(
        ImportError.MissingFiles(
            listOf(
                listOf(
                    "arru-export.csv"
                ),
                listOf(
                    "arru-export-category.csv",
                    "arru-export-item.csv",
                    "arru-export-producer.csv",
                    "arru-export-product.csv",
                    "arru-export-shop.csv",
                    "arru-export-transaction.csv",
                    "arru-export-variant.csv",
                )
            )
        )
    )

    return
}

suspend fun handleJsonImport(
    context: Context,
    treeUri: Uri,
    documents: List<DocumentInfo>,
    importRepository: ImportRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    onError: (error: ImportError) -> Unit,
) {
    // Get the documents
    var exportDocument: DocumentInfo? = null

    // @since 2.5.8
    // new naming schema for files
    documents.forEach { documentInfo ->
        if (documentInfo.displayName == "arru-export.json") exportDocument = documentInfo
    }

    // Fallback old naming schema, only read if no new detected
    documents.forEach { documentInfo ->
        if (documentInfo.displayName == "export.json" && exportDocument == null) exportDocument = documentInfo
    }

    // Prepare data
    val shopList = mutableListOf<Shop>()
    val producerList = mutableListOf<ProductProducer>()
    val categoryList = mutableListOf<ProductCategory>()
    val transactionList = mutableListOf<TransactionEntity>()
    val productList = mutableListOf<Product>()
    val variantList = mutableListOf<ProductVariant>()
    val itemEntityList = mutableListOf<ItemEntity>()

    // Process

    if (exportDocument != null) {
        onMaxProgressChange(2)

        val jsonUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, exportDocument.documentId)
        if (jsonUri == null) {
            Log.e("ImportJSON", "could not build export json uri")
            onError(ImportError.FailedToOpenFile("arru-export.json"))
            return
        }

        context.contentResolver.openInputStream(jsonUri).use { inputStream ->
            if (inputStream == null) {
                Log.e("ImportJSON", "failed to open export json file")
                onError(ImportError.FailedToOpenFile("arru-export.json"))
                return
            }

            val reader = JsonReader(inputStream.reader())

            val shops = mutableSetOf<Long>()
            val producers = mutableSetOf<Long>()
            val categories = mutableSetOf<Long>()
            val transactions = mutableSetOf<Long>()
            val products = mutableSetOf<Long>()
            val variants = mutableSetOf<Long>()
            val items = mutableSetOf<Long>()

            try {
                reader.beginArray()

                while (reader.hasNext()) {
                    reader.beginObject()

                    var id: Long? = null
                    var date: Long? = null
                    var cost: Long? = null
                    var transactionShopId: Long? = null
                    val transactionItemEntities = mutableListOf<ItemEntity>()
                    var transactionNote: String? = null

                    while (reader.hasNext()) {
                        val name = reader.nextName()

                        when (name) {
                            "id" -> {
                                id = reader.nextLong()
                            }
                            "date" -> {
                                date = reader.nextLong()
                            }
                            "cost" -> {
                                cost = reader.nextString().split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                            }
                            "note" -> {
                                if (reader.peek() == JsonToken.NULL) {
                                    transactionNote = null
                                    reader.nextNull()
                                } else {
                                    transactionNote = reader.nextString()
                                }
                            }
                            "shop" -> {
                                if (reader.peek() == JsonToken.NULL) {
                                    reader.nextNull()
                                } else {
                                    reader.beginObject()

                                    var shopName: String? = null

                                    while (reader.hasNext()) {
                                        val shopValueName = reader.nextName()

                                        when (shopValueName) {
                                            "id" -> {
                                                transactionShopId = reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                            }

                                            "name" -> {
                                                shopName = reader.nextString()
                                            }

                                            else -> {
                                                reader.skipValue()
                                            }
                                        }
                                    }

                                    // From newest to oldest
                                    // File version 1.0, @since v2.5.0
                                    if (transactionShopId != null && shopName != null) {
                                        if (shops.add(transactionShopId)) {
                                            shopList.add(
                                                Shop(
                                                    id = transactionShopId,
                                                    name = shopName,
                                                )
                                            )
                                        }
                                    } else {
                                        // Failed to determine file version
                                        Log.e("ImportJSON", "failed to determine shop data version")
                                        onError(ImportError.FailedToDetermineVersion("arru-export.json -> shop"))
                                        return
                                    }

                                    reader.endObject()
                                }
                            }
                            "items" -> {
                                if (reader.peek() == JsonToken.NULL) {
                                    reader.nextNull()
                                } else {
                                    reader.beginArray()

                                    while (reader.hasNext()) {
                                        reader.beginObject()

                                        var itemId: Long? = null
                                        var itemPrice: Long? = null
                                        var itemQuantity: Long? = null
                                        var itemProductId: Long? = null
                                        var itemVariantId: Long? = null

                                        var itemVariant: ProductVariant? = null

                                        while (reader.hasNext()) {
                                            val itemValueName = reader.nextName()

                                            when (itemValueName) {
                                                "id" -> {
                                                    itemId =
                                                        reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                }
                                                "price" -> {
                                                    itemPrice =
                                                        reader.nextString()
                                                            .split(".", ",")
                                                            .let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                                                }
                                                "quantity" -> {
                                                    itemQuantity =
                                                        reader.nextString()
                                                            .split(".", ",")
                                                            .let { it[0].plus(it[1].padEnd(3, '0')).toLong() }
                                                }
                                                "product" -> {
                                                    reader.beginObject()

                                                    var productName: String? = null
                                                    var productCategoryId: Long? = null
                                                    var productProducerId: Long? = null

                                                    while (reader.hasNext()) {
                                                        val productValueName = reader.nextName()

                                                        if (productValueName == "id") {
                                                            itemProductId =
                                                                reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                        } else if (productValueName == "name") {
                                                            productName = reader.nextString()
                                                        } else if (productValueName == "category") {
                                                            reader.beginObject()

                                                            var categoryName: String? = null

                                                            while (reader.hasNext()) {
                                                                val categoryValueName = reader.nextName()

                                                                when (categoryValueName) {
                                                                    "id" -> {
                                                                        productCategoryId =
                                                                            reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                                    }

                                                                    "name" -> {
                                                                        categoryName = reader.nextString()
                                                                    }

                                                                    else -> {
                                                                        reader.skipValue()
                                                                    }
                                                                }
                                                            }

                                                            // From newest to oldest
                                                            // File version 1.0, @since v2.5.0
                                                            if (productCategoryId != null && categoryName != null) {
                                                                if (categories.add(productCategoryId)) {
                                                                    categoryList.add(
                                                                        ProductCategory(
                                                                            id = productCategoryId,
                                                                            name = categoryName,
                                                                        )
                                                                    )
                                                                }
                                                            } else {
                                                                // Failed to determine file version
                                                                Log.e("ImportJSON", "failed to determine category data version")
                                                                onError(ImportError.FailedToDetermineVersion("arru-export.json -> category"))
                                                                return
                                                            }

                                                            reader.endObject()
                                                        } else if (productValueName == "producer") {
                                                            if (reader.peek() == JsonToken.NULL) {
                                                                reader.nextNull()
                                                            } else {
                                                                reader.beginObject()

                                                                var producerName: String? = null

                                                                while (reader.hasNext()) {
                                                                    val producerValueName = reader.nextName()

                                                                    when (producerValueName) {
                                                                        "id" -> {
                                                                            productProducerId =
                                                                                reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                                        }

                                                                        "name" -> {
                                                                            producerName = reader.nextString()
                                                                        }

                                                                        else -> {
                                                                            reader.skipValue()
                                                                        }
                                                                    }
                                                                }

                                                                // From newest to oldest
                                                                // File version 1.0, @since v2.5.0
                                                                if (productProducerId != null && producerName != null) {
                                                                    if (producers.add(productProducerId)) {
                                                                        producerList.add(
                                                                            ProductProducer(
                                                                                id = productProducerId,
                                                                                name = producerName,
                                                                            )
                                                                        )
                                                                    }
                                                                } else {
                                                                    // Failed to determine file version
                                                                    Log.e("ImportJSON", "failed to determine producer data version")
                                                                    onError(ImportError.FailedToDetermineVersion("arru-export.json -> producer"))
                                                                    return
                                                                }

                                                                reader.endObject()
                                                            }
                                                        } else {
                                                            reader.skipValue()
                                                        }
                                                    }

                                                    // From newest to oldest
                                                    // File version 1.0, @since v2.5.0
                                                    if (
                                                        itemProductId != null
                                                        && productName != null
                                                        && productCategoryId != null
                                                    ) {
                                                        if (products.add(itemProductId)) {
                                                            productList.add(
                                                                Product(
                                                                    id = itemProductId,
                                                                    name = productName,
                                                                    categoryId = productCategoryId,
                                                                    producerId = productProducerId,
                                                                )
                                                            )
                                                        }
                                                    } else {
                                                        // Failed to determine file version
                                                        Log.e("ImportJSON", "failed to determine product data version")
                                                        onError(ImportError.FailedToDetermineVersion("arru-export.json -> product"))
                                                        return
                                                    }

                                                    reader.endObject()
                                                }
                                                "variant" -> {
                                                    if (reader.peek() == JsonToken.NULL) {
                                                        reader.nextNull()
                                                    } else {
                                                        reader.beginObject()
                                                        var variantName: String? = null
                                                        var variantGlobal = false  // @since v2.5.7, global has null productId, default not global

                                                        while (reader.hasNext()) {
                                                            val variantValueName = reader.nextName()

                                                            when (variantValueName) {
                                                                "id" -> {
                                                                    itemVariantId = reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                                }

                                                                "name" -> {
                                                                    variantName = reader.nextString()
                                                                }

                                                                "global" -> {
                                                                    variantGlobal = reader.nextBoolean()
                                                                }

                                                                else -> {
                                                                    reader.skipValue()
                                                                }
                                                            }
                                                        }

                                                        // From newest to oldest
                                                        // File version 1.0, @since v2.5.0
                                                        if (
                                                            itemVariantId != null
                                                            && variantName != null
                                                        ) {
                                                            if (variants.add(itemVariantId)) {
                                                                val productId = if (variantGlobal) {
                                                                    null // global variants have no product id
                                                                } else -1L  // temporarily assume -1 as product id could technically not be set

                                                                itemVariant = ProductVariant(
                                                                    id = itemVariantId,
                                                                    productId = productId,
                                                                    name = variantName,
                                                                )
                                                            }
                                                        } else {
                                                            // Failed to determine file version
                                                            Log.e("ImportJSON", "failed to determine variant data version")
                                                            onError(ImportError.FailedToDetermineVersion("arru-export.json -> variant"))
                                                            return
                                                        }

                                                        reader.endObject()
                                                    }
                                                }
                                                else -> {
                                                    reader.skipValue()
                                                }
                                            }
                                        }


                                        // From newest to oldest
                                        // File version 1.0, @since v2.5.0
                                        if (
                                            itemId != null
                                            && itemPrice != null
                                            && itemQuantity != null
                                            && itemProductId != null
                                        ) {
                                            if (items.add(itemId)) {
                                                itemVariant?.let { // update id because now productId has to be set
                                                    // only update id if not null, null means that the variant is global
                                                    if (it.productId != null) {
                                                        it.productId = itemProductId
                                                    }
                                                    variantList.add(it) // also add to list
                                                }

                                                transactionItemEntities.add(
                                                    ItemEntity(
                                                        id = itemId,
                                                        transactionEntityId = -1, // temporarily assume -1 as transaction id could technically not be set
                                                        price = itemPrice,
                                                        quantity = itemQuantity,
                                                        productId = itemProductId,
                                                        variantId = itemVariantId
                                                    )
                                                )
                                            }
                                        } else {
                                            // Failed to determine file version
                                            Log.e("ImportJSON", "failed to determine transaction data version")
                                            onError(ImportError.FailedToDetermineVersion("arru-export.json -> transaction"))
                                            return
                                        }

                                        reader.endObject()
                                    }

                                    reader.endArray()
                                }
                            }
                            else -> {
                                reader.skipValue()
                            }
                        }
                    }


                    if (
                        id != null
                        && date != null
                        && cost != null
                    ) {
                        if (transactions.add(id)) {
                            transactionItemEntities.forEach {
                                it.transactionEntityId = id
                            }

                            itemEntityList.addAll(transactionItemEntities)

                            transactionList.add(
                                TransactionEntity(
                                    id = id,
                                    date = date,
                                    shopId = transactionShopId,
                                    totalCost = cost,
                                    note = transactionNote
                                )
                            )
                        }
                    }

                    reader.endObject()
                }

                reader.endArray()

                onProgressChange(1)

                importRepository.insertAll(
                    shops = shopList,
                    producers = producerList,
                    categories = categoryList,
                    transactions = transactionList,
                    products = productList,
                    variants = variantList,
                    entities = itemEntityList
                )

                onProgressChange(2)

                onFinished()

                return
            } catch (e: Exception) {
                e.printStackTrace()
                onError(ImportError.ParseError)
                return
            } finally {
                reader.close()
            }
        }
    }

    // did not find expected files if we are here

    Log.e("ImportJSON", "failed to find expected files")
    onError(ImportError.MissingFiles(listOf(listOf("arru-export.json"))))

    return
}
