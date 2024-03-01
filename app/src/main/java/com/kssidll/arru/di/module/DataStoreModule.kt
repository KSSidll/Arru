package com.kssidll.arru.di.module

import android.content.*
import androidx.datastore.core.*
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import com.kssidll.arru.data.preference.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.qualifiers.*
import dagger.hilt.components.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppPreferences.DATASTORENAME)

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    fun providePreferences(dataStore: DataStore<Preferences>): Preferences {
        return runBlocking {
            return@runBlocking dataStore.data.first()
        }
    }
}