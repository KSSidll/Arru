package com.kssidll.arru.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kssidll.arru.data.preference.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by
    preferencesDataStore(name = AppPreferences.DATASTORENAME)

fun getPreferencesDataStore(context: Context): DataStore<Preferences> {
    return context.dataStore
}

suspend fun getPreferences(context: Context): Preferences {
    return getPreferencesDataStore(context).data.first()
}

suspend fun Preferences.from(context: Context): Preferences {
    return getPreferences(context)
}

suspend fun Context.preferences(): Preferences {
    return getPreferences(this)
}

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return getPreferencesDataStore(context)
    }

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): Preferences {
        return runBlocking {
            return@runBlocking getPreferences(context)
        }
    }
}
