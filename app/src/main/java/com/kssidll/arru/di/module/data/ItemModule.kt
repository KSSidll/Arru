package com.kssidll.arru.di.module.data

import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.domain.usecase.data.GetItemEntityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ItemModule {

    /** ENTITY */
    @Provides
    @Singleton
    fun provideGetItemEntityUseCase(
        itemRepositorySource: ItemRepositorySource
    ): GetItemEntityUseCase {
        return GetItemEntityUseCase(itemRepositorySource)
    }

    /** DOMAIN */

    // @Provides
    // @Singleton
    // fun provideGetItemUseCase(
    //     getItemEntityUseCase: GetItemEntityUseCase
    // ): GetItemUseCase {
    //     return GetItemUseCase(getItemEntityUseCase)
    // }
}
