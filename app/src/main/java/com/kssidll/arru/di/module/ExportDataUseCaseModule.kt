package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.data.repository.ExportRepositorySource
import com.kssidll.arru.domain.usecase.ExportDataUIBlockingUseCase
import com.kssidll.arru.domain.usecase.ExportDataWithServiceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ExportDataUseCaseModule {
    @Provides
    @Singleton
    fun provideExportDataWithServiceUseCase(
        @ApplicationContext context: Context
    ): ExportDataWithServiceUseCase {
        return ExportDataWithServiceUseCase(context)
    }

    @Provides
    @Singleton
    fun provideExportDataUIBlockingUseCase(
        @ApplicationContext context: Context,
        exportRepository: ExportRepositorySource,
    ): ExportDataUIBlockingUseCase {
        return ExportDataUIBlockingUseCase(context, exportRepository)
    }
}
