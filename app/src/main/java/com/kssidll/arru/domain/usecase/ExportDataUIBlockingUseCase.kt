package com.kssidll.arru.domain.usecase

import android.content.Context
import android.net.Uri
import com.kssidll.arru.data.database.exportDataAsCompactCsv
import com.kssidll.arru.data.database.exportDataAsJson
import com.kssidll.arru.data.database.exportDataAsRawCsv
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getExportType
import com.kssidll.arru.data.repository.ExportRepositorySource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ExportDataUIBlockingUseCase(
    @param:ApplicationContext val appContext: Context,
    private val exportRepository: ExportRepositorySource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(
        uri: Uri,
        onMaxProgressChange: (newMaxProgress: Int) -> Unit,
        onProgressChange: (newProgress: Int) -> Unit,
        onFinished: () -> Unit,
    ) =
        withContext(dispatcher) {
            when (AppPreferences.getExportType(appContext).first()) {
                AppPreferences.Export.Type.Values.CompactCSV -> {
                    exportDataAsCompactCsv(
                        appContext,
                        uri,
                        exportRepository,
                        onMaxProgressChange,
                        onProgressChange,
                        onFinished,
                    )
                }

                AppPreferences.Export.Type.Values.RawCSV -> {
                    exportDataAsRawCsv(
                        appContext,
                        uri,
                        exportRepository,
                        onMaxProgressChange,
                        onProgressChange,
                        onFinished,
                    )
                }

                AppPreferences.Export.Type.Values.JSON -> {
                    exportDataAsJson(
                        appContext,
                        uri,
                        exportRepository,
                        onMaxProgressChange,
                        onProgressChange,
                        onFinished,
                    )
                }
            }
        }
}
