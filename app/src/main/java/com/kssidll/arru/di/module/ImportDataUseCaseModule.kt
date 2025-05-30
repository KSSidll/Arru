package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.data.repository.ImportRepositorySource
import com.kssidll.arru.domain.usecase.ImportDataUIBlockingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImportDataUseCaseModule {
    @Provides
    @Singleton
    fun provideImportDataUIBlockingUseCase(
        @ApplicationContext context: Context,
        importRepository: ImportRepositorySource,
    ): ImportDataUIBlockingUseCase {
        return ImportDataUIBlockingUseCase(
            context,
            importRepository
        )
    }
}