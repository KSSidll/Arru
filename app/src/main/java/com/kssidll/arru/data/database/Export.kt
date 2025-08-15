package com.kssidll.arru.data.database

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.JsonWriter
import androidx.core.provider.DocumentsContractCompat
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.asCsvList
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.data.repository.VariantRepositorySource
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Function that exports raw data from the database to csv files in the [uri] as separate files
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataAsRawCsv(
    context: Context,
    uri: Uri,
    categoryRepository: CategoryRepositorySource,
    itemRepository: ItemRepositorySource,
    producerRepository: ProducerRepositorySource,
    productRepository: ProductRepositorySource,
    shopRepository: ShopRepositorySource,
    transactionRepository: TransactionBasketRepositorySource,
    variantRepository: VariantRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    batchSize: Int = 5000,
) {
    var maxProgress = 0
    var progress = 0

    val addMaxProgress: (Int) -> Unit = {
        maxProgress += it
        onMaxProgressChange(maxProgress)
    }

    val addProgress: (Int) -> Unit = {
        progress += it
        onProgressChange(progress)
    }

    addMaxProgress(categoryRepository.totalCount())
    addMaxProgress(itemRepository.totalCount())
    addMaxProgress(producerRepository.totalCount())
    addMaxProgress(productRepository.totalCount())
    addMaxProgress(shopRepository.totalCount())
    addMaxProgress(transactionRepository.totalCount())
    addMaxProgress(variantRepository.totalCount())

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat(
        "dd-MM-yyyy-HH-mm-ss",
        Locale.US
    ).format(timeNow)

    val exportDirUri = DocumentsContractCompat.createDocument(
        context.contentResolver, uri, DocumentsContract.Document.MIME_TYPE_DIR, "arru-export-raw-csv-${timeFormatted}"
    )!!

    val categoryCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-category.csv"
    )!!

    val itemCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-item.csv"
    )!!

    val producerCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-producer.csv"
    )!!

    val productCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-product.csv"
    )!!

    val shopCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-shop.csv"
    )!!

    val transactionCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-transaction.csv"
    )!!

    val variantCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export-variant.csv"
    )!!

    context.contentResolver.openFileDescriptor(categoryCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductCategoryEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = categoryRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    context.contentResolver.openFileDescriptor(itemCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ItemEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = itemRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    context.contentResolver.openFileDescriptor(producerCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductProducerEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = producerRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    context.contentResolver.openFileDescriptor(productCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = productRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    context.contentResolver.openFileDescriptor(shopCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ShopEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = shopRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    context.contentResolver.openFileDescriptor(transactionCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    TransactionEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = transactionRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    context.contentResolver.openFileDescriptor(variantCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductVariantEntity.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = variantRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )
                        .asCsvList()

                    outputStream.write("\n".toByteArray())
                    data.forEach { csvData ->
                        outputStream.write(csvData.toByteArray())
                    }

                    offset += batchSize
                    addProgress(data.size)
                } while (data.isNotEmpty())
            }
        }

    onFinished()
}

/**
 * Function that exports compact data from the database to csv file in the [uri] as single file
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataAsCompactCsv(
    context: Context,
    uri: Uri,
    categoryRepository: CategoryRepositorySource,
    itemRepository: ItemRepositorySource,
    producerRepository: ProducerRepositorySource,
    productRepository: ProductRepositorySource,
    shopRepository: ShopRepositorySource,
    transactionRepository: TransactionBasketRepositorySource,
    variantRepository: VariantRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    batchSize: Int = 20,
) {
    var maxProgress = 0
    var progress = 0

    val addMaxProgress: (Int) -> Unit = {
        maxProgress += it
        onMaxProgressChange(maxProgress)
    }

    val addProgress: (Int) -> Unit = {
        progress += it
        onProgressChange(progress)
    }

    addMaxProgress(transactionRepository.totalCount())

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat(
        "dd-MM-yyyy-HH-mm-ss",
        Locale.US
    ).format(timeNow)

    val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
        uri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val exportDirUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        parentUri,
        DocumentsContract.Document.MIME_TYPE_DIR,
        "arru-export-compact-csv-${timeFormatted}"
    )!!

    val exportCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "text/csv", "arru-export.csv"
    )!!

    context.contentResolver.openFileDescriptor(exportCsvFileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    "transactionId;transactionDate;transactionTotalPrice;shop;product;variant;variantGlobal;category;producer;price;quantity;transactionNote\n".toByteArray()
                )

                var offset = 0
                do {
                    val transactions = transactionRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )

                    transactions.forEach { transactionData ->
                        val shop =
                            transactionData.shopEntityId?.let { shopRepository.get(it) }?.name ?: "null"
                        val items = itemRepository.getByTransaction(transactionData.id)

                        if (items.isEmpty()) {
                            outputStream.write(
                                "${transactionData.id};${transactionData.date};${transactionData.actualTotalCost()};${shop};null;null;${false};null;null;null;null;${transactionData.note?.replace(';', ',')?.trim()}\n".toByteArray()
                            )
                        } else {
                            items.forEach { item ->
                                val product = productRepository.get(item.productId)
                                val category =
                                    product?.categoryId?.let { categoryRepository.get(it) }?.name
                                        ?: "null"
                                val producer =
                                    product?.producerId?.let { producerRepository.get(it) }?.name
                                        ?: "null"
                                val variant =
                                    item.variantId?.let { variantRepository.get(it)?.name }
                                        ?: "null"
                                val variantGlobal = item.variantId?.let { variantRepository.get(it)?.productId == null }
                                    ?: false

                                outputStream.write(
                                    "${transactionData.id};${transactionData.date};${transactionData.actualTotalCost()};${shop};${product?.name ?: "null"};${variant};${variantGlobal};${category};${producer};${item.actualPrice()};${item.actualQuantity()};${transactionData.note?.replace(';', ',')?.trim()}\n".toByteArray()
                                )
                            }
                        }
                    }

                    offset += batchSize
                    addProgress(transactions.size)
                } while (transactions.isNotEmpty())
            }
        }

    onFinished()
}

/**
 * Function that exports compact data from the database to json file in the [uri] as single file
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataAsJson(
    context: Context,
    uri: Uri,
    categoryRepository: CategoryRepositorySource,
    itemRepository: ItemRepositorySource,
    producerRepository: ProducerRepositorySource,
    productRepository: ProductRepositorySource,
    shopRepository: ShopRepositorySource,
    transactionRepository: TransactionBasketRepositorySource,
    variantRepository: VariantRepositorySource,
    onMaxProgressChange: (newMaxProgress: Int) -> Unit,
    onProgressChange: (newProgress: Int) -> Unit,
    onFinished: () -> Unit,
    batchSize: Int = 20,
) {
    var maxProgress = 0
    var progress = 0

    val addMaxProgress: (Int) -> Unit = {
        maxProgress += it
        onMaxProgressChange(maxProgress)
    }

    val addProgress: (Int) -> Unit = {
        progress += it
        onProgressChange(progress)
    }

    addMaxProgress(transactionRepository.totalCount())

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat(
        "dd-MM-yyyy-HH-mm-ss",
        Locale.US
    ).format(timeNow)

    val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
        uri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val exportDirUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        parentUri,
        DocumentsContract.Document.MIME_TYPE_DIR,
        "arru-export-json-${timeFormatted}"
    )!!

    val exportJsonFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver, exportDirUri, "application/json", "arru-export.json"
    )!!

    context.contentResolver.openOutputStream(exportJsonFileUri, "w")?.use { outputStream ->
            JsonWriter(OutputStreamWriter(outputStream)).use { writer ->
                writer.setIndent("  ")
                writer.beginArray()

                var offset = 0
                do {
                    val transactions = transactionRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )

                    transactions.forEach { transactionData ->
                        val shop =
                            transactionData.shopEntityId?.let { shopRepository.get(it) }
                        val items = itemRepository.getByTransaction(transactionData.id)

                        writer.beginObject()
                        writer.name("id").value(transactionData.id)
                        writer.name("date").value(transactionData.date)
                        writer.name("cost").value(transactionData.actualTotalCost())
                        writer.name("note").value(transactionData.note)

                        writer.name("shop")
                        if (shop == null) writer.nullValue()
                        else {
                            writer.beginObject()
                            writer.name("id").value(shop.id)
                            writer.name("name").value(shop.name)
                            writer.endObject()
                        }

                        writer.name("items")
                        if (items.isEmpty()) {
                            writer.nullValue()
                        } else {
                            writer.beginArray()
                            items.forEach { item ->
                                val product = productRepository.get(item.productId)
                                val category =
                                    product?.categoryId?.let { categoryRepository.get(it) }
                                val producer =
                                    product?.producerId?.let { producerRepository.get(it) }
                                val variant = item.variantId?.let { variantRepository.get(it) }

                                writer.beginObject()

                                writer.name("id").value(item.id)
                                writer.name("price").value(item.actualPrice())
                                writer.name("quantity").value(item.actualQuantity())

                                writer.name("product")
                                if (product == null) writer.nullValue()
                                else {
                                    writer.beginObject()
                                    writer.name("id").value(product.id)
                                    writer.name("name").value(product.name)

                                    writer.name("category")
                                    if (category == null) writer.nullValue()
                                    else {
                                        writer.beginObject()
                                        writer.name("id").value(category.id)
                                        writer.name("name").value(category.name)
                                        writer.endObject()
                                    }

                                    writer.name("producer")
                                    if (producer == null) writer.nullValue()
                                    else {
                                        writer.beginObject()
                                        writer.name("id").value(producer.id)
                                        writer.name("name").value(producer.name)
                                        writer.endObject()
                                    }

                                    writer.endObject()
                                }

                                writer.name("variant")
                                if (variant == null) writer.nullValue()
                                else {
                                    writer.beginObject()
                                    writer.name("id").value(variant.id)
                                    writer.name("name").value(variant.name)
                                    writer.name("global").value(variant.productId == null)
                                    writer.endObject()
                                }

                                writer.endObject()
                            }
                            writer.endArray()
                        }

                        writer.endObject()
                    }

                    offset += batchSize
                    addProgress(transactions.size)
                } while (transactions.isNotEmpty())

                writer.endArray()
            }
        }

    onFinished()
}
