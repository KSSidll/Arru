package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.database.AppDatabase
import com.kssidll.arru.data.repository.TransactionRepository
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.usecase.data.GetAllTransactionsUseCase
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
        transactionRepository: TransactionRepositorySource
    ): GetTransactionEntityUseCase {
        return GetTransactionEntityUseCase(transactionRepository)
    }

    /** DOMAIN */
    @Provides
    @Singleton
    fun provideGetTransactionUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetTransactionUseCase {
        return GetTransactionUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllTransactionsUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetAllTransactionsUseCase {
        return GetAllTransactionsUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetTotalSpentUseCase {
        return GetTotalSpentUseCase(transactionRepository)
    }

    /** DOMAIN CHART */
    @Provides
    @Singleton
    fun provideGetTotalSpentByDayUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetTotalSpentByDayUseCase {
        return GetTotalSpentByDayUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetTotalSpentByWeekUseCase {
        return GetTotalSpentByWeekUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetTotalSpentByMonthUseCase {
        return GetTotalSpentByMonthUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearUseCase(
        transactionRepository: TransactionRepositorySource
    ): GetTotalSpentByYearUseCase {
        return GetTotalSpentByYearUseCase(transactionRepository)
    }
}
