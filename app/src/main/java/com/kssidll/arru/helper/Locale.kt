package com.kssidll.arru.helper

import android.content.Context
import android.content.res.Configuration

/** Helper function to get localized string when getString for some reason doesn't localize it */
fun Context.getLocalizedString(resourseId: Int): String {
    val config = resources.configuration
    val locale = java.util.Locale.getDefault()
    val newConfig = Configuration(config)
    newConfig.setLocale(locale)
    return createConfigurationContext(newConfig).getString(resourseId)
}
