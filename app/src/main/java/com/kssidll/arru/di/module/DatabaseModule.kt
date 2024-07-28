package com.kssidll.arru.di.module

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.kssidll.arru.Arru
import com.kssidll.arru.data.dao.ItemDao
import com.kssidll.arru.data.dao.TransactionDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.database.externalDbFile
import com.kssidll.arru.data.database.internalDbFile
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.repository.ItemRepository
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.TransactionRepository
import com.kssidll.arru.data.repository.TransactionRepositorySource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        preferences: Preferences
    ): AppDatabase {
        val location = preferences[AppPreferences.Database.Location.key]

        // if it's not internal and doesn't exist, create it so it can be moved
        if (location != AppPreferences.Database.Location.INTERNAL) {
            if (!appContext.internalDbFile()
                    .exists()
            ) {
                // simple query to ensure database creation
                AppDatabase.buildInternal(appContext)
                    .query(
                        "SELECT 1",
                        null
                    )
                    .close()
                Arru.restart(appContext)
            }
        }

        return when (preferences[AppPreferences.Database.Location.key]) {
            AppPreferences.Database.Location.EXTERNAL -> {
                if (!appContext.externalDbFile()
                        .exists()
                ) {
                    AppDatabase.moveInternalToExternal(appContext)
                }

                AppDatabase.buildExternal(appContext)
            }

            AppPreferences.Database.Location.INTERNAL -> {
                if (!appContext.internalDbFile()
                        .exists()
                ) {
                    if (appContext.externalDbFile()
                            .exists()
                    ) {
                        AppDatabase.moveExternalToInternal(appContext)
                    }
                }

                AppDatabase.buildInternal(appContext)
            }

            else -> error("The database location preference key isn't set to a valid value")
        }
    }

    @Provides
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.getTransactionDao()
    }

    @Provides
    fun provideTransactionRepository(transactionBasketDao: TransactionDao): TransactionRepositorySource {
        return TransactionRepository(transactionBasketDao)
    }

    @Provides
    fun provideItemDao(appDatabase: AppDatabase): ItemDao {
        return appDatabase.getItemDao()
    }

    @Provides
    fun provideItemRepository(itemDao: ItemDao): ItemRepositorySource {
        return ItemRepository(itemDao)
    }
}
