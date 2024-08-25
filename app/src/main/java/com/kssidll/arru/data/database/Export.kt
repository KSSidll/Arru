package com.kssidll.arru.data.database

import android.content.Context
import android.content.Intent
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
import com.kssidll.arru.data.data.TransactionBasketItem
import com.kssidll.arru.data.data.asCsvList
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.data.repository.VariantRepositorySource
import java.io.FileOutputStream
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
    context.contentResolver.takePersistableUriPermission(
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )

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

    val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
        uri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat(
        "dd-MM-yyyy-HH-mm-ss",
        Locale.US
    ).format(timeNow)

    val exportDirUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        parentUri,
        DocumentsContract.Document.MIME_TYPE_DIR,
        "export-raw-csv-${timeFormatted}"
    )!!

    val categoryCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "category.csv"
    )!!

    val itemCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "item.csv"
    )!!

    val producerCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "producer.csv"
    )!!

    val productCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "product.csv"
    )!!

    val shopCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "shop.csv"
    )!!

    val transactionCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "transaction.csv"
    )!!

    val transactionItemCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "transaction-item.csv"
    )!!

    val variantCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "variant.csv"
    )!!

    context.contentResolver.openFileDescriptor(
        categoryCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductCategory.csvHeaders()
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

    context.contentResolver.openFileDescriptor(
        itemCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    Item.csvHeaders()
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

    context.contentResolver.openFileDescriptor(
        producerCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductProducer.csvHeaders()
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

    context.contentResolver.openFileDescriptor(
        productCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    Product.csvHeaders()
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

    context.contentResolver.openFileDescriptor(
        shopCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    Shop.csvHeaders()
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

    context.contentResolver.openFileDescriptor(
        transactionCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    TransactionBasket.csvHeaders()
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

    context.contentResolver.openFileDescriptor(
        transactionItemCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    TransactionBasketItem.csvHeaders()
                        .toByteArray()
                )

                var offset = 0
                do {
                    val data = transactionRepository.getPagedItemList(
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

    context.contentResolver.openFileDescriptor(
        variantCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    ProductVariant.csvHeaders()
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
 * Function that exports compact data from the database to csv files in the [uri] as single file
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
    context.contentResolver.takePersistableUriPermission(
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )

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

    val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
        uri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val timeNow = Calendar.getInstance().time

    val timeFormatted = SimpleDateFormat(
        "dd-MM-yyyy-HH-mm-ss",
        Locale.US
    ).format(timeNow)

    val exportDirUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        parentUri,
        DocumentsContract.Document.MIME_TYPE_DIR,
        "export-compact-csv-${timeFormatted}"
    )!!

    val exportCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        exportDirUri,
        "text/csv",
        "export.csv"
    )!!

    context.contentResolver.openFileDescriptor(
        exportCsvFileUri,
        "w"
    )
        ?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                outputStream.write(
                    "transactionDate;transactionTotalPrice;shop;product;variant;category;producer;price;quantity".toByteArray()
                )

                var offset = 0
                do {
                    val transactions = transactionRepository.getPagedList(
                        limit = batchSize,
                        offset = offset
                    )

                    outputStream.write("\n".toByteArray())
                    transactions.forEach { transactionData ->
                        val shop =
                            transactionData.shopId?.let { shopRepository.get(it) }?.name ?: "null"
                        val items = itemRepository.getByTransaction(transactionData.id)

                        if (items.isEmpty()) {
                            outputStream.write(
                                "${transactionData.date};${transactionData.actualTotalCost()};${shop};null;null;null;null;null;null\n".toByteArray()
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

                                outputStream.write(
                                    "${transactionData.date};${transactionData.actualTotalCost()};${shop};${product?.name ?: "null"};${variant};${category};${producer};${item.actualPrice()};${item.actualQuantity()}\n".toByteArray()
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
