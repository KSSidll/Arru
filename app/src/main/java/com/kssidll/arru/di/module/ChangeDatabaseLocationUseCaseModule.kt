package com.kssidll.arru.di.module

import android.content.Context
import com.kssidll.arru.domain.usecase.ChangeDatabaseLocationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChangeDatabaseLocationUseCaseModule {
    @Provides
    @Singleton
    fun provideChangeDatabaseLocationUseCase(@ApplicationContext context: Context): ChangeDatabaseLocationUseCase {
        return ChangeDatabaseLocationUseCase(context)
    }
}