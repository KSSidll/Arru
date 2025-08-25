package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.data.dao.BackupDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.BackupRepository
import com.kssidll.arru.data.repository.BackupRepositorySource
import com.kssidll.arru.domain.usecase.data.CreateBackupUseCase
import com.kssidll.arru.domain.usecase.data.DeleteBackupUseCase
import com.kssidll.arru.domain.usecase.data.GetBackupsUseCase
import com.kssidll.arru.domain.usecase.data.LoadBackupUseCase
import com.kssidll.arru.domain.usecase.data.ToggleBackupLockUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BackupModule {
    @Provides
    @Singleton
    fun provideBackupDao(appDatabase: AppDatabase): BackupDao {
        return appDatabase.getBackupDao()
    }

    @Provides
    @Singleton
    fun provideBackupRepository(dao: BackupDao): BackupRepositorySource {
        return BackupRepository(dao)
    }

    @Provides
    @Singleton
    fun provideGetBackupsUseCase(@ApplicationContext context: Context): GetBackupsUseCase {
        return GetBackupsUseCase(context)
    }

    @Provides
    @Singleton
    fun provideCreateBackupUseCase(
        @ApplicationContext context: Context,
        backupRepositorySource: BackupRepositorySource,
    ): CreateBackupUseCase {
        return CreateBackupUseCase(context, backupRepositorySource)
    }

    @Provides
    @Singleton
    fun provideDeleteBackupUseCase(): DeleteBackupUseCase {
        return DeleteBackupUseCase()
    }

    @Provides
    @Singleton
    fun provideToggleBackupLockUseCase(): ToggleBackupLockUseCase {
        return ToggleBackupLockUseCase()
    }

    @Provides
    @Singleton
    fun provideLoadBackupUseCase(@ApplicationContext context: Context): LoadBackupUseCase {
        return LoadBackupUseCase(context)
    }
}
