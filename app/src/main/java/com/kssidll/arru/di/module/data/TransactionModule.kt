package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.TransactionRepository
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentUseCase
import com.kssidll.arru.domain.usecase.data.GetTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TransactionModule {

    @Provides
    @Singleton
    fun provideTransactionEntityDao(appDatabase: AppDatabase): TransactionEntityDao {
        return appDatabase.getTransactionEntityDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionEntityDao): TransactionRepositorySource {
        return TransactionRepository(dao)
    }

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetTransactionEntityUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTransactionEntityUseCase {
        return GetTransactionEntityUseCase(transactionRepositorySource)
    }

    /** DOMAIN */
    @Provides
    @Singleton
    fun provideGetTransactionUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTransactionUseCase {
        return GetTransactionUseCase(transactionRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTotalSpentUseCase {
        return GetTotalSpentUseCase(transactionRepositorySource)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTotalSpentByDayUseCase {
        return GetTotalSpentByDayUseCase(transactionRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTotalSpentByWeekUseCase {
        return GetTotalSpentByWeekUseCase(transactionRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTotalSpentByMonthUseCase {
        return GetTotalSpentByMonthUseCase(transactionRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearUseCase(
        transactionRepositorySource: TransactionRepositorySource
    ): GetTotalSpentByYearUseCase {
        return GetTotalSpentByYearUseCase(transactionRepositorySource)
    }
}
