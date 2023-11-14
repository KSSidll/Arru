package com.kssidll.arrugarq.domain.preference

import androidx.datastore.preferences.core.*

/**
 * Data associated with datastore preferences
 */
data object AppPreferences {
    const val DATASTORENAME: String = "settings"

    /**
     * Data associated with DatabaseLocation datastore preference
     */
    data object Database {
        val key: Preferences.Key<String> = stringPreferencesKey("databaselocation")

        data object Location {
            const val INTERNAL: String = "internal"
            const val EXTERNAL: String = "external"
        }
    }
}