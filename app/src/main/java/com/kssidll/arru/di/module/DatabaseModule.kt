package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.data.dao.ExportDao
import com.kssidll.arru.data.dao.ImportDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.database.downloadsAppDirectory
import com.kssidll.arru.data.database.downloadsDbFile
import com.kssidll.arru.data.database.externalDbFile
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getDatabaseLocation
import com.kssidll.arru.data.repository.ExportRepository
import com.kssidll.arru.data.repository.ExportRepositorySource
import com.kssidll.arru.data.repository.ImportRepository
import com.kssidll.arru.data.repository.ImportRepositorySource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        var databaseLocation: AppPreferences.Database.Location.Values
        runBlocking { databaseLocation = AppPreferences.getDatabaseLocation(context).first() }

        return when (databaseLocation) {
            AppPreferences.Database.Location.Values.EXTERNAL -> {
                AppDatabase.buildExternal(context, context.externalDbFile().absolutePath)
            }

            AppPreferences.Database.Location.Values.INTERNAL -> {
                AppDatabase.buildInternal(context)
            }

            AppPreferences.Database.Location.Values.DOWNLOADS -> {
                val downloadsAppDirectory = context.downloadsAppDirectory()

                // create in case it doesn't exist
                downloadsAppDirectory.mkdir()

                AppDatabase.buildExternal(context, context.downloadsDbFile().absolutePath)
            }
        }
    }

    @Provides
    @Singleton
    fun provideExportDao(appDatabase: AppDatabase): ExportDao {
        return appDatabase.getExportDao()
    }

    @Provides
    @Singleton
    fun provideExportRepository(dao: ExportDao): ExportRepositorySource {
        return ExportRepository(dao)
    }

    @Provides
    @Singleton
    fun provideImportDao(appDatabase: AppDatabase): ImportDao {
        return appDatabase.getImportDao()
    }

    @Provides
    @Singleton
    fun provideImportRepository(dao: ImportDao): ImportRepositorySource {
        return ImportRepository(dao)
    }
}
