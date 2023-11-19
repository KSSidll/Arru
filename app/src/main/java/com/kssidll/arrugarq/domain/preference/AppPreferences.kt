package com.kssidll.arrugarq.domain.preference

import android.content.*
import androidx.datastore.preferences.core.*
import com.kssidll.arrugarq.di.module.*

/**
 * Data associated with datastore preferences
 */
data object AppPreferences {
    const val DATASTORENAME: String = "settings"

    /**
     * Data associated with database datastore preferences
     */
    data object Database {
        /**
         * Data associated with database location datastore preferonce
         */
        data object Location {
            /**
             * key for database location preference key-value pair
             */
            val key: Preferences.Key<String> = stringPreferencesKey("databaselocation")

            /**
             * value for internal location
             */
            const val INTERNAL: String = "internal"

            /**
             * value for external location
             */
            const val EXTERNAL: String = "external"

            /**
             * value for default location
             */
            const val DEFAULT: String = EXTERNAL
        }
    }
}

/**
 * sets preferences that aren't set to their default value
 *
 * should be called before any preference is read
 * @param context app context
 */
suspend fun Preferences.setNullToDefault(context: Context) {
    if (this[AppPreferences.Database.Location.key] == null) {
        context.dataStore.edit {
            it[AppPreferences.Database.Location.key] = AppPreferences.Database.Location.DEFAULT
        }
    }
}