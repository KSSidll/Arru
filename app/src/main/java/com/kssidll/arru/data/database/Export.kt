package com.kssidll.arru.data.database

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.provider.DocumentsContractCompat
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.asCsvList
import com.kssidll.arru.data.repository.ItemRepositorySource
import java.io.FileOutputStream

/**
 * Function that exports data from the database to csv files in the [uri]
 * @param context application context
 * @param uri location to save the data in
 * @param onMaxProgressChange event called when the total export amount changes. Provides new total export amount as parameter
 * @param onProgressChange event called when the export progress changes. Provides new progress value as parameter
 * @param onFinished event called when the export finishes
 * @param batchSize batch size of a single export operation
 */
suspend fun exportDataToCsv(
    context: Context,
    uri: Uri,
    itemRepository: ItemRepositorySource,
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

    addMaxProgress(itemRepository.totalCount())

    val parentUri = DocumentsContractCompat.buildDocumentUriUsingTree(
        uri,
        DocumentsContractCompat.getTreeDocumentId(uri)!!
    )!!

    val itemCsvFileUri = DocumentsContractCompat.createDocument(
        context.contentResolver,
        parentUri,
        "text/csv",
        "item.csv"
    )!!

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

    onFinished()
}