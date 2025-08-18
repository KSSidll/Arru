package com.kssidll.arru.domain.usecase

import android.content.Context
import android.net.Uri
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getExportType
import com.kssidll.arru.service.DataExportService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ExportDataWithServiceUseCase(
    @param:ApplicationContext val appContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke(uri: Uri) =
        withContext(dispatcher) {
            when (AppPreferences.getExportType(appContext).first()) {
                AppPreferences.Export.Type.Values.CompactCSV -> {
                    DataExportService.startExportCsvCompact(appContext, uri)
                }

                AppPreferences.Export.Type.Values.RawCSV -> {
                    DataExportService.startExportCsvRaw(appContext, uri)
                }

                AppPreferences.Export.Type.Values.JSON -> {
                    DataExportService.startExportJson(appContext, uri)
                }
            }
        }
}
