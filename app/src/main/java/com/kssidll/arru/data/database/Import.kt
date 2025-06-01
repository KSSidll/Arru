package com.kssidll.arru.data.database

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.JsonReader
import android.util.JsonToken
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
        if (documentInfo.displayName == "transaction.csv") transactionDocument = documentInfo
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
                // File version 1.0, @since v2.5.0
                if (headers == "transactionDate;transactionTotalPrice;shop;product;variant;category;producer;price;quantity") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val transactionDate = text[0].toLong()
                            val transactionTotalPrice = text[1].split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                            val shop = if (text[2] != "null") text[2] else null
                            val product = if (text[3] != "null") text[3] else null
                            val variant = if (text[4] != "null") text[4] else null
                            val category = if (text[5] != "null") text[5] else null
                            val producer = if (text[6] != "null") text[6] else null
                            val price = if (text[7] != "null") text[7].split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() } else null
                            val quantity = if (text[8] != "null") text[8].split(".", ",").let { it[0].plus(it[1].padEnd(3, '0')).toLong() } else null

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
            if (transactions[Pair(data.transactionDate, data.transactionTotalPrice)] == null) {
                transactions.put(
                    Pair(data.transactionDate, data.transactionTotalPrice),
                    transactions.size.toLong() + 1 // id of 0 causes a foreign key constraint fail
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
                if (variants[variant] == null) {
                    variants.put(variant, variants.size.toLong() + 1) // id of 0 causes a foreign key constraint fail
                    variantList.add(
                        ProductVariant(
                            id = variants[variant]!!,
                            productId = products[data.product]!!,
                            name = variant
                        )
                    )
                }
            }

            // handle items
            data.quantity?.let { quantity ->
                data.price?.let { price ->
                    itemList.add(
                        Item(
                            id = itemList.size.toLong() + 1, // id of 0 causes a foreign key constraint fail
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
            onError()
            return
        }

        // read shop csv
        context.contentResolver.openInputStream(shopCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
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
                    onError()
                    return
                }
            }
        }

        onProgressChange(1)

        // read producer csv
        context.contentResolver.openInputStream(producerCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
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
                    onError()
                    return
                }
            }
        }

        onProgressChange(2)

        // read categories csv
        context.contentResolver.openInputStream(categoryCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
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
                    onError()
                    return
                }
            }
        }

        onProgressChange(3)

        // read transactions csv
        context.contentResolver.openInputStream(transactionCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;date;shopId;totalCost") {
                    val transactionCostDivisor = 100

                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong()
                            val date = text[1].toLong()
                            val shopId = if (text[2] != "null") text[2].toLong() else null
                            val totalCost =
                                text[3].split(".", ",")
                                    .let { (it[0].toLong() * transactionCostDivisor) + it[1].padEnd(2, '0').toLong() }

                            transactionList.add(
                                TransactionBasket(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    date = date,
                                    shopId = shopId?.plus(1), // id can be 0 but 0 causes a foreign key constraint fail
                                    totalCost = totalCost
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    onError()
                    return
                }
            }
        }

        onProgressChange(4)

        // read products csv
        context.contentResolver.openInputStream(productCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
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
                    onError()
                    return
                }
            }
        }

        onProgressChange(5)

        // read variants csv
        context.contentResolver.openInputStream(variantCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
                return
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Get headers from first line
                val headers = reader.readLine()

                // From newest to oldest
                // File version 1.0, @since v2.5.0
                if (headers == "id;productId;name") {
                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(";")

                            val id = text[0].toLong()
                            val productId = text[1].toLong()
                            val name = text[2]

                            variantList.add(
                                ProductVariant(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    productId = productId + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    name = name
                                )
                            )
                        }
                    }
                } else {
                    // Failed to determine file version
                    onError()
                    return
                }
            }
        }

        onProgressChange(6)

        // read items csv
        context.contentResolver.openInputStream(itemCsvUri).use { inputStream ->
            if (inputStream == null) {
                onError()
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

                    reader.forEachLine {
                        if (it.isNotBlank()) {
                            val text = it.split(';')

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

                            itemList.add(
                                Item(
                                    id = id + 1, // id can be 0 but 0 causes a foreign key constraint fail
                                    transactionBasketId = transactionBasketId + 1, // id can be 0 but 0 causes a foreign key constraint fail
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
                    onError()
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
            items = itemList
        )

        onProgressChange(8)

        onFinished()

        return
    }

    // did not find expected files if we are here

    onError()

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
    onError: () -> Unit,
) {
    // Get the documents
    var exportDocument: DocumentInfo? = null

    documents.forEach { documentInfo ->
        if (documentInfo.displayName == "export.json") exportDocument = documentInfo
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

    if (exportDocument != null) {
        onMaxProgressChange(2)

        val jsonUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, exportDocument.documentId)
        if (jsonUri == null) {
            onError()
            return
        }

        context.contentResolver.openInputStream(jsonUri).use { inputStream ->
            if (inputStream == null) {
                onError()
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
                    val transactionItems = mutableListOf<Item>()

                    while (reader.hasNext()) {
                        val name = reader.nextName()

                        if (name == "id") {
                            id = reader.nextLong()
                        } else if (name == "date") {
                            date = reader.nextLong()
                        } else if (name == "cost") {
                            cost = reader.nextString().split(".", ",").let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                        } else if (name == "shop") {
                            if (reader.peek() == JsonToken.NULL) {
                                reader.skipValue()
                            } else {
                                reader.beginObject()

                                var shopName: String? = null

                                while (reader.hasNext()) {
                                    val shopValueName = reader.nextName()

                                    if (shopValueName == "id") {
                                        transactionShopId = reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                    } else if (shopValueName == "name") {
                                        shopName = reader.nextString()
                                    } else {
                                        reader.skipValue()
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
                                    onError()
                                    return
                                }

                                reader.endObject()
                            }
                        } else if (name == "items") {
                            if (reader.peek() == JsonToken.NULL) {
                                reader.skipValue()
                            } else {
                                reader.beginArray()

                                while (reader.hasNext()) {
                                    reader.beginObject()

                                    var itemId: Long? = null
                                    var itemPrice: Long? = null
                                    var itemQuantity: Long? = null
                                    var itemProductId: Long? = null

                                    var itemVariant: ProductVariant? = null

                                    while (reader.hasNext()) {
                                        val itemValueName = reader.nextName()

                                        if (itemValueName == "id") {
                                            itemId =
                                                reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                        } else if (itemValueName == "price") {
                                            itemPrice =
                                                reader.nextString()
                                                    .split(".", ",")
                                                    .let { it[0].plus(it[1].padEnd(2, '0')).toLong() }
                                        } else if (itemValueName == "quantity") {
                                            itemQuantity =
                                                reader.nextString()
                                                    .split(".", ",")
                                                    .let { it[0].plus(it[1].padEnd(3, '0')).toLong() }
                                        } else if (itemValueName == "product") {
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

                                                        if (categoryValueName == "id") {
                                                            productCategoryId =
                                                                reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                        } else if (categoryValueName == "name") {
                                                            categoryName = reader.nextString()
                                                        } else {
                                                            reader.skipValue()
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
                                                        onError()
                                                        return
                                                    }

                                                    reader.endObject()
                                                } else if (productValueName == "producer") {
                                                    if (reader.peek() == JsonToken.NULL) {
                                                        reader.skipValue()
                                                    } else {
                                                        reader.beginObject()

                                                        var producerName: String? = null

                                                        while (reader.hasNext()) {
                                                            val producerValueName = reader.nextName()

                                                            if (producerValueName == "id") {
                                                                productProducerId =
                                                                    reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                            } else if (producerValueName == "name") {
                                                                producerName = reader.nextString()
                                                            } else {
                                                                reader.skipValue()
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
                                                            onError()
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
                                                onError()
                                                return
                                            }

                                            reader.endObject()
                                        } else if (itemValueName == "variant") {
                                            if (reader.peek() == JsonToken.NULL) {
                                                reader.skipValue()
                                            } else {
                                                reader.beginObject()

                                                var variantId: Long? = null
                                                var variantName: String? = null

                                                while (reader.hasNext()) {
                                                    val variantValueName = reader.nextName()

                                                    if (variantValueName == "id") {
                                                        variantId =
                                                            reader.nextLong() + 1 // id of 0 causes a foreign key constraint fail
                                                    } else if (variantValueName == "name") {
                                                        variantName = reader.nextString()
                                                    } else {
                                                        reader.skipValue()
                                                    }
                                                }

                                                // From newest to oldest
                                                // File version 1.0, @since v2.5.0
                                                if (
                                                    variantId != null
                                                    && variantName != null
                                                ) {
                                                    if (variants.add(variantId)) {
                                                        itemVariant = ProductVariant(
                                                            id = variantId,
                                                            productId = -1, // temporarily assume -1 as product id could technically not be set
                                                            name = variantName,
                                                        )
                                                    }
                                                } else {
                                                    // Failed to determine file version
                                                    onError()
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
                                        itemId != null
                                        && itemPrice != null
                                        && itemQuantity != null
                                        && itemProductId != null
                                    ) {
                                        if (items.add(itemId)) {
                                            itemVariant?.let { // update id because now productId has to be set
                                                it.productId = itemProductId
                                                variantList.add(it) // also add to list
                                            }

                                            transactionItems.add(
                                                Item(
                                                    id = itemId,
                                                    transactionBasketId = -1, // temporarily assume -1 as transaction id could technically not be set
                                                    price = itemPrice,
                                                    quantity = itemQuantity,
                                                    productId = itemProductId,
                                                    variantId = itemVariant?.id
                                                )
                                            )
                                        }
                                    } else {
                                        // Failed to determine file version
                                        onError()
                                        return
                                    }

                                    reader.endObject()
                                }

                                reader.endArray()
                            }
                        } else {
                            reader.skipValue()
                        }
                    }


                    if (
                        id != null
                        && date != null
                        && cost != null
                    ) {
                        if (transactions.add(id)) {
                            transactionItems.forEach {
                                it.transactionBasketId = id
                            }

                            itemList.addAll(transactionItems)

                            transactionList.add(
                                TransactionBasket(
                                    id = id,
                                    date = date,
                                    shopId = transactionShopId,
                                    totalCost = cost
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
                    items = itemList
                )

                onProgressChange(2)

                onFinished()

                return
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
                return
            }
            finally {
                reader.close()
            }
        }
    }

    // did not find expected files if we are here

    onError()

    return
}
