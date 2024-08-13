package com.kssidll.arru.data.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kssidll.arru.di.module.dataStore

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
         * Data associated with database location datastore preference
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

    /**
     * Data associated with transaction datastore preferences
     */
    data object Transaction {
        /**
         * Data associated with transaction date datastore preference
         */
        data object Date {
            /**
             * Key for transaction date preference key-value pair
             */
            val key: Preferences.Key<Int> = intPreferencesKey("transactiondate")

            /**
             * Value for default date
             */
            val DEFAULT = Values.CURRENT

            enum class Values {
                /**
                 * Value for current date
                 */
                CURRENT,

                /**
                 * Value for last transaction date
                 */
                LAST,

                ;
                companion object {
                    private val idMap = Values.entries.associateBy { it.ordinal }
                    fun getByOrdinal(ordinal: Int) = idMap[ordinal]
                }
            }
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

    if (this[AppPreferences.Transaction.Date.key] == null) {
        context.dataStore.edit {
            it[AppPreferences.Transaction.Date.key] = AppPreferences.Transaction.Date.DEFAULT.ordinal
        }
    }
}

/**
 * Resets the preferences that should be reset on startup
 * @param context App context
 */
suspend fun AppPreferences.setResettableToDefault(context: Context) {
    setTransactionDate(context, AppPreferences.Transaction.Date.DEFAULT)
}

/**
 * Sets the transaction date preference to a new value
 * @param context App context
 * @param newDatePreference Value to set the date preference to
 */
suspend fun AppPreferences.setTransactionDate(context: Context, newDatePreference: AppPreferences.Transaction.Date.Values) {
    context.dataStore.edit {
        it[AppPreferences.Transaction.Date.key] = newDatePreference.ordinal
    }
}

/**
 * Returns the transaction date preference value
 * @return The transaction date preference value
 */
fun Preferences.getTransactionDate(): AppPreferences.Transaction.Date.Values {
    return this[AppPreferences.Transaction.Date.key]?.let { AppPreferences.Transaction.Date.Values.getByOrdinal(it) } ?: AppPreferences.Transaction.Date.DEFAULT
}
