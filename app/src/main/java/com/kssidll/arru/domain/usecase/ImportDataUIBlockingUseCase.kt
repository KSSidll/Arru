package com.kssidll.arru.domain.usecase

import android.content.Context
import android.net.Uri
import com.kssidll.arru.data.database.ImportError
import com.kssidll.arru.data.database.importDataFromUris
import com.kssidll.arru.data.repository.ImportRepositorySource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportDataUIBlockingUseCase(
    @param:ApplicationContext val appContext: Context,
    private val importRepository: ImportRepositorySource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(
        uri: Uri,
        onMaxProgressChange: (newMaxProgress: Int) -> Unit,
        onProgressChange: (newProgress: Int) -> Unit,
        onFinished: () -> Unit,
        onError: (error: ImportError) -> Unit,
    ) = withContext(dispatcher) {
        importDataFromUris(
            context = appContext,
            uri = uri,
            importRepository = importRepository,
            onMaxProgressChange = onMaxProgressChange,
            onProgressChange = onProgressChange,
            onFinished = onFinished,
            onError = onError
        )
    }
}