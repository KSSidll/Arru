package com.kssidll.arru.data.preference

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kssidll.arru.R
import com.kssidll.arru.di.module.dataStore
import com.kssidll.arru.di.module.getPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

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

    /**
     * Data associated with export datastore preferences
     */
    data object Export {
        /**
         * Data associated with export type datastore preference
         */
        data object Type {
            /**
             * Key for export type preference key-value pair
             */
            val key: Preferences.Key<Int> = intPreferencesKey("exporttype")

            /**
             * Value for default export type
             */
            val DEFAULT = Values.CompactCSV

            enum class Values {
                /**
                 * Value for compact csv export
                 */
                CompactCSV,

                /**
                 * Value for raw csv export
                 */
                RawCSV,

                /**
                 * Value for json export
                 */
                JSON,

                ;

                @Composable
                @ReadOnlyComposable
                fun getTranslation(): String {
                    return when (this) {
                        CompactCSV -> stringResource(R.string.export_compact_csv)
                        RawCSV -> stringResource(R.string.export_raw_csv)
                        JSON -> stringResource(R.string.export_json)
                    }
                }

                companion object {
                    private val idMap = Values.entries.associateBy { it.ordinal }
                    fun getByOrdinal(ordinal: Int) = idMap[ordinal]
                }
            }
        }

        /**
         * Data associated with export location datastore preference
         */
        data object Location {
            /**
             * Key for export location preference key-value pair
             */
            val key: Preferences.Key<String> = stringPreferencesKey("exportlocation")
        }
    }

    /**
     * Data associated with theme datastore preferences
     */
    data object Theme {
        /**
         * Data associated with theme color scheme datastore preference
         */
        data object ColorScheme {
            /**
             * Key for theme color scheme preference key-value pair
             */
            val key: Preferences.Key<Int> = intPreferencesKey("themecolorscheme")

            /**
             * Value for default color scheme
             */
            val DEFAULT = Values.SYSTEM


            enum class Values {
                /**
                 * Value for system color scheme
                 */
                SYSTEM,

                /**
                 * Value for dark color scheme
                 */
                DARK,

                /**
                 * Value for light color scheme
                 */
                LIGHT

                ;

                @Composable
                @ReadOnlyComposable
                fun getTranslation(): String {
                    return when (this) {
                        SYSTEM -> stringResource(R.string.system)
                        DARK -> stringResource(R.string.dark)
                        LIGHT -> stringResource(R.string.light)
                    }
                }

                companion object {
                    private val idMap = Values.entries.associateBy { it.ordinal }
                    fun getByOrdinal(ordinal: Int) = idMap[ordinal]
                }
            }
        }

        /**
         * Data associated with dynamic color datastore preference
         */
        data object DynamicColor {
            /**
             * Key for dynamic color preference key-value pair
             */
            @RequiresApi(Build.VERSION_CODES.S)
            val key: Preferences.Key<Boolean> = booleanPreferencesKey("themedynamiccolor")

            /**
             * Default value for dynamic color
             */
            val DEFAULT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }
    }

    data object Locale {
        data object Currency {
            val key: Preferences.Key<String> = stringPreferencesKey("localecurrency")

            const val DEFAULT = "DEFAULT"
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
suspend fun AppPreferences.setTransactionDate(
    context: Context,
    newDatePreference: AppPreferences.Transaction.Date.Values
) {
    context.dataStore.edit {
        it[AppPreferences.Transaction.Date.key] = newDatePreference.ordinal
    }
}

/**
 * Returns the transaction date preference value
 * @param context App context
 * @return The transaction date preference value
 */
fun AppPreferences.getTransactionDate(context: Context): Flow<AppPreferences.Transaction.Date.Values> {
    return getPreferencesDataStore(context).data.map { preferences ->
        preferences[AppPreferences.Transaction.Date.key]?.let {
            AppPreferences.Transaction.Date.Values.getByOrdinal(
                it
            )
        }
            ?: AppPreferences.Transaction.Date.DEFAULT
    }
}

/**
 * Sets the export type preference to a new value
 * @param context App context
 * @param newExportType Value to set the export type preference to
 */
suspend fun AppPreferences.setExportType(
    context: Context,
    newExportType: AppPreferences.Export.Type.Values
) {
    getPreferencesDataStore(context).edit {
        it[AppPreferences.Export.Type.key] = newExportType.ordinal
    }
}

/**
 * Returns the export type preference value
 * @param context App context
 * @return The export type preference value
 */
fun AppPreferences.getExportType(context: Context): Flow<AppPreferences.Export.Type.Values> {
    return getPreferencesDataStore(context).data.map { preferences ->
        preferences[AppPreferences.Export.Type.key]?.let {
            AppPreferences.Export.Type.Values.getByOrdinal(
                it
            )
        }
            ?: AppPreferences.Export.Type.DEFAULT
    }
}

/**
 * Sets the export location preference to a new value
 * @param context App context
 * @param newExportLocation Value to set the export location preference to
 */
suspend fun AppPreferences.setExportLocation(
    context: Context,
    newExportLocation: Uri
) {
    getPreferencesDataStore(context).edit {
        it[AppPreferences.Export.Location.key] = newExportLocation.toString()
    }
}

/**
 * Returns the export location preference value if any
 * @param context App context
 * @return The export location preference value if any
 */
fun AppPreferences.getExportLocation(context: Context): Flow<Uri?> {
    return getPreferencesDataStore(context).data.map { preferences ->
        preferences[AppPreferences.Export.Location.key]?.let {
            Uri.parse(it)
        }
    }
}

/**
 * Sets the theme color scheme preference to a new value
 * @param context App context
 * @param newColorScheme Value to set the theme color scheme preference to
 */
suspend fun AppPreferences.setThemeColorScheme(
    context: Context,
    newColorScheme: AppPreferences.Theme.ColorScheme.Values
) {
    getPreferencesDataStore(context).edit {
        it[AppPreferences.Theme.ColorScheme.key] = newColorScheme.ordinal
    }
}

/**
 * Returns the theme color scheme preference value
 * @param context App context
 * @return The color scheme preference value
 */
fun AppPreferences.getColorScheme(context: Context): Flow<AppPreferences.Theme.ColorScheme.Values> {
    return getPreferencesDataStore(context).data.map { preferences ->
        preferences[AppPreferences.Theme.ColorScheme.key]?.let {
            AppPreferences.Theme.ColorScheme.Values.getByOrdinal(it)
        }
            ?: AppPreferences.Theme.ColorScheme.DEFAULT
    }
}

fun AppPreferences.Theme.ColorScheme.Values.detectDarkMode(): (Resources) -> Boolean =
    { resources ->
        when (this) {
            AppPreferences.Theme.ColorScheme.Values.SYSTEM -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            AppPreferences.Theme.ColorScheme.Values.DARK -> true
            AppPreferences.Theme.ColorScheme.Values.LIGHT -> false
        }
    }

/**
 * Sets the theme dynamic color preference to a new value
 * @param context App context
 * @param isDynamicColor Value to set the theme dynamic color preference to
 */
suspend fun AppPreferences.setThemeDynamicColor(
    context: Context,
    isDynamicColor: Boolean
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getPreferencesDataStore(context).edit {
            it[AppPreferences.Theme.DynamicColor.key] = isDynamicColor
        }
    }
}

/**
 * Returns the theme dynamic color preference value
 * @param context App context
 * @return The dynamic color preference value
 */
fun AppPreferences.getDynamicColor(context: Context): Flow<Boolean> {
    return getPreferencesDataStore(context).data.map { preferences ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            preferences[AppPreferences.Theme.DynamicColor.key]
                ?: AppPreferences.Theme.DynamicColor.DEFAULT
        } else false
    }
}

suspend fun AppPreferences.setCurrencyFormatLocale(
    context: Context,
    newCurrencyLocale: Locale?
) {
    getPreferencesDataStore(context).edit {
        if (newCurrencyLocale == null) {
            it[AppPreferences.Locale.Currency.key] = AppPreferences.Locale.Currency.DEFAULT
        } else {
            it[AppPreferences.Locale.Currency.key] = newCurrencyLocale.toLanguageTag()
        }
    }
}

fun AppPreferences.getCurrencyFormatLocale(context: Context): Flow<Locale> {
    return getPreferencesDataStore(context).data.map { preferences ->
        preferences[AppPreferences.Locale.Currency.key]?.let {
            if (it == AppPreferences.Locale.Currency.DEFAULT) {
                Locale.getDefault()
            } else {
                Locale.forLanguageTag(it)
            }
        }
            ?: Locale.getDefault()
    }
}