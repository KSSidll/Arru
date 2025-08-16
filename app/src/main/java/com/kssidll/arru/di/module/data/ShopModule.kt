package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemsForShopUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
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
    fun provideGetItemsForShopUseCase(
        shopRepositorySource: ShopRepositorySource
    ): GetItemsForShopUseCase {
        return GetItemsForShopUseCase(shopRepositorySource)
    }
}
