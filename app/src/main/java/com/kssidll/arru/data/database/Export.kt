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
import com.kssidll.arru.data.repository.ExportRepositorySource
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Function that exports raw data from the database to csv files in the [uri] as separate files
 *
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total
 *   export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress
 *   value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataAsRawCsv(
    context: Context,
    uri: Uri,
    exportRepository: ExportRepositorySource,
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

    addMaxProgress(exportRepository.shopCount())
    addMaxProgress(exportRepository.transactionCount())
    addMaxProgress(exportRepository.productProducerCount())
    addMaxProgress(exportRepository.productCategoryCount())
    addMaxProgress(exportRepository.productCount())
    addMaxProgress(exportRepository.productVariantCount())
    addMaxProgress(exportRepository.itemCount())

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.US).format(timeNow)

    val exportDirUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            uri,
            DocumentsContract.Document.MIME_TYPE_DIR,
            "arru-export-raw-csv-${timeFormatted}",
        )!!

    val categoryCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-category.csv",
        )!!

    val itemCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-item.csv",
        )!!

    val producerCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-producer.csv",
        )!!

    val productCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-product.csv",
        )!!

    val shopCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-shop.csv",
        )!!

    val transactionCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-transaction.csv",
        )!!

    val variantCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export-variant.csv",
        )!!

    context.contentResolver.openFileDescriptor(categoryCsvFileUri, "w")?.use { parcelFileDescriptor
        ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(ProductCategoryEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository
                        .productCategoryPagedList(limit = batchSize, offset = offset)
                        .asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    context.contentResolver.openFileDescriptor(itemCsvFileUri, "w")?.use { parcelFileDescriptor ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(ItemEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository.itemPagedList(limit = batchSize, offset = offset).asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    context.contentResolver.openFileDescriptor(producerCsvFileUri, "w")?.use { parcelFileDescriptor
        ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(ProductProducerEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository
                        .productProducerPagedList(limit = batchSize, offset = offset)
                        .asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    context.contentResolver.openFileDescriptor(productCsvFileUri, "w")?.use { parcelFileDescriptor
        ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(ProductEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository
                        .productPagedList(limit = batchSize, offset = offset)
                        .asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    context.contentResolver.openFileDescriptor(shopCsvFileUri, "w")?.use { parcelFileDescriptor ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(ShopEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository.shopPagedList(limit = batchSize, offset = offset).asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    context.contentResolver.openFileDescriptor(transactionCsvFileUri, "w")?.use {
        parcelFileDescriptor ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(TransactionEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository
                        .transactionPagedList(limit = batchSize, offset = offset)
                        .asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    context.contentResolver.openFileDescriptor(variantCsvFileUri, "w")?.use { parcelFileDescriptor
        ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(ProductVariantEntity.CSV_HEADERS.toByteArray())

            var offset = 0
            do {
                val data =
                    exportRepository
                        .productVariantPagedList(limit = batchSize, offset = offset)
                        .asCsvList()

                outputStream.write("\n".toByteArray())
                data.forEach { csvData -> outputStream.write(csvData.toByteArray()) }

                offset += batchSize
                addProgress(data.size)
            } while (data.isNotEmpty())
        }
    }

    onFinished()
}

/**
 * Function that exports compact data from the database to csv file in the [uri] as single file
 *
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total
 *   export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress
 *   value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataAsCompactCsv(
    context: Context,
    uri: Uri,
    exportRepository: ExportRepositorySource,
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

    addMaxProgress(exportRepository.transactionCount())

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.US).format(timeNow)

    val parentUri =
        DocumentsContractCompat.buildDocumentUriUsingTree(
            uri,
            DocumentsContractCompat.getTreeDocumentId(uri)!!,
        )!!

    val exportDirUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            parentUri,
            DocumentsContract.Document.MIME_TYPE_DIR,
            "arru-export-compact-csv-${timeFormatted}",
        )!!

    val exportCsvFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "text/csv",
            "arru-export.csv",
        )!!

    context.contentResolver.openFileDescriptor(exportCsvFileUri, "w")?.use { parcelFileDescriptor ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
            outputStream.write(
                "transactionId;transactionDate;transactionTotalPrice;shop;product;variant;variantGlobal;category;producer;price;quantity;transactionNote\n"
                    .toByteArray()
            )

            var offset = 0
            do {
                val transactions =
                    exportRepository.transactionPagedList(limit = batchSize, offset = offset)

                transactions.forEach { transactionData ->
                    val shop =
                        transactionData.shopEntityId?.let { exportRepository.getShop(it) }?.name
                            ?: "null"
                    val items = exportRepository.getItemsByTransaction(transactionData.id)

                    if (items.isEmpty()) {
                        outputStream.write(
                            "${transactionData.id};${transactionData.date};${transactionData.actualTotalCost()};${shop};null;null;${false};null;null;null;null;${transactionData.note?.replace(';', ',')?.trim()}\n"
                                .toByteArray()
                        )
                    } else {
                        items.forEach { item ->
                            val product = exportRepository.getProduct(item.productEntityId)
                            val category =
                                product
                                    ?.productCategoryEntityId
                                    ?.let { exportRepository.getProductCategory(it) }
                                    ?.name ?: "null"
                            val producer =
                                product
                                    ?.productProducerEntityId
                                    ?.let { exportRepository.getProductProducer(it) }
                                    ?.name ?: "null"
                            val variant =
                                item.productVariantEntityId?.let {
                                    exportRepository.getProductVariant(it)?.name
                                } ?: "null"
                            val variantGlobal =
                                item.productVariantEntityId?.let {
                                    exportRepository.getProductVariant(it)?.productEntityId == null
                                } ?: false

                            outputStream.write(
                                "${transactionData.id};${transactionData.date};${transactionData.actualTotalCost()};${shop};${product?.name ?: "null"};${variant};${variantGlobal};${category};${producer};${item.actualPrice()};${item.actualQuantity()};${transactionData.note?.replace(';', ',')?.trim()}\n"
                                    .toByteArray()
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
 *
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total
 *   export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress
 *   value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataAsJson(
    context: Context,
    uri: Uri,
    exportRepository: ExportRepositorySource,
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

    addMaxProgress(exportRepository.transactionCount())

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.US).format(timeNow)

    val parentUri =
        DocumentsContractCompat.buildDocumentUriUsingTree(
            uri,
            DocumentsContractCompat.getTreeDocumentId(uri)!!,
        )!!

    val exportDirUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            parentUri,
            DocumentsContract.Document.MIME_TYPE_DIR,
            "arru-export-json-${timeFormatted}",
        )!!

    val exportJsonFileUri =
        DocumentsContractCompat.createDocument(
            context.contentResolver,
            exportDirUri,
            "application/json",
            "arru-export.json",
        )!!

    context.contentResolver.openOutputStream(exportJsonFileUri, "w")?.use { outputStream ->
        JsonWriter(OutputStreamWriter(outputStream)).use { writer ->
            writer.setIndent("  ")
            writer.beginArray()

            var offset = 0
            do {
                val transactions =
                    exportRepository.transactionPagedList(limit = batchSize, offset = offset)

                transactions.forEach { transactionData ->
                    val shop = transactionData.shopEntityId?.let { exportRepository.getShop(it) }
                    val items = exportRepository.getItemsByTransaction(transactionData.id)

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
                            val product = exportRepository.getProduct(item.productEntityId)
                            val category =
                                product?.productCategoryEntityId?.let {
                                    exportRepository.getProductCategory(it)
                                }
                            val producer =
                                product?.productProducerEntityId?.let {
                                    exportRepository.getProductProducer(it)
                                }
                            val variant =
                                item.productVariantEntityId?.let {
                                    exportRepository.getProductVariant(it)
                                }

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
                                writer.name("global").value(variant.productEntityId == null)
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
