package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByDayForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByMonthForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByWeekForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentByYearForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetTotalSpentForShopUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ShopModule {

    /** ENTITY */


    @Provides
    @Singleton
    fun provideGetShopEntityUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetShopEntityUseCase {
        return GetShopEntityUseCase(shopRepositorySource)
    }


    /** DOMAIN */


    // @Provides
    // @Singleton
    // fun provideGetShopUseCase(
    //     getShopEntityUseCase: GetShopEntityUseCase
    // ): GetShopUseCase {
    //     return GetShopUseCase(getShopEntityUseCase)
    // }

    @Provides
    @Singleton
    fun provideGetTotalSpentForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetTotalSpentForShopUseCase {
        return GetTotalSpentForShopUseCase(shopRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetItemsForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetItemsForShopUseCase {
        return GetItemsForShopUseCase(shopRepositorySource)
    }


    /** DOMAIN CHART */


    @Provides
    @Singleton
    fun provideGetTotalSpentByDayForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetTotalSpentByDayForShopUseCase {
        return GetTotalSpentByDayForShopUseCase(shopRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByWeekForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetTotalSpentByWeekForShopUseCase {
        return GetTotalSpentByWeekForShopUseCase(shopRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByMonthForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetTotalSpentByMonthForShopUseCase {
        return GetTotalSpentByMonthForShopUseCase(shopRepositorySource)
    }

    @Provides
    @Singleton
    fun provideGetTotalSpentByYearForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetTotalSpentByYearForShopUseCase {
        return GetTotalSpentByYearForShopUseCase(shopRepositorySource)
    }
}
