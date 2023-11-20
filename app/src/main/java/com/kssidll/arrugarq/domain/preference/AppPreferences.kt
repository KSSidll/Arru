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
             * Key for database location preference key-value pair
             */
            val key: Preferences.Key<String> = stringPreferencesKey("databaselocation")

            /**
             * Value for internal location
             */
            const val INTERNAL: String = "internal"

            /**
             * Value for external location
             */
            const val EXTERNAL: String = "external"

            /**
             * Value for default location
             */
            const val DEFAULT: String = EXTERNAL
        }
    }
}

/**
 * Sets preferences that aren't set to their default value
 *
 * Should be called before any preference is read
 * @param context App context
 */
suspend fun Preferences.setNullToDefault(context: Context) {
    if (this[AppPreferences.Database.Location.key] == null) {
        context.dataStore.edit {
            it[AppPreferences.Database.Location.key] = AppPreferences.Database.Location.DEFAULT
        }
    }
}