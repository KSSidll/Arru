package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.TransactionRepositorySource
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
        getTransactionEntityUseCase: GetTransactionEntityUseCase
    ): GetTransactionUseCase {
        return GetTransactionUseCase(getTransactionEntityUseCase)
    }
}
