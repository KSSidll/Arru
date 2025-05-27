package com.kssidll.arru.domain.usecase

import android.content.Context
import androidx.annotation.RequiresApi
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.setDatabaseLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class DatabaseMoveResult {
    SUCCESS,
    FAILED,
}

class ChangeDatabaseLocationUseCase(
    @ApplicationContext val appContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    @RequiresApi(30)
    suspend operator fun invoke(
        newDatabaseLocation: AppPreferences.Database.Location.Values
    ) = withContext(dispatcher) {
        val setLocation = AppPreferences.setDatabaseLocation(appContext, newDatabaseLocation)

        if (setLocation != newDatabaseLocation) {
            return@withContext DatabaseMoveResult.FAILED
        }

        return@withContext DatabaseMoveResult.SUCCESS
    }
}